package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.loader.Workspace;
import com.opensymphony.workflow.FactoryException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 1:14:41 AM
 */
public class NewWorkflow extends AbstractAction implements WorkspaceListener
{
  private static final Log log = LogFactory.getLog(NewWorkflow.class);

  private Workspace currentWorkspace;

  public NewWorkflow()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    String name = JOptionPane.showInputDialog(ResourceManager.getString("createflow"), ResourceManager.getString("createflow.long"));
    if(name==null) return;
    try
    {
      if(currentWorkspace.getWorkflow(name)!=null)
      {
        JOptionPane.showMessageDialog((Component)e.getSource(), ResourceManager.getString("createflow.error", new Object[]{name}), ResourceManager.getString("createflow.error"), JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    catch(FactoryException ex)
    {
      log.error("Error creating definition:" + ex.toString());
      return;
    }
    currentWorkspace.createWorkflow(name);
    WorkflowDesigner.INSTANCE.newWorkflowCreated(name);
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
