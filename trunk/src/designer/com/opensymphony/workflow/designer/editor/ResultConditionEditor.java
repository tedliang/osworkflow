package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author baab
 */
public class ResultConditionEditor extends ConditionEditor
{
  //private ResultEdge edge;
  private ResultDescriptor descriptor;

  public ResultConditionEditor(ResultDescriptor desc)
  {
    super(null);
    descriptor = desc;
  }

  protected AbstractDescriptor getParent()
  {
    return descriptor;
  }

	protected ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond)
	{
		return getModel().getPalette().getResultCondition(cond.getName());
	}

	protected ConfigConditionDescriptor getCondition()
	{
		ConfigConditionDescriptor template = (ConfigConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getResultConditions(),
		  ResourceManager.getString("condition.select.result"),
		  ResourceManager.getString("condition.select"),
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new ConfigConditionDescriptor(template);
		return null;
	}
}
