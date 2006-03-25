package com.opensymphony.workflow.designer.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-18
 */
public class CreateSplit extends AbstractAction implements DescriptorAware
{

	private WorkflowGraphModel model;
	private Point location;
	private WorkflowDescriptor workflow;

	public CreateSplit(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
	{
		super("Split");
		this.workflow = workflow;		
		this.model = model;
		this.location = location;
	}

	public void actionPerformed(ActionEvent e) 
	{
		CellFactory.createSplit(workflow, model, location);
	}
  
  public void setDescriptor(WorkflowDescriptor descriptor)
  {
    this.workflow = descriptor;
  }
}
