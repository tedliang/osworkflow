package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.loader.WorkflowFactory;
import com.opensymphony.workflow.FactoryException;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 1:14:41 AM
 */
public class NewWorkflow extends AbstractAction implements WorkspaceListener
{
  //private Workspace currentWorkspace;
  private WorkflowFactory currentWorkspace;

  public NewWorkflow()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    String name = JOptionPane.showInputDialog(WorkflowDesigner.INSTANCE, ResourceManager.getString("createflow"), ResourceManager.getString("createflow.long"));
    if(name==null) return;
    try
    {
      if(currentWorkspace.getWorkflow(name)!=null)
      {
        JOptionPane.showMessageDialog(WorkflowDesigner.INSTANCE, ResourceManager.getString("createflow.error", new Object[]{name}), ResourceManager.getString("createflow.error"), JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    catch(FactoryException ex)
    {
      ex.printStackTrace();
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
