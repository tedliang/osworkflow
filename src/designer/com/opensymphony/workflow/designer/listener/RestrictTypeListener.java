/*
 * Created on 2003-11-23
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.RestrictionDescriptor;

/**
 * @author baab
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RestrictTypeListener extends ComboListener {

	private RestrictionDescriptor restrict;
	
	public void setRestrict(RestrictionDescriptor restrict){
		this.restrict = restrict;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.listener.ComboListener#valueChanged(java.lang.String)
	 */
	protected void valueChanged(String newValue) {
		restrict.setConditionType(newValue);
	}

}
