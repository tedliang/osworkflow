package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author baab
 */
public class JoinConditionEditor extends ConditionEditor
{
  public JoinConditionEditor(JoinCell cell)
  {
    super(cell);
  }

  protected AbstractDescriptor getParent()
  {
    return getCell().getJoinDescriptor();
  }

  protected ConfigConditionDescriptor getCondition()
  {
	  ConfigConditionDescriptor template = (ConfigConditionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getJoinConditions(),
	    ResourceManager.getString("condition.select.join"),
	    ResourceManager.getString("condition.select"), null);
	  if(template!=null)
	    return new ConfigConditionDescriptor(template);
	  return null;
  }

  protected JoinCell getCell()
  {
    return (JoinCell)cell;
  }

}
