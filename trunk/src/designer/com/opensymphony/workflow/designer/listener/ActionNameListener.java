package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.ActionDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class ActionNameListener extends TextFieldListener
{
  private ActionDescriptor action;

  public void setAction(ActionDescriptor action)
  {
    this.action = action;
  }

  protected void valueChanged(String msg)
  {
    action.setName(msg);
  }

}
