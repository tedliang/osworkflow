package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.StepDescriptor;

/**
 * @author baab
 */
public class StepNameListener extends TextFieldListener
{
  StepDescriptor step;

  public void setStep(StepDescriptor step)
  {
    this.step = step;
  }

  protected void valueChanged(String msg)
  {
    step.setName(msg);
  }
}
