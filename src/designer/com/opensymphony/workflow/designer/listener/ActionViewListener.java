/*
 * Created on 2003-11-24
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.ActionDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class ActionViewListener extends TextFieldListener {

	private ActionDescriptor action;
	
	public void setAction(ActionDescriptor action){
		this.action = action;
	}
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.listener.TextFieldListener#valueChanged(java.lang.String)
	 */
	protected void valueChanged(String msg) {
		action.setView(msg);
	}

}
