/*
 * Created on 2003-11-22
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.spi;

import java.util.Map;

import com.opensymphony.workflow.designer.editor.DialogUtils;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author Gulei
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultConditionPluginImpl implements IConditionPlugin {

	private ConfigConditionDescriptor condition ;
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IResultCondition#setResultCondition(com.opensymphony.workflow.loader.ResultConditionDescriptor)
	 */
	public void setCondition(ConfigConditionDescriptor descriptor) {
		condition = descriptor;

	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IResultCondition#getResultCondition()
	 */
	public ConfigConditionDescriptor getCondition() {
		return condition;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IResultCondition#editResultCondition(java.util.Map)
	 */
	public boolean editCondition(Map args) {
		Map newArg = DialogUtils.getMapDialog(condition.getArgs(), condition.getType(), condition.getName(), condition.getDescription());
		if(newArg == null){
			return false;
		}
		
		condition.getArgs().putAll(newArg);
		return true;
		
	}


}
