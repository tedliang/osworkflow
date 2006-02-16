package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.spi.ConditionPlugin;
import com.opensymphony.workflow.designer.spi.DefaultConditionPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

/**
 * @author baab
 */
public abstract class ConditionEditor
{
  //protected WorkflowCell cell;
  protected AbstractDescriptor descriptor;
  protected WorkflowGraphModel model;

  public ConditionEditor(AbstractDescriptor descriptor)
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

  public ConditionDescriptor add()
  {
    ConfigConditionDescriptor condition = getCondition();
    if(condition == null)
    {
      return null;
    }

    condition = editCondition(condition, WorkflowDesigner.INSTANCE);

    if(condition != null)
    {
      ConditionDescriptor cond = DescriptorFactory.getFactory().createConditionDescriptor();
      //			cond.setId(0);
      //			cond.setNegate(false);
      cond.setParent(getParent());
      cond.setType(condition.getType());
      cond.setName(condition.getName());

      // negate?
      String n = (String)condition.getArgs().get("negate");
      condition.getArgs().remove("negate");

      cond.setNegate("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n));
      cond.getArgs().putAll(condition.getArgs());

      return cond;
    }
    else
    {
      return null;
    }

  }

  public void modify(ConditionDescriptor cond)
  {
    ConfigConditionDescriptor condition;

    if(cond.getName() != null)
    {
	    condition = new ConfigConditionDescriptor(getConfigDescriptor(cond));
    }
    else
    {
      condition = new ConfigConditionDescriptor(getModel().getPalette());
      condition.setType(cond.getType());
    }

    // set negate as argument
    cond.getArgs().put("negate", String.valueOf(cond.isNegate()));
    
    condition.getArgs().putAll(cond.getArgs());

    condition = editCondition(condition, WorkflowDesigner.INSTANCE);

    if(condition != null)
    {
      // negate?
      String n = (String)condition.getArgs().get("negate");
      condition.getArgs().remove("negate");

      cond.setNegate("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n));
      cond.getArgs().putAll(condition.getArgs());
    }
  }

	protected abstract ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond);

	private ConfigConditionDescriptor editCondition(ConfigConditionDescriptor config, Component parent)
  {
    // get plugin
    String clazz = config.getPlugin();
    if(clazz == null || clazz.length() == 0)
    {
      clazz = "com.opensymphony.workflow.designer.spi.DefaultConditionPlugin";
    }
    ConditionPlugin condImpl;
    try
    {
      condImpl = (ConditionPlugin)Class.forName(clazz).newInstance();
    }
    catch(Exception e1)
    {
      e1.printStackTrace();
      condImpl = new DefaultConditionPlugin();
    }

    condImpl.setCondition(config);

    // put the parameter
    Map args = new HashMap();
    args.put("cell", descriptor);

    if(!condImpl.editCondition(args, parent))
    {
      // cancel
      return null;
    }
    config = condImpl.getCondition();
    return config;
  }

  abstract protected AbstractDescriptor getParent();

  abstract protected ConfigConditionDescriptor getCondition();
}
