package com.opensymphony.workflow.designer;

import java.awt.*;

import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.designer.views.EdgeRouter;
import org.jgraph.graph.GraphConstants;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 3:27:52 PM
 */
public class ResultEdge extends WorkflowEdge
{
  private static final EdgeRouter EDGE_ROUTER = new EdgeRouter();
  private int index;

  private ResultDescriptor descriptor;

  public ResultEdge(ResultDescriptor descriptor, Point labelPos)
  {
    setDescriptor(descriptor);
    int arrow = GraphConstants.ARROW_CLASSIC;
    GraphConstants.setLineEnd(attributes, arrow);
    GraphConstants.setEndFill(attributes, true);
    GraphConstants.setDisconnectable(attributes, false);
    GraphConstants.setRouting(attributes, EDGE_ROUTER);
    if(labelPos!=null)
    {
      GraphConstants.setLabelPosition(attributes, labelPos);
    }
  }

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
