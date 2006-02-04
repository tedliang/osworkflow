package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;

/**
 * User: Hani Suleiman
 * Date: Jan 6, 2004
 * Time: 1:23:42 PM
 */
public class BshConditionPlugin implements ConditionPlugin
{
	private ConfigConditionDescriptor condition;

	public void setCondition(ConfigConditionDescriptor descriptor)
	{
		this.condition = descriptor;
	}

	public ConfigConditionDescriptor getCondition()
	{
		return condition;
	}

	public boolean editCondition(Map args, Component parent)
	{
		String text = DialogUtils.getTextDialog((String)condition.getArgs().get("script"), parent);
		if(text!=null)
		{
			condition.getArgs().put("script", text);
			return true;
		}
		return false;
	}
}
