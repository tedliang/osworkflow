package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-18
 */
public class CreateJoin extends AbstractAction implements DescriptorAware
{

  private WorkflowDescriptor workflow;
  private WorkflowGraphModel model;
  private Point location;

  public CreateJoin(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    super(ResourceManager.getString("join"));
    this.workflow = workflow;
    this.model = model;
    this.location = location;
  }

  public void actionPerformed(ActionEvent e)
  {
    CellFactory.createJoin(workflow, model, location);
  }

  public void setDescriptor(WorkflowDescriptor descriptor)
  {
    this.workflow = descriptor;
  }
}
