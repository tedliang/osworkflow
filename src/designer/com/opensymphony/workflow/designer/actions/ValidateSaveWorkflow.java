package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.loader.AbstractWorkflowFactory;
import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * @author acapitani
 */
public class ValidateSaveWorkflow extends AbstractAction implements WorkspaceListener
{
	private AbstractWorkflowFactory currentWorkspace;

	public ValidateSaveWorkflow()
	{
		setEnabled(false);
	}

  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) 
  {
  	WorkflowDesigner.INSTANCE.validateSaveCurrentWorkflow(); 
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
