package com.opensymphony.workflow.designer;

import java.util.List;

import javax.swing.JOptionPane;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.DescriptorBuilder;
import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.SplitDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.designer.dialogs.ActionTypeDialog;

/**
 * @author baab
 */
public class ConnectHelper
{

  public static final int CONDITIONAL = 0;
  public static final int UNCONDITIONAL = 1;
  private static final int UNKNOWN = -1;

  private static boolean isConnectable(WorkflowCell source, WorkflowCell target)
  {

    if(source == null || target == null)
    {
      return false;
    }

    if(source instanceof InitialActionCell && target instanceof StepCell)
    {
      return true;
    }
    else if(source instanceof StepCell && !(target instanceof InitialActionCell))
    {
      return true;
    }
    else if(source instanceof JoinCell && target instanceof StepCell)
    {
      return true;
    }
    else if(source instanceof SplitCell && target instanceof StepCell)
    {
      return true;
    }

    return false;
  }

  public static boolean connect(WorkflowCell source, WorkflowCell target, WorkflowGraphModel model)
  {

    if(!isConnectable(source, target))
    {
      return false;
    }

    if(source instanceof SplitCell && target instanceof StepCell)
    {
      return connectSplitToStep((SplitCell)source, (StepCell)target, model);
    }
    else if(source instanceof InitialActionCell && target instanceof StepCell)
    {
      return connectStartToStep((InitialActionCell)source, (StepCell)target, model);
    }
    else if(source instanceof JoinCell && target instanceof StepCell)
    {
      return connectJoinToStep((JoinCell)source, (StepCell)target, model);
    }
    else if(source instanceof StepCell && !(target instanceof InitialActionCell))
    {
      return connectStepTo((StepCell)source, target, model);
    }
    return true;
  }

  private static boolean isConnected(List results, AbstractDescriptor desc)
  {
    if(results == null || results.size() == 0)
    {
      return false;
    }

    if(desc instanceof StepDescriptor)
    {
      return _isConnected(results, (StepDescriptor)desc);
    }
    else if(desc instanceof SplitDescriptor)
    {
      return _isConnected(results, (SplitDescriptor)desc);
    }
    else if(desc instanceof JoinDescriptor)
    {
      return _isConnected(results, (JoinDescriptor)desc);
    }
    return false;
  }

