package com.opensymphony.workflow.designer;

import java.io.PrintWriter;
import java.awt.*;

import org.w3c.dom.Element;

import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;

public class Activity implements XMLizable
{
  String id = "";
  private Rectangle bounds;

  public Activity()
  {

  }

  public Activity(WorkflowCell cell)
  {
    id = cell.toString();
    bounds = (Rectangle)cell.getAttributes().get("bounds");
  }

  public Activity(Element activity)
  {
    id = activity.getAttribute("id");
    bounds = new Rectangle();
    bounds.height = Integer.parseInt(activity.getAttribute("height"));
    bounds.width = Integer.parseInt(activity.getAttribute("width"));
    bounds.x = Integer.parseInt(activity.getAttribute("x"));
    bounds.y = Integer.parseInt(activity.getAttribute("y"));
  }

  /**
   * Returns the id.
   * @return String
   */
  public String getId()
  {
    return id;
  }

  /**
   * Sets the id.
   * @param id The id to set
   */
  public void setId(String id)
  {
    this.id = id;
  }

  public void writeXML(PrintWriter out, int indent)
  {
    XMLUtil.printIndent(out, indent++);
    StringBuffer buf = new StringBuffer();
    buf.append("<activity ");
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
