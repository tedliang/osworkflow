package com.opensymphony.workflow.designer.spi;

import java.util.Map;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author Gulei
 */
public class DefaultFunctionPluginImpl implements IFunctionPlugin
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

  public boolean editFunction(Map args)
  {
    Map newArg = DialogUtils.getMapDialog(func.getArgs(), func.getType(), func.getName(), func.getDescription());
    if(newArg == null)
    {
      return false;
    }

    func.getArgs().putAll(newArg);
    return true;
  }

}
