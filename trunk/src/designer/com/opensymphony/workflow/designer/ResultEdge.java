package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.GraphConstants;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 20, 2003
 *         Time: 3:27:52 PM
 */
public class ResultEdge extends WorkflowEdge
{
  //private static final EdgeRouter EDGE_ROUTER = new EdgeRouter();

  public ResultEdge(ResultDescriptor descriptor, Point2D labelPos)
  {
    super(descriptor);
    setAttributes(new WorkflowAttributeMap(getAttributes()));
    getAttributes().put("descriptor", descriptor);
    int arrow = GraphConstants.ARROW_CLASSIC;
    //GraphConstants.setLabelAlongEdge(attributes, true);
    GraphConstants.setLineEnd(attributes, arrow);
    GraphConstants.setEndFill(attributes, true);
    GraphConstants.setDisconnectable(attributes, true);
    GraphConstants.setBendable(attributes, true);
    GraphConstants.setForeground(attributes, Color.BLACK);
    //GraphConstants.setRouting(attributes, EDGE_ROUTER);
    GraphConstants.setMoveable(attributes, true);
    if(labelPos != null)
    {
      GraphConstants.setLabelPosition(attributes, new Point((int)labelPos.getX(), (int)labelPos.getY()));
    }
  }

  public ResultEdge(ResultDescriptor descriptor, Point2D labelPos, java.util.List routingPoints)
  {
    this(descriptor, labelPos);
    java.util.List newpoints = new ArrayList();
    newpoints.add(new Point(0, 0));
    for(int i = 0; i < routingPoints.size(); i++)
    {
      newpoints.add(new Point(((Point)routingPoints.get(i)).x, ((Point)routingPoints.get(i)).y));
    }
    newpoints.add(new Point(0, 0));
    GraphConstants.setPoints(attributes, newpoints);
  }

  public ResultEdge(ResultDescriptor descriptor, Point2D labelPos, float lineWidth, Color color, java.util.List routingPoints)
  {
    this(descriptor, labelPos, routingPoints);
    GraphConstants.setLineWidth(attributes, lineWidth);
    GraphConstants.setForeground(attributes, color);
  }

  public ResultDescriptor getDescriptor()
  {
    return (ResultDescriptor)getAttributes().get("descriptor");
  }

  public void setAutoroute()
  {
    GraphConstants.setRouting(attributes, new DefaultRouting());
  }

  public String toString()
  {
    ResultDescriptor descriptor = getDescriptor();
    if(descriptor == null) return null;
    if(descriptor.getDisplayName() != null)
    {
      if(descriptor.getDisplayName().length() > 0)
      {
        return descriptor.getDisplayName();
      }
    }
    if(descriptor.getParent() instanceof ActionDescriptor)
    {
      return ((ActionDescriptor)descriptor.getParent()).getName();
    }
    else if(descriptor.getParent() instanceof SplitDescriptor)
    {
      return "Split #" + descriptor.getParent().getId();
    }
    else if(descriptor.getParent() instanceof JoinDescriptor)
    {
      return "Join #" + descriptor.getParent().getId();
    }
    return "<unknown>";
  }
}
