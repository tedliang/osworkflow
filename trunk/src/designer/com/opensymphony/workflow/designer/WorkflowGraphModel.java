package com.opensymphony.workflow.designer;

import java.util.*;
import javax.swing.undo.UndoableEdit;

import com.opensymphony.workflow.designer.event.JoinChangedEvent;
import com.opensymphony.workflow.designer.event.JoinChangedListener;
import com.opensymphony.workflow.designer.proxy.ActionProxy;
import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.*;

public class WorkflowGraphModel extends DefaultGraphModel
{
  private Collection stepCells = new HashSet();
  private Collection splitCells = new HashSet();
  private Collection joinCells = new HashSet();
  private Collection initialActions = new ArrayList();
  private ResultHolderList results = new ResultHolderList();
  private IDGenerator resultIdGenerator = new IDGenerator();
  private Layout layout;

  public WorkflowGraphModel(Layout layout)
  {
    this.layout = layout;
  }

  private JoinCell getJoinCell(int id)
  {
    Iterator iter = joinCells.iterator();
    while(iter.hasNext())
    {
      JoinCell cell = (JoinCell)iter.next();
      if(cell.getJoinDescriptor().getId() == id)
      {
        return cell;
      }
    }
    return null;
  }

  public boolean acceptsTarget(Object edge, Object port)
  {
    if(port==null) return false;
    WorkflowCell cell = (WorkflowCell)((WorkflowPort)port).getParent();
    if(cell instanceof InitialActionCell) return false;
    //todo return true once we have connection stuff working
    return true;
  }

  public boolean acceptsSource(Object edge, Object port)
  {
//    if(port==null) return false;
//    WorkflowCell cell = (WorkflowCell)((WorkflowPort)port).getParent();
    return false;
  }

  public void processJoinChangeEvent(JoinCell cell)
  {
    JoinDescriptor join = cell.getJoinDescriptor();

    List list = join.getConditions();
    for(int i = 0; i < list.size(); i++)
    {
      ConditionDescriptor cond = (ConditionDescriptor)list.get(i);
      if(cond.getType().equals("class"))
      {
        String clazz = (String)cond.getArgs().get("class.name");
        try
        {
          Object obj = Class.forName(clazz).newInstance();
          if(obj instanceof JoinChangedListener)
          {
            JoinChangedEvent event = new JoinChangedEvent(cell, this);
            event.setArgs(cond.getArgs());
            ((JoinChangedListener)obj).joinChanged(event);
            cond.getArgs().putAll(event.getArgs());
          }
        }
        catch(Exception e)
        {
        }
      }
    }
  }

