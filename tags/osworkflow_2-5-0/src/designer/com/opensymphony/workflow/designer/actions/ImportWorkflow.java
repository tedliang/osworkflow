package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.Workspace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 12:55:51 AM
 */
public class ImportWorkflow extends AbstractAction implements WorkspaceListener
{
  private static final Log log = LogFactory.getLog(ImportWorkflow.class);

  private Workspace currentWorkspace;

  public ImportWorkflow()
  {
    setEnabled(false);
    putValue(SHORT_DESCRIPTION, "Import a workflow");
    putValue(NAME, "Import workflow");
    putValue(LONG_DESCRIPTION, "Import a workflow definition file into the current workspace");
  }

  public void actionPerformed(ActionEvent e)
  {
    File file = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, false, ".xml", "XML Workflow Files");
    if(file!=null)
    {
      try
      {
        if(currentWorkspace.getLocation()==null)
        {
          JOptionPane.showMessageDialog((Component)e.getSource(), "Please save this workspace before importing workflows into it", "Warning", JOptionPane.WARNING_MESSAGE);
          return;
        }
        FileOutputStream out = new FileOutputStream(new File(currentWorkspace.getLocation().getParentFile(), file.getName()));
        FileInputStream in = new FileInputStream(file);

        byte[] buff = new byte[4096];
        int nch;
        while((nch = in.read(buff, 0, buff.length)) != -1)
        {
          out.write(buff, 0, nch);
        }
        out.flush();
        out.close();
        String name = file.getName();
        currentWorkspace.importDescriptor(name.substring(0, name.lastIndexOf('.')), new FileInputStream(file));
        WorkflowDesigner.INSTANCE.navigator().setWorkspace(currentWorkspace);
      }
      catch(Exception t)
      {
        log.error("Error importing descriptor", t);
      }
    }
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
