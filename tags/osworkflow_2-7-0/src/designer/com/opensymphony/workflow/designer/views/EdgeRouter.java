package com.opensymphony.workflow.designer.views;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;

import com.opensymphony.workflow.designer.WorkflowPort;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 1:45:47 PM
 */
public class EdgeRouter implements Edge.Routing
{
  public void route(EdgeView edge, java.util.List points)
  {
    PortView sourceView = (PortView)edge.getSource();
    PortView targetView = (PortView)edge.getTarget();
    if(sourceView==null || targetView==null) return;

    WorkflowPort sourcePort = (WorkflowPort)sourceView.getCell();
    WorkflowPort targetPort = (WorkflowPort)targetView.getCell();
    Point2D from = sourceView.getLocation(null);
    Point2D to = targetView.getLocation(null);
    //check if this is a dup route
//    Collection duplicates = new HashSet();
//    if(sourcePort!=targetPort)
//    {
//      Iterator iter = sourcePort.edges();
//      while(iter.hasNext())
//      {
//        Edge e = (Edge)iter.next();
//        if(e.getTarget()==sourcePort && e.getSource()==targetPort && e!=edge.getCell())
//        {
//          duplicates.add(e);
//          System.out.println("detected duplicate line for edge " + e);
//        }
//      }
//    }
    if(from != null && to != null)
    {
      Point[] routed;
      // Handle self references
      if(sourcePort == targetPort)
      {
        Rectangle2D bounds = sourceView.getParentView().getBounds();
        int size = 35;
        routed = new Point[4];
        routed[0] = new Point((int)(bounds.getX() + bounds.getWidth()), (int)(bounds.getY() + bounds.getHeight()));
        routed[1] = new Point((int)(bounds.getX() + bounds.getWidth()), (int)(bounds.getY() + bounds.getHeight() + size));
        routed[2] = new Point((int)(bounds.getX() + size + bounds.getWidth()), (int)(bounds.getY() + bounds.getHeight() + size));
        routed[3] = new Point((int)(bounds.getX() + size + bounds.getWidth()), (int)(bounds.getY() + bounds.getHeight()));
      }
      else
      {
        double dx = Math.abs(from.getX() - to.getX());
        double dy = Math.abs(from.getY() - to.getY());
        double x2 = from.getX() + ((to.getX() - from.getX()) / 2);
        double y2 = from.getY() + ((to.getY() - from.getY()) / 2);
        routed = new Point[2];
        if(dx > dy)
        {
          routed[0] = new Point((int)x2, (int)from.getY());
          routed[1] = new Point((int)x2, (int)to.getY());
        }
        else
        {
          routed[0] = new Point((int)from.getX(), (int)y2);
          routed[1] = new Point((int)to.getX(), (int)y2);
        }
      }
      // Set/Add Points
      for(int i = 0; i < routed.length; i++)
        if(points.size() > i + 2)
          points.set(i + 1, routed[i]);
        else
          points.add(i + 1, routed[i]);
      // Remove spare points
      while(points.size() > routed.length + 2)
      {
        points.remove(points.size() - 2);
      }
    }
  }

}
