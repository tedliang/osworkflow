package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 12:21:03 AM
 */
public class CloseWorkspace extends AbstractAction implements WorkspaceListener
{
  public CloseWorkspace()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    WorkflowDesigner.INSTANCE.closeWorkspace();
  }

  public void workspaceChanged(WorkspaceEvent event)
  {
    if(event.getId()==WorkspaceEvent.WORKSPACE_OPENED)
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }
}
