package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * User: Hani Suleiman
 * Date: Jan 13, 2004
 * Time: 3:21:54 PM
 */
public class DeleteWorkflow extends AbstractAction
{
	private WorkflowDesigner designer;
	private String name;

	public DeleteWorkflow(WorkflowDesigner designer)
	{
		this.designer = designer;
	}

	public void actionPerformed(ActionEvent e)
	{
		designer.deleteWorkflow(name);
	}

	public void setWorkflow(String name)
	{
		this.name = name;
	}
}
