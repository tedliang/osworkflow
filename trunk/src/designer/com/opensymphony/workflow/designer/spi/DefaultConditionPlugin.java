package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

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

  public boolean editCondition(Map args, Component parent)
  {
    Map newArg = DialogUtils.getMapDialog(condition, condition.getType(), null, parent);
    if(newArg == null)
    {
      return false;
    }
    condition.getArgs().putAll(newArg);
    return true;
  }
}
