package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;

/**
 * User: Hani Suleiman
 * Date: Jan 6, 2004
 * Time: 1:23:42 PM
 */
public class BshFunctionPlugin implements FunctionPlugin
{
	private ConfigFunctionDescriptor function;

	public void setFunction(ConfigFunctionDescriptor descriptor)
	{
		this.function = descriptor;
	}

	public ConfigFunctionDescriptor getFunction()
	{
		return function;
	}

	public boolean editFunction(Map args, Component parent)
	{
		String text = DialogUtils.getTextDialog((String)function.getArgs().get("script"), parent);
		if(text!=null)
		{
			function.getArgs().put("script", text);
			return true;
		}
		return false;
	}
}
