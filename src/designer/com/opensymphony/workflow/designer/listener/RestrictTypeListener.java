package com.opensymphony.workflow.designer.listener;

import com.opensymphony.workflow.loader.RestrictionDescriptor;

/**
 * @author baab
 */
public class RestrictTypeListener extends ComboListener
{

  private RestrictionDescriptor restrict;

  public void setRestrict(RestrictionDescriptor restrict)
  {
    this.restrict = restrict;
  }

  protected void valueChanged(String newValue)
  {
    restrict.setConditionType(newValue);
  }

}
