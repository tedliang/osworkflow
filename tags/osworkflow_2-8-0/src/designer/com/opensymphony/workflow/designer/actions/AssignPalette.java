package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowDesigner;

/**
 * User: Hani Suleiman
 * Date: Jan 13, 2004
 * Time: 4:16:16 PM
 */
public class AssignPalette extends AbstractAction
{
	private WorkflowDesigner designer;
	private String workflowName;

	public AssignPalette(WorkflowDesigner designer)
	{
		this.designer = designer;
	}

	public void actionPerformed(ActionEvent e)
	{
	}

	public void setWorkflow(String name)
	{
		this.workflowName = name;
	}
}
