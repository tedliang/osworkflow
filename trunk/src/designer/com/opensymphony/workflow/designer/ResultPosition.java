package com.opensymphony.workflow.designer;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.opensymphony.workflow.designer.views.CustomEdgeView;
import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;
import org.jgraph.graph.GraphConstants;
import org.w3c.dom.Element;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 1 2003
 *         Time: 10:09:13 PM
 */
public class ResultPosition implements XMLizable
{
  private Point labelPos;
  private int id;
  private float lineWidth = 1;
  private int color = 0;
  private java.util.List routingPoints = new ArrayList();

  public ResultPosition(WorkflowGraph graph, ResultEdge edge)
  {
    id = edge.getDescriptor().getId();
    labelPos = GraphConstants.getLabelPosition(edge.getAttributes());
    lineWidth = GraphConstants.getLineWidth(edge.getAttributes());
    color = GraphConstants.getForeground(edge.getAttributes()).getRGB();
    CustomEdgeView view = (CustomEdgeView)(graph.getGraphLayoutCache().getMapping(edge, false));
    if(view != null)
    {
      for(int i = 0; i < (view.getPointCount() - 2); i++)
      {
        routingPoints.add(new Point((int)view.getPoint(i + 1).getX(), (int)view.getPoint(i + 1).getY()));
      }
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

      labelPos = new Point();
      attr = edge.getAttribute("labelx");
      if(attr != null && attr.length() > 0)
        labelPos.x = Integer.parseInt(attr);

      attr = edge.getAttribute("labely");
      if(attr != null && attr.length() > 0)
        labelPos.y = Integer.parseInt(attr);
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
    buf.append(" labelx=\"").append(labelPos.x).append('\"');
    buf.append(" labely=\"").append(labelPos.y).append('\"');
    buf.append('>');
    writer.println(buf.toString());
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

  public Point getLabelPosition()
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
}
