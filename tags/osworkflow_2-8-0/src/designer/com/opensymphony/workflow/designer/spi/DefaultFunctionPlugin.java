package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author Gulei
 */
public class DefaultFunctionPlugin implements FunctionPlugin
{

  ConfigFunctionDescriptor func;

  public void setFunction(ConfigFunctionDescriptor descriptor)
  {
    func = descriptor;

  }

  public ConfigFunctionDescriptor getFunction()
  {
    return func;
  }

  public boolean editFunction(Map args, Component parent)
  {
    Map newArg = DialogUtils.getMapDialog(func, func.getType(), null, parent);
    if(newArg == null)
    {
      return false;
    }

    func.getArgs().putAll(newArg);
    return true;
  }

}
