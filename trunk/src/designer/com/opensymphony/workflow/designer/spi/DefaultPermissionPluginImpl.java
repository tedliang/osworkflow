/*
 * Created on 2003-11-22
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.spi;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.opensymphony.workflow.designer.editor.DialogUtils;
import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

/**
 * @author Gulei
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultPermissionPluginImpl implements IPermissionCondition {

	PermissionConditionDescriptor perm;
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPermissionCondition#setPermissionCondition(com.opensymphony.workflow.loader.PermissionConditionDescriptor)
	 */
	public void setPermissionCondition(PermissionConditionDescriptor descriptor) {
		perm = descriptor;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPermissionCondition#getPermissionCondition()
	 */
	public PermissionConditionDescriptor getPermissionCondition() {
		return perm;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.spi.IPermissionCondition#editPermissionCondition(java.util.Map)
	 * @return Resources.OWNER??? is owner
	 */
	public boolean editPermissionCondition(Map args) {
		Map newArg = DialogUtils.getMapDialogWithOwner(perm.getArgs(), perm.getType(), perm.getName(), perm.getDescription(), perm.getOwner());
		if(newArg == null){
			return false;
		}
		
		Map oldArg = perm.getArgs();
		Set keys = oldArg.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext()){
			Object key = iter.next();
			oldArg.put(key, newArg.get(key));
		}
		
		return true;
	}

}
