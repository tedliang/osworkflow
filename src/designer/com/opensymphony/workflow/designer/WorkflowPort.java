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

  /**
   * Check all existing edges for this port and assign the edge an index.
   * If there are no edges that would overlap edge, then the index
   * is 0. The index ensures that it is possible to uniquely
   * identify any overlapping edges.
   * @param edge
   */
  public void assignIndex(ResultEdge edge)
  {
    WorkflowPort source = (WorkflowPort)edge.getSource();
    WorkflowPort target = (WorkflowPort)edge.getTarget();
    int index = 0;
    Iterator i = edges.iterator();
    while(i.hasNext())
    {
      ResultEdge e = (ResultEdge)i.next();
      if(e.getTarget().equals(source) && e.getSource().equals(target))
      {
        index++;
      }
    }
    edge.setIndex(index);
  }
}
