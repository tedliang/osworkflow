/*
 * Created on 2003-11-18
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.workflow.designer.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-18
 */
public class CreateSplit extends AbstractAction {

	private WorkflowGraphModel model;
	private Point location;
	private WorkflowDescriptor workflow;

	public CreateSplit(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location){
		super("Split");
		this.workflow = workflow;		
		this.model = model;
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		CellFactory.createJoin(workflow, model, location);
	}

}
