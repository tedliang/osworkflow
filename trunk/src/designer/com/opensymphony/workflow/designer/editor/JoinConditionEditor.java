package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.JoinDescriptor;

/**
 * @author baab
 */
public class JoinConditionEditor extends ConditionEditor
{
  public JoinConditionEditor(JoinDescriptor desc)
  {
    super(desc);
  }

  protected AbstractDescriptor getParent()
  {
    return getDescriptor();
  }

  protected ConfigConditionDescriptor getCondition()
  {
	  ConfigConditionDescriptor template = (ConfigConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getJoinConditions(),
	    ResourceManager.getString("condition.select.join"),
	    ResourceManager.getString("condition.select"), 
	    WorkflowDesigner.INSTANCE);
	  if(template!=null)
	    return new ConfigConditionDescriptor(template);
	  return null;
  }

	protected ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond)
	{
		return getModel().getPalette().getJoinCondition(cond.getName());
	}
}
