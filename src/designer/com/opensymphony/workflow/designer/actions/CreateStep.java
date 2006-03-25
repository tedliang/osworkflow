package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * User: Hani Suleiman
 * Date: Oct 24, 2003
 * Time: 2:29:02 PM
 */
public class CreateStep extends AbstractAction implements DescriptorAware
{
  private WorkflowDescriptor descriptor;
  private WorkflowGraphModel model;
  private Point location;

  public CreateStep(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    super(ResourceManager.getString("step"));
    this.descriptor = workflow;
    this.model = model;
    this.location = location;
  }

  public void actionPerformed(ActionEvent e)
  {
    CellFactory.createStep(descriptor, model, location);
  }

  public void setDescriptor(WorkflowDescriptor descriptor)
  {
    this.descriptor = descriptor;
  }
}
