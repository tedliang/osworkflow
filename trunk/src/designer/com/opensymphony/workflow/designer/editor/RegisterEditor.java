package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.spi.DefaultRegisterPlugin;
import com.opensymphony.workflow.designer.spi.RegisterPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigRegisterDescriptor;
import com.opensymphony.workflow.loader.RegisterDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:25:07 AM
 */
public class RegisterEditor
{
  protected AbstractDescriptor descriptor;
  protected WorkflowGraphModel model;

  public RegisterEditor(AbstractDescriptor descriptor)
  {
    this.descriptor = descriptor;
  }

  public WorkflowGraphModel getModel()
  {
    return model;
	}
	
	public AbstractDescriptor getDescriptor()
	{
		return descriptor;
	}

  public void setModel(WorkflowGraphModel model)
  {
    this.model = model;
  }

  public RegisterDescriptor add()
  {
    ConfigRegisterDescriptor register = getRegister();
    if (register == null)
    {
      return null;
    }

    register = editRegister(register, WorkflowDesigner.INSTANCE);

    if (register != null)
    {
      RegisterDescriptor reg = DescriptorFactory.getFactory().createRegisterDescriptor();
      reg.setParent(getParent());
      reg.setType(register.getType());
      reg.setVariableName(register.getVariableName());
      reg.getArgs().putAll(register.getArgs());
      return reg;
    }
    else
    {
      return null;
    }

  }

  public void modify(RegisterDescriptor reg)
  {
    ConfigRegisterDescriptor register;

    if (reg.getVariableName() != null)
    {
	    register = new ConfigRegisterDescriptor(getModel().getPalette().getRegister(reg.getVariableName()));
    }
    else
    {
      register = new ConfigRegisterDescriptor(getModel().getPalette());
      register.setType(reg.getType());
    }

    register.getArgs().putAll(reg.getArgs());

    register = editRegister(register, WorkflowDesigner.INSTANCE);

    if (register != null)
    {
      reg.getArgs().putAll(register.getArgs());
    }
  }

  private ConfigRegisterDescriptor editRegister(ConfigRegisterDescriptor config, Component parent)
  {
    // get plugin
    String clazz = config.getPlugin();
    if (clazz == null || clazz.length() == 0)
    {
      clazz = "com.opensymphony.workflow.designer.spi.DefaultRegisterPlugin";
    }
    RegisterPlugin regImpl;
    try
    {
      regImpl = (RegisterPlugin)Class.forName(clazz).newInstance();
    }
    catch(Exception e1)
    {
      e1.printStackTrace();
      regImpl = new DefaultRegisterPlugin();
    }

    regImpl.setRegister(config);

    // put the parameter
    Map args = new HashMap();
    args.put("cell", descriptor);

    if (!regImpl.editRegister(args, WorkflowDesigner.INSTANCE))
    {
      // cancel
      return null;
    }
    config = regImpl.getRegister();
    return config;
  }

  protected AbstractDescriptor getParent()
  {
  	// WorkflowDescriptor is the parent of the Register...
  	return descriptor;
  }

	protected ConfigRegisterDescriptor getRegister()
	{
		ConfigRegisterDescriptor template = (ConfigRegisterDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getRegisters(),
			ResourceManager.getString("register.select"),
			ResourceManager.getString("register.select"), 
			WorkflowDesigner.INSTANCE);
		if(template!=null)
			return new ConfigRegisterDescriptor(template);
		return null;
	}	
}
