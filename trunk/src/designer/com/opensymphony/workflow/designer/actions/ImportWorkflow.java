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
import com.opensymphony.workflow.designer.swing.status.StatusDisplay;
import com.opensymphony.workflow.designer.dialogs.ImportWorkflowDialog;
import com.opensymphony.workflow.loader.Workspace;
import com.opensymphony.workflow.FactoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import foxtrot.Worker;
import foxtrot.Task;

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
    dialog.getBanner().setTitle("");
    dialog.getBanner().setSubtitle(ResourceManager.getString("import.long"));
    Utils.centerComponent(WorkflowDesigner.INSTANCE, dialog);
    if(!dialog.ask()) return;
    importURL(dialog.getImportURL());
  }

  public void importURL(final URL url)
  {
    if(url==null) return;
    StatusDisplay status = (StatusDisplay)WorkflowDesigner.INSTANCE.statusBar.getItemByName("Status");
    String f = url.getFile();
    if(!"file".equals(url.getProtocol()) && f.indexOf('?')>-1) f = f.substring(0, f.indexOf('?'));
    String fileName = f.substring(f.lastIndexOf('/')+1, f.length());
    try
    {
      final File outputFile = new File(currentWorkspace.getLocation().getParentFile(), fileName);
      if(outputFile.isDirectory()) throw new Exception("Output file is a directory!");
      //don't allow importing of files within the workspace!
      if(!"file".equals(url.getProtocol()) ||
        !new File(url.getFile()).getParentFile().getCanonicalPath().equals(currentWorkspace.getLocation().getParentFile().getCanonicalPath()))
      {
        status.setProgressStatus(ResourceManager.getString("import.progress", new Object[]{fileName}));
        status.setIndeterminate(true);
        Worker.post(new Task()
        {
          public Object run() throws Exception
          {
            FileOutputStream out = new FileOutputStream(outputFile);
            InputStream in = url.openStream();
            byte[] buff = new byte[4096];
            int nch;
            while((nch = in.read(buff, 0, buff.length)) != -1)
            {
              out.write(buff, 0, nch);
            }
            out.flush();
            out.close();
            return null;
          }
        });
        String workflowName = fileName.substring(0, fileName.lastIndexOf('.'));
        currentWorkspace.importDescriptor(workflowName, url.openStream());
        //this ensures that the descriptor is loaded into the cache
        currentWorkspace.getWorkflow(workflowName);
        WorkflowDesigner.INSTANCE.navigator().setWorkspace(currentWorkspace);
      }
    }
    catch(FactoryException ex)
    {
      JOptionPane.showMessageDialog(WorkflowDesigner.INSTANCE, ResourceManager.getString("import.factory.error.long", new Object[]{fileName, ex.getMessage()}),
                                    ResourceManager.getString("import.factory.error"), JOptionPane.ERROR_MESSAGE);
    }
    catch(Exception t)
    {
      log.error("Error importing descriptor", t);
    }
    finally
    {
      status.setIndeterminate(false);
      status.setStatus("");
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
