package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

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

  protected ConfigConditionDescriptor getNewCondition(String selection)
  {
    return new PermissionConditionDescriptor(getModel().getPalette().getPermissionCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(getModel().getPalette().getPermissionNames(), "Select step permission", "Select Permission", null);
  }

  protected StepCell getCell()
  {
    return (StepCell)cell;
  }
}