  public void insertInitialActions(List initialActions, InitialActionCell initialActionCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    this.initialActions.add(initialActionCell);
    // TODO:: currently only supports one action
    for(int i = 0; i < initialActions.size() && i < 1; i++)
    {
      // added by jackflit
      if(i == 0)
      {
        initialActionCell.setActionDescriptor((ActionDescriptor)initialActions.get(i));
      }

      ActionDescriptor action = (ActionDescriptor)initialActions.get(i);
      Utils.checkId(action);
      List conResults = action.getConditionalResults();
      recordResults(initialActionCell, conResults, action);
      ResultDescriptor result = action.getUnconditionalResult();
      if(result != null)
      {
        recordResult(initialActionCell, result, action);
      }
      Object[] cells = new Object[]{initialActionCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
    }
  }

  public void insertStepCell(StepCell stepCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    stepCells.add(stepCell);
    Utils.checkId(stepCell.getDescriptor());
    Object[] cells = new Object[]{stepCell};
    // Insert into Model
    insert(cells, attributes, null, pm, edits);
    recordResults(stepCell);
  }

  public void insertSplitCell(SplitCell splitCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    splitCells.add(splitCell);
    Utils.checkId(splitCell.getSplitDescriptor());
    Object[] cells = new Object[]{splitCell};
    // Insert into Model
    insert(cells, attributes, null, pm, edits);
    recordResults(splitCell);
  }

  public void insertJoinCell(JoinCell joinCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    joinCells.add(joinCell);
    Utils.checkId(joinCell.getJoinDescriptor());
    Object[] cells = new Object[]{joinCell};
    // Insert into Model
    insert(cells, attributes, null, pm, edits);
    recordResults(joinCell);
  }

  public void insertResultConnections()
  {
    Iterator steps = stepCells.iterator();
    while(steps.hasNext())
    {
      StepCell stepCell = (StepCell)steps.next();
      processStepEndPointResult(stepCell);
    }
    Iterator splits = splitCells.iterator();
    while(splits.hasNext())
    {
      SplitCell splitCell = (SplitCell)splits.next();
      processSplitEndPointResult(splitCell);
    }
    Iterator joins = joinCells.iterator();
    while(joins.hasNext())
    {
      JoinCell joinCell = (JoinCell)joins.next();
      processJoinEndPointResult(joinCell);
      this.processJoinChangeEvent(joinCell);
    }
  }

  public void recordResults(JoinCell fromCell)
  {
    JoinDescriptor joinDescriptor = fromCell.getJoinDescriptor();
    ResultDescriptor result = joinDescriptor.getResult();
    if(result != null)
    {
      recordResult(fromCell, result, null);
    }
  }

  public List getResultsToJoin(JoinCell joinCell)
  {
    return results.getResultsToJoin(joinCell.getJoinDescriptor().getId());
  }

  private void processJoinEndPointResult(JoinCell joinCell)
  {
    int joinId = joinCell.getJoinDescriptor().getId();
    Iterator results = this.results.getResultsToJoin(joinId).iterator();
    while(results.hasNext())
    {
      ResultHolder result = (ResultHolder)results.next();
      connectCells(result, joinCell);
    }
  }

  private void processSplitEndPointResult(SplitCell splitCell)
  {
    int splitId = splitCell.getSplitDescriptor().getId();
    Iterator results = this.results.getResultsToSplit(splitId).iterator();
    while(results.hasNext())
    {
      ResultHolder result = (ResultHolder)results.next();
      connectCells(result, splitCell);
    }

  }

  public void recordResults(SplitCell fromCell)
  {
    SplitDescriptor splitDescriptor = fromCell.getSplitDescriptor();
    List results = splitDescriptor.getResults();
    recordResults(fromCell, results, null);
  }

  public List getResultsToStep(StepCell stepCell)
  {
    return results.getResultsToStep(stepCell.getDescriptor().getId());
  }

  /**
   * Find Results that have StepCell's associated Step passed in as next Step. Connect all such cells
   */
  private void processStepEndPointResult(StepCell stepCell)
  {
    int stepId = stepCell.getDescriptor().getId();
    Iterator results = this.results.getResultsToStep(stepId).iterator();
    while(results.hasNext())
    {
      ResultHolder result = (ResultHolder)results.next();
      connectCells(result, stepCell);
    }
  }

  public void connectCells(WorkflowCell from, ActionDescriptor action, WorkflowCell to, ResultDescriptor result)
  {
    WorkflowPort fromPort = (WorkflowPort)from.getChildAt(0);
    WorkflowPort toPort = (WorkflowPort)to.getChildAt(0);

    resultIdGenerator.checkId(result.getId());
    if(result.getId() == 0) result.setId(resultIdGenerator.generateId());
    // Create Edge
    ResultEdge edge = new ResultEdge(result, layout!=null ? layout.getLabelPosition(result.getId()) : null);
    edge.setUserObject(action==null ? null : new ActionProxy(action));

    // Connect Edge
    ConnectionSet cs = new ConnectionSet(edge, fromPort, toPort);
    Object[] cells = new Object[]{edge};
    // Insert into Model
    insert(cells, null, cs, null, null);
    toPort.assignIndex(edge);
    fromPort.assignIndex(edge);

    // process join changed event
    if(to instanceof JoinCell)
    {
      processJoinChangeEvent((JoinCell)to);
    }
  }

  /**
   * Connects fromCell contained in resultCell to the toCell passed in.
   */
  public void connectCells(ResultHolder resultCell, DefaultGraphCell toCell)
  {
    WorkflowPort fromPort = (WorkflowPort)resultCell.getFromCell().getChildAt(0);
    WorkflowPort toPort = (WorkflowPort)toCell.getChildAt(0);

    // Create Edge
    ResultDescriptor descriptor = resultCell.getDescriptor();
    resultIdGenerator.checkId(descriptor.getId());
    if(descriptor.getId()==0) descriptor.setId(resultIdGenerator.generateId());
    ResultEdge edge = new ResultEdge(descriptor, layout != null ? layout.getLabelPosition(descriptor.getId()) : null);

    // this is action, why?
    edge.setUserObject(new ActionProxy(resultCell.getAction()));
    ConnectionSet cs = new ConnectionSet(edge, fromPort, toPort);
    Object[] cells = new Object[]{edge};
    // Insert into Model
    insert(cells, null, cs, null, null);
    //toPort.assignIndex(edge);
    //fromPort.assignIndex(edge);
  }

  /**
   * When a Step is inserted, introspect it. find all the actions and add them to the GraphModel.
   * Introspect each action and record results.
   */
  public void recordResults(StepCell fromCell)
  {
    StepDescriptor stepDescriptor = fromCell.getDescriptor();
    List actionList = stepDescriptor.getActions();
    for(int i = 0; i < actionList.size(); i++)
    {
      ActionDescriptor action = (ActionDescriptor)actionList.get(i);
      Utils.checkId(action);
      List conResults = action.getConditionalResults();
      recordResults(fromCell, conResults, action);
      ResultDescriptor result = action.getUnconditionalResult();
      if(result!=null)
        recordResult(fromCell, result, action);
    }
  }

  private void recordResults(WorkflowCell fromCell, List results, ActionDescriptor action)
  {
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      recordResult(fromCell, result, action);
    }
  }

