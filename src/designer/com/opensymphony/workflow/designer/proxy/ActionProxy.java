package com.opensymphony.workflow.designer.proxy;

import com.opensymphony.workflow.loader.ActionDescriptor;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Gulei
 */
public class ActionProxy implements DefaultGraphCell.ValueChangeHandler
{
  private ActionDescriptor action;

  public ActionProxy(Object obj)
  {
    if(obj == null || !(obj instanceof ActionDescriptor))
    {
      action = null;
      if(obj!=null)
      {
        System.out.println("WARNING Unexpected action " + obj);
        new Throwable().printStackTrace();
      }
    }
    else
    {
      action = (ActionDescriptor)obj;
    }
  }

  public String toString()
  {
    return action == null ? "" : action.getName();
  }

  public Object valueChanged(Object newValue)
  {
    if(action==null) return "";
    String name = action.getName();
    action.setName(newValue.toString());
    return name;
  }

  public Object clone()
  {
    System.out.println(getClass() + " WARNING: Unexpected clone called on " + this);
    return this;
  }
}
