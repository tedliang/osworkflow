package com.opensymphony.workflow.designer;

import org.jgraph.graph.DefaultPort;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 3:12:13 PM
 */
public class WorkflowPort extends DefaultPort
{
  private int index;

  public WorkflowPort()
  {
  }

  public WorkflowPort(int index)
  {
    this.index = index;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public int getEdgeCount()
  {
    return edges.size();
  }
}
