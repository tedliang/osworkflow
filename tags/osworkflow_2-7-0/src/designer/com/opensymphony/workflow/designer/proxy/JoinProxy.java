package com.opensymphony.workflow.designer.proxy;

import com.opensymphony.workflow.loader.JoinDescriptor;

/**
 * @author baab
 */
public class JoinProxy
{
  private JoinDescriptor join;

  public JoinProxy(JoinDescriptor join)
  {
    this.join = join;
  }

  public String toString()
  {
    return "Join id " + join.getId();
  }

}
