package com.opensymphony.workflow.designer.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.SplitCell;
import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.WorkflowGraph;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * User: Hani Suleiman
 * Date: Nov 24, 2003
 * Time: 1:10:28 PM
 */
public class Delete extends AbstractAction
{
  private WorkflowDescriptor workflow;
  private WorkflowGraph graph;
  private Point location;

  public Delete(WorkflowDescriptor workflow, WorkflowGraph graph, Point location)
  {
    super("Delete");
    this.workflow = workflow;
    this.graph = graph;
    this.location = location;
  }

  public void actionPerformed(ActionEvent e)
  {
    Object cell = graph.getFirstCellForLocation(location.x, location.y);
    if(cell == null)
    {
      return;
    }
    else if(cell instanceof ResultEdge)
    {
      graph.removeEdge((ResultEdge)cell);
    }
    else if(cell instanceof StepCell)
    {
      graph.removeStep((StepCell)cell);
    }
    else if(cell instanceof JoinCell)
    {
      graph.removeJoin((JoinCell)cell);
    }
    else if(cell instanceof SplitCell)
    {
      graph.removeSplit((SplitCell)cell);
    }
  }

}
