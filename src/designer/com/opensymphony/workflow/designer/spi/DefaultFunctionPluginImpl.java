/*
 * Created on 2003-11-22
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.spi;

import java.util.Map;

import com.opensymphony.workflow.designer.editor.DialogUtils;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author Gulei
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultFunctionPluginImpl implements IFunctionPlugin {

	ConfigFunctionDescriptor func;
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPrefunction#setPrefunction(com.opensymphony.workflow.loader.PrefunctionDescriptor)
	 */
	public void setFunction(ConfigFunctionDescriptor descriptor) {
		func = descriptor;

	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPrefunction#getPrefunction()
	 */
	public ConfigFunctionDescriptor getFunction() {
		return func;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPrefunction#editPrefunction(java.util.Map)
	 */
	public boolean editFunction(Map args) {
		Map newArg = DialogUtils.getMapDialog(func.getArgs(), func.getType(), func.getName(), func.getDescription());
		if(newArg == null){
			return false;
		}
		
		func.getArgs().putAll(newArg);
		return true;
	}

}
