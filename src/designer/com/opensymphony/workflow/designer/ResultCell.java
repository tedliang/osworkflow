/**
 * Created on Feb 12, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import org.jgraph.graph.DefaultGraphCell;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;

public class ResultCell extends WorkflowCell implements Keyable
{
  private DefaultGraphCell fromCell;
  private ResultDescriptor descriptor;
  //the action that causes this result
  private ActionDescriptor action;

  //   private String mKey;
  public ResultCell(DefaultGraphCell fromCell, ResultDescriptor resultDescriptor, ActionDescriptor action)
  {
    super(action);
    this.fromCell = fromCell;
    descriptor = resultDescriptor;
    id = descriptor.getId();
    this.action = action;
  }

  public String getKey()
  {
    return null;
  }

  public DefaultGraphCell getFromCell()
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
