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
    if(newValue instanceof String)
    {
      if(result == null) return "";
      String name = result.getDisplayName();
      boolean hasNickname = (name != null);
      if(hasNickname) hasNickname &= (name.length() > 0);
      if((!hasNickname) && (result.getParent() instanceof ActionDescriptor))
      {
        if(((ActionDescriptor)result.getParent()).getConditionalResults() != null)
        {
          if(((ActionDescriptor)result.getParent()).getConditionalResults().isEmpty())
          {
            ((ActionDescriptor)result.getParent()).setName(newValue.toString());
            return (name != null) ? name : "";
          }
        }
      }
      result.setDisplayName(newValue.toString());
      return (name != null) ? name : "";
    }
    return newValue;
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
