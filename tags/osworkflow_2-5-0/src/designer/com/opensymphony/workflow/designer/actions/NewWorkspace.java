package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 1:14:32 AM
 */
public class NewWorkspace extends AbstractAction
{
  public NewWorkspace()
  {
    putValue(SHORT_DESCRIPTION, "Create new workspace");
    putValue(NAME, "New workspace");
    putValue(LONG_DESCRIPTION, "Create a new workspace");
  }

  public void actionPerformed(ActionEvent e)
  {
    WorkflowDesigner.INSTANCE.newWorkspace();
  }
}
