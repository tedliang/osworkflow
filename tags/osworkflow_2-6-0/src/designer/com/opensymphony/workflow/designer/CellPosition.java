package com.opensymphony.workflow.designer;

import java.io.PrintWriter;
import java.awt.*;

import org.w3c.dom.Element;

import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;

public class CellPosition implements XMLizable
{
  private int id = 0;
  private Rectangle bounds;

  public CellPosition(WorkflowCell cell)
  {
    id = cell.getId();
    bounds = (Rectangle)cell.getAttributes().get("bounds");
  }

  public CellPosition(Element activity)
  {
    try
    {
      id = Integer.parseInt(activity.getAttribute("id"));
      bounds = new Rectangle();
      bounds.height = Integer.parseInt(activity.getAttribute("height"));
      bounds.width = Integer.parseInt(activity.getAttribute("width"));
      bounds.x = Integer.parseInt(activity.getAttribute("x"));
      bounds.y = Integer.parseInt(activity.getAttribute("y"));
    }
    catch(Exception e)
    {
      System.out.println("Error parsing cell position:" + e);
    }
  }

  public int getId()
  {
    return id;
  }

  public void writeXML(PrintWriter out, int indent)
  {
    XMLUtil.printIndent(out, indent++);
    StringBuffer buf = new StringBuffer();
    buf.append("<cell ");
    buf.append("id=\"").append(id).append("\"");
    buf.append(" height=\"").append(bounds.height).append("\"");
    buf.append(" width=\"").append(bounds.width).append("\"");
    buf.append(" x=\"").append(bounds.x).append("\"");
    buf.append(" y=\"").append(bounds.y).append("\"");

    buf.append("/>");
    out.println(buf.toString());
  }

  public Rectangle getBounds()
  {
    return bounds;
  }
}
