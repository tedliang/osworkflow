package com.opensymphony.workflow.designer.views;

import java.awt.*;
import java.util.*;

import org.jgraph.graph.*;
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
    CustomPortView sourceView = (CustomPortView)edge.getSource();
    CustomPortView targetView = (CustomPortView)edge.getTarget();
    if(sourceView==null || targetView==null) return;

    WorkflowPort sourcePort = (WorkflowPort)sourceView.getCell();
    WorkflowPort targetPort = (WorkflowPort)targetView.getCell();
    Point from = sourceView.getLocation(null);
    Point to = targetView.getLocation(null);
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
        Rectangle bounds = sourceView.getParentView().getBounds();
        int size = 35;
        routed = new Point[4];
        routed[0] = new Point(bounds.x + bounds.width, bounds.y + bounds.height);
        routed[1] = new Point(bounds.x + bounds.width, bounds.y + bounds.height + size);
        routed[2] = new Point(bounds.x + size + bounds.width, bounds.y + bounds.height + size);
        routed[3] = new Point(bounds.x + size + bounds.width, bounds.y + bounds.height);
      }
      else
      {
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        int x2 = from.x + ((to.x - from.x) / 2);
        int y2 = from.y + ((to.y - from.y) / 2);
        routed = new Point[2];
        if(dx > dy)
        {
          routed[0] = new Point(x2, from.y);
          //new Point(to.x, from.y)
          routed[1] = new Point(x2, to.y);
        }
        else
        {
          routed[0] = new Point(from.x, y2);
          // new Point(from.x, to.y)
          routed[1] = new Point(to.x, y2);
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
