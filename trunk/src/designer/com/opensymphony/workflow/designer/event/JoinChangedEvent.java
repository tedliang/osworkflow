/*
 * Created on 2003-11-22
 *
 * source is JoinCell
 * should know model? in whitch model 
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.event;

import java.util.EventObject;
import java.util.Map;

import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author Gulei
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoinChangedEvent extends EventObject {

	private WorkflowGraphModel model;
	private Map args;
	/**
	 * @param source should be JoinCell
	 */
	public JoinChangedEvent(Object source) {
		super(source);
	}

	public JoinChangedEvent(Object source, WorkflowGraphModel model) {
		super(source);
		this.model = model;		
	}
	
	

	/**
	 * @return
	 */
	public Map getArgs() {
		return args;
	}

	/**
	 * @return
	 */
	public WorkflowGraphModel getModel() {
		return model;
	}

	/**
	 * @param map
	 */
	public void setArgs(Map map) {
		args = map;
	}

	/**
	 * @param model
	 */
	public void setModel(WorkflowGraphModel model) {
		this.model = model;
	}

}
