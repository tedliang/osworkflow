package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.Prefs;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.Workspace;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 1:02:27 AM
 */
public class SaveWorkspace extends AbstractAction implements WorkspaceListener
{
  private Workspace currentWorkspace;

  public SaveWorkspace()
  {
    setEnabled(false);
    putValue(SHORT_DESCRIPTION, "Save current workspace");
    putValue(NAME, "Save workspace");
    putValue(LONG_DESCRIPTION, "Save the currently loaded workspace");
  }

  public void actionPerformed(ActionEvent e)
  {
    if(currentWorkspace.getLocation()==null)
    {
      File toSave = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, true, WorkflowDesigner.WORKSPACE_SUFFIX, "Workspace Files");
      if(toSave!=null)
      {
        currentWorkspace.setLocation(toSave);
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, toSave.toString());
        WorkflowDesigner.INSTANCE.navigator().setWorkspace(currentWorkspace);
      }
      else
      {
        return;
      }
    }
    WorkflowDesigner.INSTANCE.saveWorkspace();
    WorkflowDesigner.INSTANCE.saveOpenGraphs();
  }

  public void workspaceChanged(WorkspaceEvent event)
  {
    if(event.getId()==WorkspaceEvent.WORKSPACE_OPENED)
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
