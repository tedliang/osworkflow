package com.opensymphony.workflow.designer;

import java.awt.*;

import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;

import org.jgraph.graph.GraphConstants;

public class JoinCell extends WorkflowCell implements ResultAware
{
  private JoinDescriptor descriptor;

  public JoinCell(JoinDescriptor userObject)
  {
    super(userObject);
    descriptor = userObject;
    id = descriptor.getId();
    GraphConstants.setBackground(attributes, Color.gray);
  }

  public JoinDescriptor getJoinDescriptor()
  {
    return descriptor;
  }

  public String toString()
  {
    return "Join id " + descriptor.getId();
  }

  public boolean removeResult(ResultDescriptor result)
  {
    if(descriptor.getResult() == result)
    {
      descriptor.setResult(null);
      return true;
    }
    return false;
  }
}
