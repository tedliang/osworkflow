package com.opensymphony.workflow.designer;

import java.util.Iterator;

import org.jgraph.graph.DefaultPort;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 3:12:13 PM
 */
public class WorkflowPort extends DefaultPort
{
  public WorkflowPort()
  {
  }

  public int getEdgeCount()
  {
    return edges.size();
  }
}
