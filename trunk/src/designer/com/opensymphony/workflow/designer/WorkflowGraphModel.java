/**
 * Created on Feb 11, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import java.util.*;
import javax.swing.undo.UndoableEdit;

import com.opensymphony.workflow.designer.views.EdgeRouter;
import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.*;

public class WorkflowGraphModel extends DefaultGraphModel
{
  private Collection stepCells = new HashSet();
  private Collection splitCells = new HashSet();
  private Collection joinCells = new HashSet();
  private Collection initialActions = new ArrayList();
  private ResultCellCollection resultCells = new ResultCellCollection();
  private static final EdgeRouter EDGE_ROUTER = new EdgeRouter();
  private int nextId = 0;

  public void insertInitialActions(List initialActions, InitialActionCell initialActionCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    this.initialActions.add(initialActionCell);
    if(initialActionCell.getChildCount()==0)
    {
      initialActionCell.add(new WorkflowPort());
    }
    for(int i = 0; i < initialActions.size(); i++)
    {
      ActionDescriptor action = (ActionDescriptor)initialActions.get(i);
      checkId(action);
      List conResults = action.getConditionalResults();
      recordResults(initialActionCell, conResults, action);
      ResultDescriptor result = action.getUnconditionalResult();
      if(result!=null)
      {
        checkId(result);
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
    checkId(stepCell.getDescriptor());
    Object[] cells = new Object[]{stepCell};
    // Insert into Model
    insert(cells, attributes, null, pm, edits);
    recordResults(stepCell);
  }

  public void insertSplitCell(SplitCell splitCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    splitCells.add(splitCell);
    checkId(splitCell.getSplitDescriptor());
    Object[] cells = new Object[]{splitCell};
    // Insert into Model
    insert(cells, attributes, null, pm, edits);
    recordResults(splitCell);
  }

  public void insertJoinCell(JoinCell joinCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    joinCells.add(joinCell);
    checkId(joinCell.getJoinDescriptor());
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
    }
  }

  public void recordResults(JoinCell fromCell)
  {
    JoinDescriptor joinDescriptor = fromCell.getJoinDescriptor();
    ResultDescriptor result = joinDescriptor.getResult();
    recordResult(fromCell, result, null);
  }

  private void processJoinEndPointResult(JoinCell joinCell)
  {
    int joinId = joinCell.getJoinDescriptor().getId();
    Iterator results = resultCells.getResultsToJoin(joinId).iterator();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      connectCells(result, joinCell);
    }
  }

  private void processSplitEndPointResult(SplitCell splitCell)
  {
    int splitId = splitCell.getSplitDescriptor().getId();
    Iterator results = resultCells.getResultsToSplit(splitId).iterator();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      connectCells(result, splitCell);
    }

  }

  public void recordResults(SplitCell fromCell)
  {
    SplitDescriptor splitDescriptor = fromCell.getSplitDescriptor();
    List results = splitDescriptor.getResults();
    recordResults(fromCell, results, null);
  }

  /**
   * Find Results that have StepCell's associated Step passed in as next Step. Connect all such cells
   */
  private void processStepEndPointResult(StepCell stepCell)
  {
    int stepId = stepCell.getDescriptor().getId();
    Iterator results = resultCells.getResultsToStep(stepId).iterator();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      connectCells(result, stepCell);
    }
  }

  /**
   * Connects fromCell contained in resultCell to the toCell passed in.
   */
  public void connectCells(ResultCell resultCell, DefaultGraphCell toCell)
  {
    Map attributeMap = new HashMap();
    WorkflowPort fromPort;
    if(resultCell.getFromCell().getChildCount() > 0)
    {
      fromPort = (WorkflowPort)resultCell.getFromCell().getChildAt(0);
    }
    else
    {
      fromPort = new WorkflowPort();
      resultCell.getFromCell().add(fromPort);
    }
    WorkflowPort toPort;
    if(toCell.getChildCount() > 0)
    {
      toPort = (WorkflowPort)toCell.getChildAt(0);
    }
    else
    {
      toPort = new WorkflowPort();
      toCell.add(toPort);
    }

    // Create Edge
    ResultEdge edge = new ResultEdge();
    //edge.setSource(fromPort);
//    edge.setTarget(toPort);
    edge.setUserObject(resultCell.getUserObject());
    edge.setDescriptor(resultCell.getDescriptor());
    // Create Edge Attributes
    Map edgeAttrib = GraphConstants.createMap();
    // Set Arrow
    int arrow = GraphConstants.ARROW_CLASSIC;
    GraphConstants.setLineEnd(edgeAttrib, arrow);
    GraphConstants.setEndFill(edgeAttrib, true);
    GraphConstants.setDisconnectable(edgeAttrib, false);
    GraphConstants.setRouting(edgeAttrib, EDGE_ROUTER);
    // Connect Edge
    ConnectionSet cs = new ConnectionSet(edge, fromPort, toPort);
    Object[] cells = new Object[]{edge};
    // Insert into Model
    attributeMap.put(edge, edgeAttrib);
    insert(cells, attributeMap, cs, null, null);
    toPort.assignIndex(edge);
    fromPort.assignIndex(edge);
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
      checkId(action);
      List conResults = action.getConditionalResults();
      recordResults(fromCell, conResults, action);
      ResultDescriptor result = action.getUnconditionalResult();
      recordResult(fromCell, result, action);
    }
  }

  private void checkId(AbstractDescriptor descriptor)
  {
    if(descriptor==null) return;
    if(descriptor.getId()>=nextId) nextId = descriptor.getId() + 1;
  }

  private void recordResults(DefaultGraphCell fromCell, List results, ActionDescriptor action)
  {
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      recordResult(fromCell, result, action);
    }
  }

  public ResultCell recordResult(DefaultGraphCell fromCell, ResultDescriptor result, ActionDescriptor action)
  {
    String key = resultCells.getNextKey();
    ResultCell newCell = new ResultCell(fromCell, result, action);
    resultCells.put(key, newCell);
    return newCell;
  }

  public Collection getActivitiesList()
  {
    List l = new ArrayList();
    l.addAll(initialActions);
    l.addAll(stepCells);
    l.addAll(splitCells);
    l.addAll(joinCells);
    return l;
  }

  public int getNextId()
  {
    return nextId;
  }
}
