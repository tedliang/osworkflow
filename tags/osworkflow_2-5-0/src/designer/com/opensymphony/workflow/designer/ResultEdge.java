package com.opensymphony.workflow.designer;

import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 3:27:52 PM
 */
public class ResultEdge extends WorkflowEdge
{
  private ResultDescriptor descriptor;

  public ResultDescriptor getDescriptor()
  {
    return descriptor;
  }

  public void setDescriptor(ResultDescriptor descriptor)
  {
    this.descriptor = descriptor;
  }
}
