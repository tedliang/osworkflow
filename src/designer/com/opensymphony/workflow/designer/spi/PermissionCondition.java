package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

/**
 * @author Gulei
 */
public interface PermissionCondition {

	public void setPermissionCondition(PermissionConditionDescriptor descriptor);

	public PermissionConditionDescriptor getPermissionCondition();

	public boolean editPermissionCondition(Map args, Component parent);

}
