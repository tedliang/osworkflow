package com.opensymphony.workflow.designer.proxy;

import com.opensymphony.workflow.loader.StepDescriptor;

import org.jgraph.graph.DefaultGraphCell;

/**
 * @author baab
 */
public class StepProxy implements DefaultGraphCell.ValueChangeHandler
{
  private StepDescriptor step;

  public StepProxy(StepDescriptor step)
  {
    this.step = step;
  }

  public String toString()
  {
    return step.getName();
  }

  public Object valueChanged(Object newValue)
  {
    String name = step.getName();
    step.setName(newValue.toString());
    return name;
  }

  public Object clone()
  {
    System.out.println(getClass() + " WARNING: Unexpected clone called on " + this);
    return this;
  }
}
