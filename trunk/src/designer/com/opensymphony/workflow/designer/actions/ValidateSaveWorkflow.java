package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.WorkflowDesigner;

public class ValidateSaveWorkflow extends AbstractAction implements WorkspaceListener
{
	public ValidateSaveWorkflow()
	{
		setEnabled(false);
	}

  public void actionPerformed(ActionEvent arg0)
  {
  	WorkflowDesigner.INSTANCE.validateSaveCurrentWorkflow(); 
  }
  
	public void workspaceChanged(WorkspaceEvent event)
	{
		if(event.getId()==WorkspaceEvent.WORKSPACE_OPENED)
		{
			setEnabled(true);
		}
		else
		{
			setEnabled(false);
		}
	}

}
