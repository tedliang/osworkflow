package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.WorkflowDesigner;

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
    File file = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, false, WorkflowDesigner.WORKSPACE_SUFFIX, "Workspace Files");
    WorkflowDesigner.INSTANCE.openWorkspace(file);
  }
}
