/**
 * Created on Feb 12, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import org.jgraph.graph.DefaultGraphCell;
import com.opensymphony.workflow.loader.ResultDescriptor;

public class ResultCell extends WorkflowCell implements Keyable
{
  private DefaultGraphCell fromCell;
  private ResultDescriptor descriptor;

  //   private String mKey;
  public ResultCell(DefaultGraphCell fromCell, ResultDescriptor resultDescriptor)
  {
    super(new Integer(resultDescriptor.getId()));
    this.fromCell = fromCell;
    descriptor = resultDescriptor;
    id = descriptor.getId();
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
}
