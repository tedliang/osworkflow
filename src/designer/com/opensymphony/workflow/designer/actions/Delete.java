package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * User: Hani Suleiman
 * Date: Nov 24, 2003
 * Time: 1:10:28 PM
 */
public class Delete extends AbstractAction
{
  private WorkflowGraphModel model;

  public Delete(WorkflowGraphModel model)
  {
    super("Delete");
    this.model = model;
  }

  public void actionPerformed(ActionEvent e)
  {
  }
}
