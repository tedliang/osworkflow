package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
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
    return new PermissionConditionDescriptor(getModel().getPalette().getPermissionCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(getModel().getPalette().getPermissionNames(), ResourceManager.getString("permission.select"), ResourceManager.getString("permission.select"), null);
  }

}