  private static boolean _isConnected(List results, StepDescriptor step)
  {
    if(results == null)
    {
      return false;
    }
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      if(result.getStep() == step.getId())
      {
        // already connected
        return true;
      }
    }
    return false;
  }

  private static boolean _isConnected(List results, JoinDescriptor join)
  {
    if(results == null)
    {
      return false;
    }
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      if(result.getJoin() == join.getId())
      {
        // already connected
        return true;
      }
    }
    return false;
  }

  private static boolean _isConnected(List results, SplitDescriptor split)
  {
    if(results == null)
    {
      return false;
    }
    for(int i = 0; i < results.size(); i++)
    {
      ResultDescriptor result = (ResultDescriptor)results.get(i);
      if(result.getSplit() == split.getId())
      {
        // already connected
        return true;
      }
    }
    return false;
  }

  public static boolean isConnected(ResultDescriptor result, AbstractDescriptor desc)
  {
    if(result == null)
    {
      return false;
    }
    if(desc instanceof StepDescriptor)
    {
      return _isConnected(result, (StepDescriptor)desc);
    }
    else if(desc instanceof JoinDescriptor)
    {
      return _isConnected(result, (JoinDescriptor)desc);
    }
    else if(desc instanceof SplitDescriptor)
    {
      return _isConnected(result, (SplitDescriptor)desc);
    }

    return false;
  }

  private static boolean _isConnected(ResultDescriptor result, StepDescriptor step)
  {
    return result.getStep() == step.getId();
  }

  private static boolean _isConnected(ResultDescriptor result, JoinDescriptor join)
  {
    return result.getJoin() == join.getId();
  }

  private static boolean _isConnected(ResultDescriptor result, SplitDescriptor split)
  {
    return result.getSplit() == split.getId();
  }

  private static int getConnectType(ActionDescriptor from, AbstractDescriptor to)
  {
    List results = from.getConditionalResults();
    if(isConnected(results, to))
    {
      return UNKNOWN;
    }

    ResultDescriptor result = from.getUnconditionalResult();
    if(isConnected(result, to))
    {
      return UNKNOWN;
    }

    int type;
    if(result != null)
    {
      type = CONDITIONAL;
    }
    else
    {
      // choose unconditional or conditional
      String conditional = ResourceManager.getString("result.conditional");
      String unconditional = ResourceManager.getString("result.unconditional");
      String cancel = ResourceManager.getString("cancel");
      type = JOptionPane.showOptionDialog(null, ResourceManager.getString("result.select.long"), ResourceManager.getString("result.select"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{conditional, unconditional, cancel}, conditional);
      if(type != 0 && type != 1)
      {
        // cancel
        return UNKNOWN;
      }
    }
    return type;
  }

  private static boolean connectStepTo(StepCell source, WorkflowCell target, WorkflowGraphModel model)
  {
    AbstractDescriptor to;
    if(target instanceof StepCell)
    {
      to = ((StepCell)target).getDescriptor();
    }
    else if(target instanceof SplitCell)
    {
      to = ((SplitCell)target).getSplitDescriptor();
    }
    else if(target instanceof JoinCell)
    {
      to = ((JoinCell)target).getJoinDescriptor();
    }
    else
    {
      return false;
    }

    ResultDescriptor result;

    ActionTypeDialog selectType = new ActionTypeDialog(WorkflowDesigner.INSTANCE, source.getDescriptor());
    selectType.setModel(model);
    if(!selectType.ask(WorkflowDesigner.INSTANCE)) return false;

    ActionDescriptor sourceAction = selectType.getRelatedAction();

    int type = selectType.getType();

    if(type == CONDITIONAL)
    {
      result = DescriptorBuilder.createConditionalResult(model, to);
    }
    else if(type == UNCONDITIONAL)
    {
      result = DescriptorBuilder.createResult(model, to);
    }
    else
    {
      return false;
    }

    result.setParent(sourceAction);
    if(type == CONDITIONAL)
    {
      sourceAction.getConditionalResults().add(result);
    }
    else
    {
      sourceAction.setUnconditionalResult(result);
    }

    model.recordResult(source, result, sourceAction);
    model.connectCells(source, sourceAction, target, result);

		// update the workspace navigator
		WorkflowDescriptor workflow = WorkflowDesigner.INSTANCE.getCurrentGraph().getDescriptor();   
		WorkflowDesigner.INSTANCE.navigator().reloadStep(workflow, source.getDescriptor());
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, result);	
		
    return true;
  }

  private static boolean connectStartToStep(InitialActionCell source, StepCell target, WorkflowGraphModel model)
  {
    ActionDescriptor start = source.getActionDescriptor();
    StepDescriptor step = target.getDescriptor();
    ResultDescriptor result;

    int type = getConnectType(start, step);

    if(type == CONDITIONAL)
    {
      result = DescriptorBuilder.createConditionalResult(model, step);
    }
    else if(type == UNCONDITIONAL)
    {
      result = DescriptorBuilder.createResult(model, step);
    }
    else
    {
      return false;
    }

    result.setParent(start);
    if(type == CONDITIONAL)
    {
      start.getConditionalResults().add(result);
    }
    else
    {
      start.setUnconditionalResult(result);
    }

    // create new resultCell
    model.recordResult(source, result, start);
    model.connectCells(source, start, target, result);

		//	update the workspace navigator
		WorkflowDescriptor workflow = WorkflowDesigner.INSTANCE.getCurrentGraph().getDescriptor();   
		WorkflowDesigner.INSTANCE.navigator().reloadInitialAction(workflow);
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, result);
		
    return true;
  }

  private static boolean connectSplitToStep(SplitCell source, StepCell target, WorkflowGraphModel model)
  {
    SplitDescriptor split = source.getSplitDescriptor();
    StepDescriptor step = target.getDescriptor();

    List results = split.getResults();

    if(isConnected(results, step))
    {
      return false;
    }

    // create new unconditional result
    ResultDescriptor result = DescriptorBuilder.createResult(model, step);
    result.setParent(split);

    // add to split's result list
    results.add(result);

    model.recordResult(source, result, null);

    // update the graph
    model.connectCells(source, null, target, result);

		// update the workspace navigator
		WorkflowDescriptor workflow = WorkflowDesigner.INSTANCE.getCurrentGraph().getDescriptor();   
		WorkflowDesigner.INSTANCE.navigator().reloadSplit(workflow, source.getSplitDescriptor()); 
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, result);
		
    return true;
  }

  private static boolean connectJoinToStep(JoinCell source, StepCell target, WorkflowGraphModel model)
  {
    JoinDescriptor join = source.getJoinDescriptor();
    StepDescriptor step = target.getDescriptor();

    ResultDescriptor result = join.getResult();
    if(result != null)
    {
      return false;
    }

    // create new unconditional result
    result = DescriptorBuilder.createResult(model, step);
    result.setParent(join);
    join.setResult(result);

    model.recordResult(source, result, null);

    // update the graph
    model.connectCells(source, null, target, result);

		// update the workspace navigator
		WorkflowDescriptor workflow = WorkflowDesigner.INSTANCE.getCurrentGraph().getDescriptor();   
		WorkflowDesigner.INSTANCE.navigator().reloadJoin(workflow, source.getJoinDescriptor()); 
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, result);
		
    return true;
  }

}
