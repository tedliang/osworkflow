package com.opensymphony.workflow.designer;

import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;

public class ResultHolder
{
  private WorkflowCell fromCell;
  private ResultDescriptor descriptor;
  //the action that causes this result
  private ActionDescriptor action;

  public ResultHolder(WorkflowCell fromCell, ResultDescriptor resultDescriptor, ActionDescriptor action)
  {
    this.fromCell = fromCell;
    descriptor = resultDescriptor;
    this.action = action;
  }

  public WorkflowCell getFromCell()
  {
    return fromCell;
  }

  public int getStep()
  {
    return descriptor.getStep();
  }

  public ResultDescriptor getDescriptor()
  {
    return descriptor;
  }

  public int getSplit()
  {
    return descriptor.getSplit();
  }

  public int getJoin()
  {
    return descriptor.getJoin();
  }

  public ActionDescriptor getAction()
  {
    return action;
  }
}
