package com.opensymphony.workflow.designer.editor;

import java.util.List;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.*;

/**
 * @author baab
 */
public class StepPermissionEditor extends ConditionEditor
{
  public StepPermissionEditor(StepDescriptor desc)
  {
    super(desc);
  }

  protected AbstractDescriptor getParent()
  {
    List actions = ((StepDescriptor)getDescriptor()).getActions();
    if(actions.size()>0)
      return ((ActionDescriptor)actions.get(0)).getRestriction();
    return null;
  }

	protected ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond)
	{
		return getModel().getPalette().getPermissionCondition(cond.getName());
	}

	protected ConfigConditionDescriptor getCondition()
	{
		PermissionConditionDescriptor template = (PermissionConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPermissionConditions(),
		  ResourceManager.getString("permission.select.step"),
		  ResourceManager.getString("permission.select"), 
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new PermissionConditionDescriptor(template);
		return null;
	}
}
