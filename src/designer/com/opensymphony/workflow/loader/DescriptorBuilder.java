package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author jackflit
 * Date: 2003-11-18
 */
public class DescriptorBuilder
{
  public static StepDescriptor createStep(String name, int id)
  {
    StepDescriptor step = DescriptorFactory.getFactory().createStepDescriptor();
    step.setName(name);
    step.setId(id);
    return step;
  }

  public static ActionDescriptor createAction(StepDescriptor step, String name, int id)
  {
    ActionDescriptor action = DescriptorFactory.getFactory().createActionDescriptor();
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
    JoinDescriptor join = DescriptorFactory.getFactory().createJoinDescriptor();
    join.setId(id);
    return join;
  }

  public static SplitDescriptor createSplit(int id)
  {
    SplitDescriptor split = DescriptorFactory.getFactory().createSplitDescriptor();
    split.setId(id);
    return split;
  }

  public static ResultDescriptor createResult(WorkflowGraphModel model, AbstractDescriptor desc)
  {
    ResultDescriptor result = DescriptorFactory.getFactory().createResultDescriptor();
    Utils.checkId(model.getContext(), desc);
    if(result.getId()==0)
      result.setId(Utils.getNextId(model.getContext()));
    result.setOldStatus(model.getPalette().getDefaultOldStatus());
    result.setStatus(model.getPalette().getDefaultNextStatus());
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

  public static ConditionalResultDescriptor createConditionalResult(WorkflowGraphModel model, AbstractDescriptor desc)
  {
    ConditionalResultDescriptor result = DescriptorFactory.getFactory().createConditionalResultDescriptor();
    Utils.checkId(model.getContext(), desc);
    if(result.getId()==0)
      result.setId(Utils.getNextId(model.getContext()));
    result.setOldStatus(model.getPalette().getDefaultOldStatus());
    result.setStatus(model.getPalette().getDefaultNextStatus());
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
