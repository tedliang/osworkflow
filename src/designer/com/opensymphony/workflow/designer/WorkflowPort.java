package com.opensymphony.workflow.designer;

import java.util.Iterator;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;

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

  public int getEdgeIndex(Edge e)
  {
    Iterator i = edges.iterator();
    int counter = -1;
    while(i.hasNext())
    {
      counter++;
      Edge edge = (Edge)i.next();
      if(edge==e) return counter;
    }
    return -1;
  }
}
