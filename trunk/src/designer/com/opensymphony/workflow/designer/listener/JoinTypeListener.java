/*
 * Created on 2003-11-23
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.JoinDescriptor;

/**
 * @author baab
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoinTypeListener extends ComboListener {

	protected JoinDescriptor join;
	
	public void setJoin(JoinDescriptor join){
		this.join = join;
	}
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.listenerl.ComboListener#valueChanged(java.lang.String)
	 */
	protected void valueChanged(String newValue) {
		join.setConditionType(newValue);
	}

}
