package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.loader.WorkflowFactory;
import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * @author acapitani
 */
public class ValidateWorkflow extends AbstractAction implements WorkspaceListener
{
	private WorkflowFactory currentWorkspace;
	
	public ValidateWorkflow()
	{
		setEnabled(false);
	}

  public void actionPerformed(ActionEvent arg0)
  {
  	WorkflowDesigner.INSTANCE.validateCurrentWorkflow(); 
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
