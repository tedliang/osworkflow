package com.opensymphony.workflow.designer.proxy;

import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.DefaultGraphCell;

public class ResultProxy implements DefaultGraphCell.ValueChangeHandler
{
  private ResultDescriptor result;

  public ResultProxy(ResultDescriptor d)
  {
    result = d;
  }

  public String toString()
  {
    if(result == null) return null;
    if(result.getDisplayName() != null)
    {
      if(result.getDisplayName().length() > 0)
      {
        return result.getDisplayName();
      }
    }
    if(result.getParent() instanceof ActionDescriptor)
    {
      return ((ActionDescriptor)result.getParent()).getName();
    }
    else if(result.getParent() instanceof SplitDescriptor)
    {
      return "Split #" + result.getParent().getId();
    }
    else if(result.getParent() instanceof JoinDescriptor)
    {
      return "Join #" + result.getParent().getId();
    }
    return "<unknown>";
  }

  public Object valueChanged(Object newValue)
  {
    if(result == null) return "";
    String name = result.getDisplayName();
    if(name == null) return "";

    if((name != null) && (name.length() > 0))
    {
      result.setDisplayName(newValue.toString());
      return name;
    }
    if(result.getParent() instanceof ActionDescriptor)
    {
      ((ActionDescriptor)result.getParent()).setName(newValue.toString());
    }
    return name;
  }

  public Object clone()
  {
    System.out.println(getClass() + " WARNING: Unexpected clone called on " + this);
    return this;
  }

  public ResultDescriptor getDescriptor()
  {
    return result;
  }
}
