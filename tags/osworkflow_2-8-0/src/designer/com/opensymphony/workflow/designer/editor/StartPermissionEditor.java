package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.*;

/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class StartPermissionEditor extends ConditionEditor
{

  public StartPermissionEditor(ActionDescriptor desc)
  {
    super(desc);
  }

  protected AbstractDescriptor getParent()
  {
    return getDescriptor();
  }

	protected ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond)
	{
		return getModel().getPalette().getPermissionCondition(cond.getName());
	}

	protected ConfigConditionDescriptor getCondition()
	{
		PermissionConditionDescriptor template = (PermissionConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPermissionConditions(),
		  ResourceManager.getString("permission.select"),
		  ResourceManager.getString("permission.select"), 
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new PermissionConditionDescriptor(template);
		return null;
	}
}
