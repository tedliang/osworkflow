package com.opensymphony.workflow.designer.spi;

import java.util.Map;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author Gulei
 */
public class DefaultConditionPlugin implements ConditionPlugin
{

  private ConfigConditionDescriptor condition;

  public void setCondition(ConfigConditionDescriptor descriptor)
  {
    condition = descriptor;

  }

  public ConfigConditionDescriptor getCondition()
  {
    return condition;
  }

  public boolean editCondition(Map args)
  {
    Map newArg = DialogUtils.getMapDialog(condition.getArgs(), condition.getType(), condition.getDisplayName(), condition.getDescription());
    if(newArg == null)
    {
      return false;
    }

    condition.getArgs().putAll(newArg);
    return true;
  }

}