package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class StartPermissionEditor extends ConditionEditor
{

  public StartPermissionEditor(InitialActionCell cell)
  {
    super(cell);
  }

  protected InitialActionCell getCell()
  {
    return (InitialActionCell)cell;
  }

  protected AbstractDescriptor getParent()
  {
    return getCell().getActionDescriptor();
  }

  protected ConfigConditionDescriptor getNewCondition(String selection)
  {
    return new PermissionConditionDescriptor(WorkflowDesigner.config.getPermissionCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(WorkflowDesigner.config.getPermissionNames(), "Select Permission", "Select Permission", null);
  }

}
