package com.opensymphony.workflow.designer;

import java.io.PrintWriter;
import java.awt.*;

import com.opensymphony.workflow.util.XMLizable;
import com.opensymphony.workflow.loader.XMLUtil;
import org.jgraph.graph.GraphConstants;
import org.w3c.dom.Element;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Dec 1 2003
 * Time: 10:09:13 PM
 */
public class ResultPosition implements XMLizable
{
  private Point labelPos;
  private int id;

  public ResultPosition(ResultEdge edge)
  {
    id = edge.getDescriptor().getId();
    labelPos = GraphConstants.getLabelPosition(edge.getAttributes());
  }

  public ResultPosition(Element edge)
  {
    try
    {
      id = Integer.parseInt(edge.getAttribute("id"));
      labelPos = new Point();
      labelPos.x = Integer.parseInt(edge.getAttribute("labelx"));
      labelPos.y = Integer.parseInt(edge.getAttribute("labely"));
    }
    catch(Exception e)
    {
      System.out.println("Error parsing result position:" + e);
    }
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    XMLUtil.printIndent(writer, indent++);
    StringBuffer buf = new StringBuffer();
    buf.append("<connector ");
    buf.append("id=\"").append(id).append("\"");
    buf.append(" labelx=\"").append(labelPos.x).append("\"");
    buf.append(" labely=\"").append(labelPos.y).append("\"");
    buf.append("/>");
    writer.println(buf.toString());
  }

  public Point getLabelPosition()
  {
    return labelPos;
  }

  public int getId()
  {
    return id;
  }
}
