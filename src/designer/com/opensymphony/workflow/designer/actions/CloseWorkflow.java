package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * @author hani Date: Apr 25, 2004 Time: 2:05:05 AM
 */
public class CloseWorkflow extends AbstractAction
{
  public void actionPerformed(ActionEvent e)
  {
    WorkflowDesigner.INSTANCE.closeCurrentWorkflow();
  }

}
