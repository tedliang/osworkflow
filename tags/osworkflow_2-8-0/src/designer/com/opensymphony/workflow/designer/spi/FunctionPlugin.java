/*
 * Created on 2003-11-22
 */
package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author Gulei
 */
public interface FunctionPlugin {

	public void setFunction(ConfigFunctionDescriptor descriptor);

	public ConfigFunctionDescriptor getFunction();

	public boolean editFunction(Map args, Component parent);

}
