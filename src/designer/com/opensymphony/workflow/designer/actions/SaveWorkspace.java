package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.Workspace;
import com.opensymphony.workflow.loader.WorkflowFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 21, 2003
 *         Time: 1:02:27 AM
 */
public class SaveWorkspace extends AbstractAction implements WorkspaceListener
{
  //private Workspace currentWorkspace;
  private WorkflowFactory currentWorkspace;

  public SaveWorkspace()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    if(currentWorkspace instanceof Workspace)
    {
      Workspace space = (Workspace)currentWorkspace;
      if(space.getLocation() == null)
      {
        File toSave = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, true, WorkflowDesigner.WORKSPACE_SUFFIX, ResourceManager.getString("workspace.files"));
        if(toSave != null)
        {
          space.setLocation(toSave);
          Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, toSave.toString());
          WorkflowDesigner.INSTANCE.navigator().setWorkspace(space);
        }
        else
        {
          return;
        }
      }
    }
    WorkflowDesigner.INSTANCE.saveWorkspace();
    WorkflowDesigner.INSTANCE.saveOpenGraphs();
  }

  public void workspaceChanged(WorkspaceEvent event)
  {
    if(event.getId() == WorkspaceEvent.WORKSPACE_OPENED)
    {
      setEnabled(true);
      currentWorkspace = event.getWorkspace();
    }
    else
    {
      setEnabled(false);
      currentWorkspace = null;
    }

  }
}
