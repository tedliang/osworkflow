package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.dialogs.ImportWorkflowDialog;
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
  }

  public void actionPerformed(ActionEvent e)
  {
    if(currentWorkspace.getLocation() == null)
    {
      JOptionPane.showMessageDialog((Component)e.getSource(), ResourceManager.getString("error.save.unsavedspace"), ResourceManager.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
      return;
    }
    ImportWorkflowDialog dialog = new ImportWorkflowDialog(WorkflowDesigner.INSTANCE, (String)getValue(NAME), true);
    dialog.pack();
    Utils.centerComponent(WorkflowDesigner.INSTANCE, dialog);
    dialog.show();
    URL inputFile = dialog.getImportURL();
    if(inputFile!=null)
    {
      String f = inputFile.getFile();
      String fileName = f.substring(f.lastIndexOf('/')+1, f.length());
      try
      {
        File ouputFile = new File(currentWorkspace.getLocation().getParentFile(), fileName);
        //don't allow importing of files within the workspace!
        if(!"file".equals(inputFile.getProtocol()) ||
          new File(inputFile.getFile()).getCanonicalPath().indexOf(currentWorkspace.getLocation().getParentFile().getCanonicalPath())==-1)
        {
          FileOutputStream out = new FileOutputStream(ouputFile);
          InputStream in = inputFile.openStream();

          byte[] buff = new byte[4096];
          int nch;
          while((nch = in.read(buff, 0, buff.length)) != -1)
          {
            out.write(buff, 0, nch);
          }
          out.flush();
          out.close();
          currentWorkspace.importDescriptor(fileName.substring(0, fileName.lastIndexOf('.')), inputFile.openStream());
          WorkflowDesigner.INSTANCE.navigator().setWorkspace(currentWorkspace);
        }
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
