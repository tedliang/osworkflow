/*
 * Created on 2003-11-24
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author baab
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResultOwnerListener extends TextFieldListener {

	private ResultDescriptor result;
	
	public void setResult(ResultDescriptor result){
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.listener.TextFieldListener#valueChanged(java.lang.String)
	 */
	protected void valueChanged(String msg) {
		result.setOwner(msg);
	}

}
