package com.opensymphony.workflow.designer.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * User: Hani Suleiman
 * Date: Nov 24, 2003
 * Time: 1:10:28 PM
 */
public class Delete extends AbstractAction implements DescriptorAware
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
			// update the graph
      graph.removeEdge((ResultEdge)cell);
      WorkflowDesigner.INSTANCE.navigator().reloadWorkflow(workflow);      
    }
    else if(cell instanceof StepCell)
    {
      graph.removeStep((StepCell)cell);
			//WorkflowDesigner.INSTANCE.navigator().reloadSteps(workflow);
			WorkflowDesigner.INSTANCE.navigator().reloadWorkflow(workflow);
    }
    else if(cell instanceof JoinCell)
    {
      graph.removeJoin((JoinCell)cell);
			//WorkflowDesigner.INSTANCE.navigator().reloadJoins(workflow);
			WorkflowDesigner.INSTANCE.navigator().reloadWorkflow(workflow);
    }
    else if(cell instanceof SplitCell)
    {
      graph.removeSplit((SplitCell)cell);
			//WorkflowDesigner.INSTANCE.navigator().reloadSplits(workflow);
			WorkflowDesigner.INSTANCE.navigator().reloadWorkflow(workflow);
    }
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, workflow);
  }

  public void setDescriptor(WorkflowDescriptor descriptor)
  {
    this.workflow = descriptor;
  }
}
