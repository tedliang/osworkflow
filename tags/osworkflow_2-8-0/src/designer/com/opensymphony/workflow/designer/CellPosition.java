package com.opensymphony.workflow.designer;

import java.io.PrintWriter;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.jgraph.graph.GraphConstants;

import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.util.XMLizable;

public class CellPosition implements XMLizable
{
  private int id = 0;
	private String type;
  private double height;
  private double width;
  private double x;
  private double y;

  public CellPosition(WorkflowCell cell)
  {
    id = cell.getId();
	  type = cell.getClass().getName();
	  type = type.substring(type.lastIndexOf('.')+1, type.length());
    Rectangle2D bounds = GraphConstants.getBounds(cell.getAttributes());
    height = bounds.getHeight();
    width = bounds.getWidth();
    x = bounds.getX();
    y = bounds.getY();
  }

  public CellPosition(Element activity)
  {
    try
    {
      id = Integer.parseInt(activity.getAttribute("id"));
	    type = activity.getAttribute("type");
      height = Double.parseDouble(activity.getAttribute("height"));
      width = Double.parseDouble(activity.getAttribute("width"));
      x = Double.parseDouble(activity.getAttribute("x"));
      y = Double.parseDouble(activity.getAttribute("y"));
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

  public double[] getBounds2D()
  {
    return new double[]{x, y, width, height};
  }
}
