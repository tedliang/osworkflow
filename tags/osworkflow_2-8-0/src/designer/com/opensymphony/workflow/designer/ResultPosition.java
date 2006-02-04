package com.opensymphony.workflow.designer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.EdgeView;
import org.w3c.dom.Element;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 1 2003
 *         Time: 10:09:13 PM
 */
public class ResultPosition implements XMLizable
{
  private Point2D labelPos;
  private int id;
  private float lineWidth = 1;
  private int color = 0;
  private int from;
  private int to;
  private java.util.List routingPoints = new ArrayList();

  public ResultPosition(WorkflowGraph graph, ResultEdge edge)
  {
    id = edge.getDescriptor().getId();
    EdgeView view = (EdgeView)graph.getGraphLayoutCache().getMapping(edge, false);
    labelPos = view.getLabelPosition();
    from = ((WorkflowPort)((ResultEdge)view.getCell()).getSource()).getIndex();
    to = ((WorkflowPort)((ResultEdge)view.getCell()).getTarget()).getIndex();
    lineWidth = GraphConstants.getLineWidth(edge.getAttributes());
    color = GraphConstants.getForeground(edge.getAttributes()).getRGB();
    for(int i = 0; i < (view.getPointCount() - 2); i++)
    {
      routingPoints.add(new Point((int)view.getPoint(i + 1).getX(), (int)view.getPoint(i + 1).getY()));
    }
  }

  public ResultPosition(Element edge)
  {
    try
    {
      String attr = edge.getAttribute("id");
      if(attr != null && attr.length() > 0)
        id = Integer.parseInt(attr);

      attr = edge.getAttribute("linewidth");
      if(attr != null && attr.length() > 0)
        lineWidth = Float.parseFloat(attr);

      attr = edge.getAttribute("color");
      if(attr != null && attr.length() > 0)
        color = Integer.parseInt(attr);

      labelPos = new Point2D.Double();
      attr = edge.getAttribute("labelx");
      double x = 0;
      double y = 0;
      if(attr != null && attr.length() > 0)
        x = Double.parseDouble(attr);

      attr = edge.getAttribute("labely");
      if(attr != null && attr.length() > 0)
        y = Double.parseDouble(attr);
      if(x !=0 && y != 0)
      {
        labelPos.setLocation(x, y);
      }

      attr = edge.getAttribute("from");
      if(attr != null && attr.length() > 0)
        from = Integer.parseInt(attr);

      attr = edge.getAttribute("to");
      if(attr != null && attr.length() > 0)
        to = Integer.parseInt(attr);
    }
    catch(Exception e)
    {
      System.out.println("Error parsing result position:" + e);
    }
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    XMLUtil.printIndent(writer, indent);
    StringBuffer buf = new StringBuffer();
    buf.append("<connector ");
    buf.append("id=\"").append(id).append('\"');
    buf.append(" linewidth=\"").append(lineWidth).append('\"');
    buf.append(" color=\"").append(color).append('\"');
    buf.append(" labelx=\"").append(labelPos.getX()).append('\"');
    buf.append(" labely=\"").append(labelPos.getY()).append('\"');
    buf.append(" from=\"").append(from).append('\"');
    buf.append(" to=\"").append(to).append('\"');
    writer.print(buf.toString());
    if(routingPoints.size() > 0)
    {
      writer.println('>');
      for(int i = 0; i < routingPoints.size(); i++)
      {
        StringBuffer pointbuf = new StringBuffer();
        XMLUtil.printIndent(writer, indent + 1);
        pointbuf.append("<routing");
        pointbuf.append(" id=\"").append(id).append('\"');
        pointbuf.append(" x=\"").append(((Point)routingPoints.get(i)).x).append('\"');
        pointbuf.append(" y=\"").append(((Point)routingPoints.get(i)).y).append('\"');
        pointbuf.append("/>");
        writer.println(pointbuf.toString());
      }
      XMLUtil.printIndent(writer, indent++);
      writer.println("</connector>");
    }
    else
    {
      writer.println("/>");
    }
  }

  public Point2D getLabelPosition()
  {
    return labelPos;
  }

  public float getLineWidth()
  {
    return lineWidth;
  }

  public int getColor()
  {
    return color;
  }

  public int getId()
  {
    return id;
  }

  public int getFrom()
  {
    return from;
  }

  public int getTo()
  {
    return to;
  }
}
