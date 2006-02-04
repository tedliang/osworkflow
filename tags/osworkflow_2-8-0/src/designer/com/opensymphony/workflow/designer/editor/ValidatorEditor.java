package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.spi.DefaultValidatorPlugin;
import com.opensymphony.workflow.designer.spi.ValidatorPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigValidatorDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:25:07 AM
 */
public class ValidatorEditor
{
  protected AbstractDescriptor descriptor;
  protected WorkflowGraphModel model;

  public ValidatorEditor(AbstractDescriptor descriptor)
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

  public ValidatorDescriptor add()
  {
    ConfigValidatorDescriptor validator = getValidator();
    if (validator == null)
    {
      return null;
    }

    validator = editValidator(validator, WorkflowDesigner.INSTANCE);

    if (validator != null)
    {
      ValidatorDescriptor val = DescriptorFactory.getFactory().createValidatorDescriptor();
      val.setParent(getParent());
      val.setType(validator.getType());
      val.getArgs().putAll(validator.getArgs());
      return val;
    }
    else
    {
      return null;
    }
  }

  public void modify(ValidatorDescriptor val)
  {
    ConfigValidatorDescriptor validator;
		
		// TODO [kap] verificare qui perch� manca il nome del validator
		// vedere anche come � fatto il metodo modify di RegisterEditor e/o FunctionEditor  
		
		validator = new ConfigValidatorDescriptor(getModel().getPalette());
		validator.setType(val.getType());
    
    validator.getArgs().putAll(val.getArgs());

    validator = editValidator(validator, WorkflowDesigner.INSTANCE);

    if (validator != null)
    {
      val.getArgs().putAll(validator.getArgs());
    }
  }

  private ConfigValidatorDescriptor editValidator(ConfigValidatorDescriptor config, Component parent)
  {
    // get plugin
    String clazz = config.getPlugin();
    if (clazz == null || clazz.length() == 0)
    {
      clazz = "com.opensymphony.workflow.designer.spi.DefaultValidatorPlugin";
    }
    ValidatorPlugin valImpl;
    try
    {
      valImpl = (ValidatorPlugin)Class.forName(clazz).newInstance();
    }
    catch(Exception e1)
    {
      e1.printStackTrace();
      valImpl = new DefaultValidatorPlugin();
    }

    valImpl.setValidator(config);

    // put the parameter
    Map args = new HashMap();
    args.put("cell", descriptor);

    if (!valImpl.editValidator(args, parent))
    {
      // cancel
      return null;
    }
    config = valImpl.getValidator();
    return config;
  }

  protected AbstractDescriptor getParent()
  {
  	return descriptor;
  }

	protected ConfigValidatorDescriptor getValidator()
	{
		ConfigValidatorDescriptor template = (ConfigValidatorDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getValidators(),
			ResourceManager.getString("validator.select"),
			ResourceManager.getString("validator.select"), 
			WorkflowDesigner.INSTANCE);
		if (template!=null)
			return new ConfigValidatorDescriptor(template);
		return null;
	}	
}