  public ResultHolder recordResult(WorkflowCell fromCell, ResultDescriptor result, ActionDescriptor action)
  {
    Utils.checkId(result);
    ResultHolder newCell = new ResultHolder(fromCell, result, action);
    results.add(newCell);
    return newCell;
  }

  public Collection getActivitiesList()
  {
    List l = new ArrayList();
    l.addAll(initialActions);
    l.addAll(stepCells);
    l.addAll(splitCells);
    l.addAll(joinCells);
    Iterator i = l.iterator();
    Set edges = new HashSet();
    while(i.hasNext())
    {
      WorkflowCell cell = (WorkflowCell)i.next();
      //we know every edge has to start somewhere, so we don't need to check to cells
      Iterator j = edges(cell.getFirstChild());
      while(j.hasNext())
      {
        WorkflowEdge edge = (WorkflowEdge)j.next();
        edges.add(edge);
      }
    }
    l.addAll(edges);
    return l;
  }

  public boolean removeEdge(ResultEdge edge)
  {
    ResultDescriptor result = edge.getDescriptor();

    ResultHolder cell = results.getResultCell(result);
    DefaultGraphCell from = cell.getFromCell();
    if(from instanceof ResultAware)
    {
      // remove descriptor
      ResultAware remove = (ResultAware)from;
      if(!remove.removeResult(result))
      {
        return false;
      }
      Object[] objs = new Object[]{edge};
      // remove edge
      remove(objs);

      // remove result cell

      results.remove(cell);
      //			System.out.println(obj);
    }

    if(result.getJoin() > 0)
    {
      JoinCell join = getJoinCell(result.getJoin());
      if(join != null)
      {
        this.processJoinChangeEvent(join);
      }
      else
      {
        return false;
      }
    }

    return true;
  }

  public boolean removeStep(StepCell cell)
  {
    StepDescriptor step = cell.getDescriptor();

    // remove all edges and result cells
    Set set = getEdges(this, new Object[]{cell});
    Iterator iter = set.iterator();
    while(iter.hasNext())
    {
      Object obj = iter.next();
      if(obj instanceof ResultEdge)
      {
        removeEdge((ResultEdge)obj);
      }
    }

    // remove step descriptor
    WorkflowDescriptor workflow = (WorkflowDescriptor)step.getParent();
    List list = workflow.getSteps();
    list.remove(step);

    // remove cell

    // 1. remove port
    list = cell.getChildren();
    for(int i = 0; i < list.size(); i++)
    {
      remove(new Object[]{list.get(i)});
    }
    // 2. remove cell
    remove(new Object[]{cell});

    // remove step cell from model
    stepCells.remove(cell);

    return true;
  }

  public boolean removeJoin(JoinCell cell)
  {
    JoinDescriptor join = cell.getJoinDescriptor();

    // remove all edges and result cells
    Set set = getEdges(this, new Object[]{cell});
    Iterator iter = set.iterator();
    while(iter.hasNext())
    {
      Object obj = iter.next();
      if(obj instanceof ResultEdge)
      {
        removeEdge((ResultEdge)obj);
      }
    }

    // remove join descriptor
    WorkflowDescriptor workflow = (WorkflowDescriptor)join.getParent();
    List list = workflow.getJoins();
    list.remove(join);

    // remove cell

    // 1. remove port
    list = cell.getChildren();
    for(int i = 0; i < list.size(); i++)
    {
      remove(new Object[]{list.get(i)});
    }
    // 2. remove cell
    remove(new Object[]{cell});

    // remove join cell from model
    joinCells.remove(cell);

    return true;
  }

  public boolean removeSplit(SplitCell cell)
  {

    SplitDescriptor split = cell.getSplitDescriptor();

    // remove all edges and result cells
    Set set = getEdges(this, new Object[]{cell});
    Iterator iter = set.iterator();
    while(iter.hasNext())
    {
      Object obj = iter.next();
      if(obj instanceof ResultEdge)
      {
        removeEdge((ResultEdge)obj);
      }
    }

    // remove split descriptor
    WorkflowDescriptor workflow = (WorkflowDescriptor)split.getParent();
    List list = workflow.getSplits();
    list.remove(split);

    // remove cell

    // 1. remove port
    list = cell.getChildren();
    for(int i = 0; i < list.size(); i++)
    {
      remove(new Object[]{list.get(i)});
    }
    // 2. remove cell
    remove(new Object[]{cell});

    // remove split cell from model
    splitCells.remove(cell);

    return true;
  }


}
