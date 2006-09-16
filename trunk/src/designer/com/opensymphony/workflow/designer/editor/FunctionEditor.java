package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.spi.DefaultFunctionPlugin;
import com.opensymphony.workflow.designer.spi.FunctionPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

/**
 * @author baab
 */
public abstract class FunctionEditor
{
  //protected WorkflowCell cell;
  protected AbstractDescriptor descriptor;
  protected WorkflowGraphModel model;

  public FunctionEditor(AbstractDescriptor descriptor)
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

  public FunctionDescriptor add()
  {
    ConfigFunctionDescriptor function = getFunction();
    if(function == null)
    {
      return null;
    }

    function = editFunction(function, WorkflowDesigner.INSTANCE);

    if(function != null)
    {
      FunctionDescriptor func = DescriptorFactory.getFactory().createFunctionDescriptor();
      //			cond.setId(0);
      func.setParent(getParent());
      func.setType(function.getType());
      func.setName(function.getName());
      func.getArgs().putAll(function.getArgs());

      return func;
    }
    else
    {
      return null;
    }
  }

  public void modify(FunctionDescriptor func)
  {
    ConfigFunctionDescriptor function;

    if(func.getName() != null && (getModel().getPalette().getPrefunction(func.getName()) != null))
    {
	    function = new ConfigFunctionDescriptor(getModel().getPalette().getPrefunction(func.getName()));
    }
    else
    {
      function = new ConfigFunctionDescriptor(getModel().getPalette());
      function.setType(func.getType());
    }

    function.getArgs().putAll(func.getArgs());

    function = editFunction(function, WorkflowDesigner.INSTANCE);

    if(function != null)
    {
      func.getArgs().putAll(function.getArgs());
    }
  }

  private ConfigFunctionDescriptor editFunction(ConfigFunctionDescriptor config, Component parent)
  {
    // get plugin
    String clazz = config.getPlugin();
    if(clazz == null || clazz.length() == 0)
    {
      clazz = "com.opensymphony.workflow.designer.spi.DefaultFunctionPlugin";
    }
    FunctionPlugin funcImpl;
    try
    {
      funcImpl = (FunctionPlugin)Class.forName(clazz).newInstance();
    }
    catch(Exception e1)
    {
      e1.printStackTrace();
      funcImpl = new DefaultFunctionPlugin();
    }

    funcImpl.setFunction(config);

    // put the parameter
    Map args = new HashMap();
    args.put("cell", descriptor);

    if(!funcImpl.editFunction(args, parent))
    {
      // cancel
      return null;
    }
    config = funcImpl.getFunction();
    return config;
  }

  protected abstract AbstractDescriptor getParent();

  protected abstract ConfigFunctionDescriptor getFunction();
}
