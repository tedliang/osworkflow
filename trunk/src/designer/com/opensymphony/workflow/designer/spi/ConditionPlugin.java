package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;

import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author Gulei
 */
public interface ConditionPlugin
{
  public void setCondition(ConfigConditionDescriptor descriptor);

  public ConfigConditionDescriptor getCondition();

  public boolean editCondition(Map args, Component parent);

}
