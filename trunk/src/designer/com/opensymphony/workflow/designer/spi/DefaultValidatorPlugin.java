package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigValidatorDescriptor;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:36:07 AM 
 */
public class DefaultValidatorPlugin implements ValidatorPlugin
{

  ConfigValidatorDescriptor val;

  public void setValidator(ConfigValidatorDescriptor descriptor)
  {
    val = descriptor;
  }

  public ConfigValidatorDescriptor getValidator()
  {
    return val;
  }

  public boolean editValidator(Map args, Component parent)
  {
    Map newArg = DialogUtils.getMapDialog(val, val.getType(), null, parent);
    if (newArg == null)
    {
      return false;
    }

    val.getArgs().putAll(newArg);
    return true;
  }
}
