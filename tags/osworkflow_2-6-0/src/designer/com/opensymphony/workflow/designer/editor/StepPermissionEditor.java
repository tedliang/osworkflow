package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.*;

/**
 * @author baab
 */
public class StepPermissionEditor extends ConditionEditor
{
  public StepPermissionEditor(StepCell cell)
  {
    super(cell);
  }

  protected AbstractDescriptor getParent()
  {
    return ((ActionDescriptor)getCell().getDescriptor().getActions().get(0)).getRestriction();
  }

	protected ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond)
	{
		return getModel().getPalette().getPermissionCondition(cond.getName());
	}

	protected ConfigConditionDescriptor getCondition()
	{
		PermissionConditionDescriptor template = (PermissionConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPermissionConditions(),
		  ResourceManager.getString("permission.select.step"),
		  ResourceManager.getString("permission.select"), null);
		if(template!=null)
		  return new PermissionConditionDescriptor(template);
		return null;
	}

  protected StepCell getCell()
  {
    return (StepCell)cell;
  }
}
