package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
/**
 * User: Hani Suleiman
 * Date: Oct 24, 2003
 * Time: 2:29:02 PM
 */
public class CreateStep extends AbstractAction
{
	private WorkflowDescriptor workflow;
  private WorkflowGraphModel model;
  private Point location;

  public CreateStep(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    super("Step");
    this.workflow = workflow;
    this.model = model;
    this.location = location;
  }

  public void actionPerformed(ActionEvent e){
  	CellFactory.createStep(workflow, model, location);
  }
}
