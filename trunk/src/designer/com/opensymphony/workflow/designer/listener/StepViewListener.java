package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;

/**
 * @author baab
 */
public class StepViewListener extends TextFieldListener
{
  StepDescriptor step;

  public void setStep(StepDescriptor step)
  {
    this.step = step;
  }

  protected void valueChanged(String msg)
  {
    if(step.getActions().size()==0) return;
    ActionDescriptor action = (ActionDescriptor)step.getActions().get(0);
    action.setView(msg);
  }
}
