package com.opensymphony.workflow.designer;

import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 3:27:52 PM
 */
public class ResultEdge extends WorkflowEdge
{
  private int index;

  private ResultDescriptor descriptor;

  public ResultDescriptor getDescriptor()
  {
    return descriptor;
  }

  public void setDescriptor(ResultDescriptor descriptor)
  {
    this.descriptor = descriptor;
  }

  /**
   * The index of the result.
   * For any given source and target, each result between them
   * (in any direction) will have a unique index.
   * @return
   */
  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }
}
