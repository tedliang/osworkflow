/*
 * Created on 2003-11-22
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.spi;

import java.util.Map;

import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

/**
 * @author Gulei
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IPermissionCondition {

	public void setPermissionCondition(PermissionConditionDescriptor descriptor);
	
	public PermissionConditionDescriptor getPermissionCondition();
	
	public boolean editPermissionCondition(Map args);

}
