package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigRegisterDescriptor;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:36:07 AM 
 */
public class DefaultRegisterPlugin implements RegisterPlugin
{

  ConfigRegisterDescriptor reg;

  public void setRegister(ConfigRegisterDescriptor descriptor)
  {
    reg = descriptor;
  }

  public ConfigRegisterDescriptor getRegister()
  {
    return reg;
  }

  public boolean editRegister(Map args, Component parent)
  {
    Map newArg = DialogUtils.getMapDialog(reg, reg.getType(), null, parent);
    if (newArg == null)
    {
      return false;
    }

    reg.getArgs().putAll(newArg);
    return true;
  }
}
