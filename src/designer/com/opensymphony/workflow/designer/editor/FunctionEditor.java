package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.designer.WorkflowCell;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.spi.DefaultFunctionPluginImpl;
import com.opensymphony.workflow.designer.spi.IFunctionPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * @author baab
 */
public abstract class FunctionEditor
{
  protected WorkflowCell cell;
  protected WorkflowGraphModel model;

  public FunctionEditor(WorkflowCell cell)
  {
    this.cell = cell;
  }

  public WorkflowGraphModel getModel()
  {
    return model;
  }

  public void setModel(WorkflowGraphModel model)
  {
    this.model = model;
  }

  public FunctionDescriptor add()
  {
    String selection = getSelection();
    if(selection == null)
    {
      return null;
    }

    ConfigFunctionDescriptor function = getNewFunction(selection);

    function = editFunction(function);

    if(function != null)
    {
      FunctionDescriptor func = new FunctionDescriptor();
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
    ConfigFunctionDescriptor function = null;

    if(func.getName() != null)
    {
      function = getNewFunction(func.getName());
    }
    else
    {
      function = new ConfigFunctionDescriptor();
      function.setType(func.getType());
    }

    function.getArgs().putAll(func.getArgs());

    function = editFunction(function);

    if(function != null)
    {
      func.getArgs().putAll(function.getArgs());
    }

  }

  private ConfigFunctionDescriptor editFunction(ConfigFunctionDescriptor config)
  {
    // get plugin
    String clazz = config.getPlugin();
    if(clazz == null || clazz.length() == 0)
    {
      clazz = "com.opensymphony.workflow.designer.spi.DefaultFunctionPluginImpl";
    }
    IFunctionPlugin funcImpl = null;
    try
    {
      funcImpl = (IFunctionPlugin)Class.forName(clazz).newInstance();
    }
    catch(Exception e1)
    {
      e1.printStackTrace();
      funcImpl = new DefaultFunctionPluginImpl();
    }

    funcImpl.setFunction(config);

    // put the parameter
    Map args = new HashMap();
    args.put("cell", cell);

    if(!funcImpl.editFunction(args))
    {
      // cancel
      return null;
    }
    config = funcImpl.getFunction();
    return config;
  }

  abstract protected AbstractDescriptor getParent();

  abstract protected ConfigFunctionDescriptor getNewFunction(String selection);

  abstract protected String getSelection();
}
