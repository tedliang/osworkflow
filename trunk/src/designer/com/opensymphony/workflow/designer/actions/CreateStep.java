package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.StepDescriptor;
import org.jgraph.graph.GraphConstants;

/**
 * User: Hani Suleiman
 * Date: Oct 24, 2003
 * Time: 2:29:02 PM
 */
public class CreateStep extends AbstractAction
{
  private WorkflowGraphModel model;
  private Point location;

  public CreateStep(WorkflowGraphModel model, Point location)
  {
    super("Step");
    this.model = model;
    this.location = location;
  }

  public void actionPerformed(ActionEvent e)
  {
    StepDescriptor step = new StepDescriptor();
    String name = JOptionPane.showInputDialog("Step Name?");
    if(name==null || name.trim().length()==0) return;
    step.setName(name);
    step.setId(model.getNextId());
    StepCell cell = new StepCell(step);
    WorkflowPort port = new WorkflowPort();
    cell.add(port);
    Rectangle bounds = (Rectangle)cell.getAttributes().get(GraphConstants.BOUNDS);
    bounds.x = location.x;
    bounds.y = location.y;
    model.insertStepCell(cell, null, null, null);
  }
}
