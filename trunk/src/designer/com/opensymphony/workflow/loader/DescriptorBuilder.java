package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.designer.Utils;

/**
 * @author jackflit
 * Date: 2003-11-18
 */
public class DescriptorBuilder
{
  public static StepDescriptor createStep(String name, int id)
  {
    StepDescriptor step = new StepDescriptor();
    step.setName(name);
    step.setId(id);
    return step;
  }

  public static ActionDescriptor createAction(StepDescriptor step, String name, int id)
  {
    ActionDescriptor action = new ActionDescriptor();
    step.getActions().add(action);
    action.setParent(step);
    action.setAutoExecute(false);
    action.setId(id);
    action.setName(name);
    action.setView(name);
    return action;
  }

  public static JoinDescriptor createJoin(int id)
  {
    JoinDescriptor join = new JoinDescriptor();
    join.setId(id);
    return join;
  }

  public static SplitDescriptor createSplit(int id)
  {
    SplitDescriptor split = new SplitDescriptor();
    split.setId(id);
    return split;
  }

  public static ResultDescriptor createResult(AbstractDescriptor desc)
  {
    ResultDescriptor result = new ResultDescriptor();
    Utils.checkId(desc);
    if(result.getId()==0)
      result.setId(Utils.getNextId());
    result.setOldStatus("Finished");
    result.setStatus("Underway");
    if(desc instanceof StepDescriptor)
    {
      result.setStep(desc.getId());
    }
    else if(desc instanceof SplitDescriptor)
    {
      result.setSplit(desc.getId());
    }
    else if(desc instanceof JoinDescriptor)
    {
      result.setJoin(desc.getId());
    }
    else
    {
      return null;
    }
    return result;
  }

  public static ConditionalResultDescriptor createConditionalResult(AbstractDescriptor desc)
  {
    ConditionalResultDescriptor result = new ConditionalResultDescriptor();
    Utils.checkId(desc);
    if(result.getId()==0)
      result.setId(Utils.getNextId());
    result.setOldStatus("Finished");
    result.setStatus("Underway");
    result.setOwner("$$$");
    if(desc instanceof StepDescriptor)
    {
      result.setStep(desc.getId());
    }
    else if(desc instanceof SplitDescriptor)
    {
      result.setSplit(desc.getId());
    }
    else if(desc instanceof JoinDescriptor)
    {
      result.setJoin(desc.getId());
    }
    else
    {
      return null;
    }
    
    return result;
  }
}