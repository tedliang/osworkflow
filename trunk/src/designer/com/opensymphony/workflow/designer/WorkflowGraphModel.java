/**
 * Created on Feb 11, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import java.util.*;
import javax.swing.undo.UndoableEdit;

import org.jgraph.graph.*;
import com.opensymphony.workflow.loader.*;

public class WorkflowGraphModel extends DefaultGraphModel
{
  private Map stepCells = new HashMap();
  private Map splitCells = new HashMap();
  private Map joinCells = new HashMap();
  private Map initialActions = new HashMap();
  private ResultCellCollection resultCells = new ResultCellCollection();

  public void insertInitialActions(List initialActions, InitialActionCell initialActionCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!this.initialActions.containsKey(initialActionCell.getKey()))
    {
      this.initialActions.put(initialActionCell.getKey(), initialActionCell);
      for(int i = 0; i < initialActions.size(); i++)
      {
        ActionDescriptor action = (ActionDescriptor)initialActions.get(i);
        List conResults = action.getConditionalResults();
        recordResults(initialActionCell, conResults);
        ResultDescriptor result = action.getUnconditionalResult();
        recordResult(initialActionCell, result);
        Object[] cells = new Object[]{initialActionCell};
        // Insert into Model
        insert(cells, attributes, null, pm, edits);
      }
    }

  }

  public void insertInitialsActions(List initialActions, InitialActionCell initialActionCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!this.initialActions.containsKey(initialActionCell.getKey()))
    {
      this.initialActions.put(initialActionCell.getKey(), initialActionCell);
      for(int i = 0; i < initialActions.size(); i++)
      {
        //ActionDescriptor action = (ActionDescriptor)initialActions.get(i);
        //List conResults = action.getConditionalResults();
        /*				recordResults(initialActionCell, conResults);
                ResultDescriptor result = action.getUnconditionalResult();
                recordResult(initialActionCell, result);*/
        Object[] cells = new Object[]{initialActionCell};
        // Insert into Model
        insert(cells, attributes, null, pm, edits);
      }
    }

  }

  public void insertStepCell(StepCell stepCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!stepCells.containsKey(stepCell.getKey()))
    {
      stepCells.put(stepCell.getKey(), stepCell);
      Object[] cells = new Object[]{stepCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      recordResults(stepCell);
      processStepEndPointResult(stepCell);
      // stepCell.toString();
    }
  }

  // by raj
  public void insertStep(StepCell stepCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!stepCells.containsKey(stepCell.getKey()))
    {
      stepCells.put(stepCell.getKey(), stepCell);
      Object[] cells = new Object[]{stepCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      // recordResults(stepCell);
      // processStepEndPointResult(stepCell);
      // stepCell.toString();
    }
  }

  public void insertSplitCell(SplitCell splitCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!splitCells.containsKey(splitCell.getKey()))
    {

      splitCells.put(splitCell.getKey(), splitCell);
      Object[] cells = new Object[]{splitCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      recordResults(splitCell);
      processSplitEndPointResult(splitCell);
    }
  }

  public void insertSplit(SplitCell splitCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!splitCells.containsKey(splitCell.getKey()))
    {

      splitCells.put(splitCell.getKey(), splitCell);
      Object[] cells = new Object[]{splitCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      // recordResults(splitCell);
      // processSplitEndPointResult(splitCell);
    }
  }

  public void insertJoinCell(JoinCell joinCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!joinCells.containsKey(joinCell.getKey()))
    {

      joinCells.put(joinCell.getKey(), joinCell);
      Object[] cells = new Object[]{joinCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      recordResults(joinCell);
      processJoinEndPointResult(joinCell);
    }
  }

  public void insertJoin(JoinCell joinCell, Map attributes, ParentMap pm, UndoableEdit[] edits)
  {
    if(!joinCells.containsKey(joinCell.getKey()))
    {
      joinCells.put(joinCell.getKey(), joinCell);
      Object[] cells = new Object[]{joinCell};
      // Insert into Model
      insert(cells, attributes, null, pm, edits);
      //		recordResults(joinCell);
      //		processJoinEndPointResult(joinCell);
    }
  }

  public void insertResultConnections()
  {
    Iterator steps = stepCells.values().iterator();
    while(steps.hasNext())
    {
      StepCell stepCell = (StepCell)steps.next();
      processStepEndPointResult(stepCell);
    }
    Iterator splits = splitCells.values().iterator();
    while(splits.hasNext())
    {
      SplitCell splitCell = (SplitCell)splits.next();
      processSplitEndPointResult(splitCell);
    }
    Iterator joins = joinCells.values().iterator();
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
    recordResult(fromCell, result);
  }

  public void processJoinEndPointResult(JoinCell joinCell)
  {
    int joinId = joinCell.getJoinDescriptor().getId();
    Iterator results = resultCells.getJoinEndPointResults(joinId).iterator();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      connectCells(result, joinCell);
    }
  }

  public void processSplitEndPointResult(SplitCell splitCell)
  {
    int splitId = splitCell.getSplitDescriptor().getId();
    Iterator results = resultCells.getSplitEndPointResults(splitId).iterator();
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
    recordResults(fromCell, results);
    //      for (int i = 0; i < actionList.size(); i++) {
    //         ActionDescriptor action = (ActionDescriptor) actionList.get(i);
    //         List conResults = action.getConditionalResults();
    //         recordResults(fromCell, conResults);
    //         ResultDescriptor result = action.getUnconditionalResult();
    //         recordResult(fromCell, result);
    //      }

  }

  /**
   * Find Results that have StepCell's associated Step passed in as next Step. Connect all such cells
   */
  public void processStepEndPointResult(StepCell stepCell)
  {
    int stepId = stepCell.getDescriptor().getId();
    Iterator results = resultCells.getStepEndPointResults(stepId).iterator();
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
    DefaultPort fromPort = new DefaultPort();
    resultCell.getFromCell().add(fromPort);
    DefaultPort toPort = new DefaultPort();
    toCell.add(toPort);
    // Create Edge
    ResultEdge edge = new ResultEdge();
    edge.setDescriptor(resultCell.getDescriptor());
    // Create Edge Attributes
    Map edgeAttrib = GraphConstants.createMap();
    // Set Arrow
    int arrow = GraphConstants.ARROW_CLASSIC;
    GraphConstants.setLineEnd(edgeAttrib, arrow);
    GraphConstants.setEndFill(edgeAttrib, true);
    // Connect Edge
    ConnectionSet cs = new ConnectionSet(edge, fromPort, toPort);
    Object[] cells = new Object[]{edge};
    // Insert into Model
    attributeMap.put(edge, edgeAttrib);
    insert(cells, attributeMap, cs, null, null);
  }

  /**
   *
   * When a Step is inserted, introspect it. find all the actions and add them to the GraphModel.
   * Introspect each action and record results.
   *
   */
  public void recordResults(StepCell fromCell)
  {
    StepDescriptor stepDescriptor = fromCell.getDescriptor();
    List actionList = stepDescriptor.getActions();
    for(int i = 0; i < actionList.size(); i++)
    {
      ActionDescriptor action = (ActionDescriptor)actionList.get(i);
      List conResults = action.getConditionalResults();
      recordResults(fromCell, conResults);
      ResultDescriptor result = action.getUnconditionalResult();
      recordResult(fromCell, result);
    }
  }

  private void recordResults(DefaultGraphCell fromCell, List results)
  {
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      recordResult(fromCell, result);
    }
  }

  private void recordResult(DefaultGraphCell fromCell, ResultDescriptor result)
  {
    String key = resultCells.getNextKey();
    ResultCell newCell = new ResultCell(fromCell, result);
    resultCells.put(key, newCell);
  }

  public Map getActivitiesList()
  {
    Map map = new HashMap();
    map.putAll(initialActions);
    map.putAll(stepCells);
    map.putAll(splitCells);
    map.putAll(joinCells);
    return map;
  }

  public Map getStep()
  {
    return stepCells;
  }

  /**
   * Method getSplit.
   * @return Map
   */
  public Map getSplit()
  {
    return splitCells;
  }

  public Map getJoin()
  {
    return joinCells;
  }

  public Map getInitialsAction()
  {
    return initialActions;
  }

}
