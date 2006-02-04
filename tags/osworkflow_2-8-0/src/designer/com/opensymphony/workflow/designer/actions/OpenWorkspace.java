package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*;

import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 12:09:41 AM
 */
public class OpenWorkspace extends AbstractAction
{
  public OpenWorkspace()
  {
  }

  public void actionPerformed(ActionEvent e)
  {
    File file = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, false, WorkflowDesigner.WORKSPACE_SUFFIX, ResourceManager.getString("workspace.files"));
    if(file == null) return;
	  try
	  {
		  WorkflowDesigner.INSTANCE.openWorkspace(file.toURL());
	  }
	  catch(MalformedURLException e1)
	  {
		  e1.printStackTrace();
	  }
  }
}
