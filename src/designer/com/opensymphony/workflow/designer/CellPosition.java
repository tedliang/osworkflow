package com.opensymphony.workflow.designer;

import java.io.PrintWriter;
import java.awt.*;

import org.w3c.dom.Element;

import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;

public class CellPosition implements XMLizable
{
  private int id = 0;
	private String type;
  private int height;
  private int width;
  private int x;
  private int y;

  public CellPosition(WorkflowCell cell)
  {
    id = cell.getId();
	  type = cell.getClass().getName();
	  type = type.substring(type.lastIndexOf('.')+1, type.length());
    Rectangle bounds = (Rectangle)cell.getAttributes().get("bounds");
    height = bounds.height;
    width = bounds.width;
    x = bounds.x;
    y = bounds.y;
  }

  public CellPosition(Element activity)
  {
    try
    {
      id = Integer.parseInt(activity.getAttribute("id"));
	    type = activity.getAttribute("type");
      height = Integer.parseInt(activity.getAttribute("height"));
      width = Integer.parseInt(activity.getAttribute("width"));
      x = Integer.parseInt(activity.getAttribute("x"));
      y = Integer.parseInt(activity.getAttribute("y"));
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

	public String getType()
	{
		return type;
	}

  public void writeXML(PrintWriter out, int indent)
  {
    XMLUtil.printIndent(out, indent++);
    StringBuffer buf = new StringBuffer();
    buf.append("<cell ");
    buf.append("id=\"").append(id).append("\"");
	  buf.append(" type=\"").append(type).append("\"");
    buf.append(" height=\"").append(height).append("\"");
    buf.append(" width=\"").append(width).append("\"");
    buf.append(" x=\"").append(x).append("\"");
    buf.append(" y=\"").append(y).append("\"");

    buf.append("/>");
    out.println(buf.toString());
  }

  public int[] getBounds()
  {
    return new int[]{x, y, width, height};
  }
}
