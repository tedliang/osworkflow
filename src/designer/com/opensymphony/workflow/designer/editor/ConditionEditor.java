package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.designer.WorkflowCell;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.spi.ConditionPlugin;
import com.opensymphony.workflow.designer.spi.DefaultConditionPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author baab
 */
public abstract class ConditionEditor
{
  protected WorkflowCell cell;
  protected WorkflowGraphModel model;

  public ConditionEditor(WorkflowCell cell)
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

  public ConditionDescriptor add()
  {
    ConfigConditionDescriptor condition = getCondition();
    if(condition == null)
    {
      return null;
    }

    condition = editCondition(condition);

    if(condition != null)
    {
      ConditionDescriptor cond = new ConditionDescriptor();
      //			cond.setId(0);
      //			cond.setNegate(false);
      cond.setParent(getParent());
      cond.setType(condition.getType());
      cond.setName(condition.getName());
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
	    System.out.println("cond.getName()=" + cond.getName());
	    condition = new ConfigConditionDescriptor(getConfigDescriptor(cond));
    }
    else
    {
      condition = new ConfigConditionDescriptor();
      condition.setType(cond.getType());
    }

    condition.getArgs().putAll(cond.getArgs());

    condition = editCondition(condition);

    if(condition != null)
    {
      cond.getArgs().putAll(condition.getArgs());
    }
  }

	protected abstract ConfigConditionDescriptor getConfigDescriptor(ConditionDescriptor cond);

	private ConfigConditionDescriptor editCondition(ConfigConditionDescriptor config)
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
    args.put("cell", cell);

    if(!condImpl.editCondition(args))
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
