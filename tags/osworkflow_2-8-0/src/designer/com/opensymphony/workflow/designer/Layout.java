package com.opensymphony.workflow.designer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.opensymphony.workflow.loader.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Layout
{
  private String url;

  private Collection entries = new ArrayList();
  private Map allCells = new HashMap();
  private Map results = new HashMap();

  /**
   * A Map of Maps, with keys being type names.
   * Values are a map of id/bounds
   */
  private Map cellsByType = new HashMap();

  private static class ResultLayout
  {
    float lineWidth;
    int color;
    int from;
    int to;
    Point2D labelPosition;
    java.util.List routingPoints = new ArrayList();

    ResultLayout(Point2D labelPosition, float lineWidth, int color, int from, int to)
    {
      if(labelPosition != null)
        this.labelPosition = labelPosition;
      this.lineWidth = lineWidth;
      this.color = color;
      this.from = from;
      this.to = to;
    }
  }

  public Layout()
  {
  }

  public void setAllEntries(Collection entries)
  {
    this.entries = entries;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public Layout(InputStream in)
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db;
    try
    {
      db = dbf.newDocumentBuilder();
      Document doc;
      doc = db.parse(in);

      NodeList mActivitycell = doc.getElementsByTagName("cell");
      for(int k = 0; k < mActivitycell.getLength(); k++)
      {
        Element element = (Element)mActivitycell.item(k);
        CellPosition pos = new CellPosition(element);
        double[] bound = pos.getBounds2D();
        allCells.put(new Integer(pos.getId()), bound);
        Map map = (Map)cellsByType.get(pos.getType());
        if(map == null)
        {
          map = new HashMap();
          cellsByType.put(pos.getType(), map);
        }
        map.put(new Integer(pos.getId()), bound);
      }
      NodeList list = doc.getElementsByTagName("connector");
      for(int k = 0; k < list.getLength(); k++)
      {
        Element element = (Element)list.item(k);
        ResultPosition pos = new ResultPosition(element);
        ResultLayout r = new ResultLayout(pos.getLabelPosition(), pos.getLineWidth(), pos.getColor(), pos.getFrom(), pos.getTo());
        results.put(new Integer(pos.getId()), r);
      }
      NodeList routing = doc.getElementsByTagName("routing");
      for(int k = 0; k < routing.getLength(); k++)
      {
        Element element = (Element)routing.item(k);
        try
        {
          int id = Integer.parseInt(element.getAttribute("id"));
          int x = Integer.parseInt(element.getAttribute("x"));
          int y = Integer.parseInt(element.getAttribute("y"));
          ResultLayout result = (ResultLayout)results.get(new Integer(id));
          if(result != null)
          {
            result.routingPoints.add(new Point(x, y));
          }
        }
        catch(Exception e)
        {
          System.out.println("Error parsing routing position:" + e);
        }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

	public Layout(String in)
	{
    this(new ByteArrayInputStream(in.getBytes()));
	}
	
  public void writeXML(PrintWriter out, int indent, WorkflowGraph graph)
  {
    out.println("<?xml version=\"1.0\"?>");
    XMLUtil.printIndent(out, indent++);
    out.println("<layout>");

    Iterator it = entries.iterator();
    while(it.hasNext())
    {
      Object next = it.next();
      if(next instanceof WorkflowCell)
      {
        WorkflowCell cell = (WorkflowCell)next;
        CellPosition pos = new CellPosition(cell);
        pos.writeXML(out, indent);
      }
      else
      {
        ResultEdge edge = (ResultEdge)next;
        ResultPosition pos = new ResultPosition(graph, edge);
        pos.writeXML(out, indent);
      }
    }
    XMLUtil.printIndent(out, --indent);
    out.println("</layout>");
    out.flush();
    out.close();
  }

  /**
   * Get the boounds for the specified key/type.
   * @param key
   * @param type
   * @return double[] an array of 4 ints containing x, y, width, and height.
   */
  public double[] getBounds(int key, String type)
  {
    if(type == null)
      return (double[])allCells.get(new Integer(key));
    Map typeMap = (Map)cellsByType.get(type);
    if(typeMap == null)
    {
      return null;
    }
    double[] bounds = (double[])typeMap.get(new Integer(key));
    return bounds;
  }

  public int getColor(int resultKey)
  {
    ResultLayout rl = ((ResultLayout)results.get(new Integer(resultKey)));
    return rl != null  ? rl.color : 0;
  }

  public int getFromPort(int resultKey)
  {
    ResultLayout rl = ((ResultLayout)results.get(new Integer(resultKey)));
    return rl != null  ? rl.from : 0;
  }

  public int getToPort(int resultKey)
  {
    ResultLayout rl = ((ResultLayout)results.get(new Integer(resultKey)));
    return rl != null  ? rl.to : 0;
  }

  public float getLineWidth(int resultKey)
  {
    ResultLayout resultLayout = (ResultLayout)results.get(new Integer(resultKey));
    return resultLayout != null ? resultLayout.lineWidth : 2;
  }

  public Point2D getLabelPosition(int resultKey)
  {
    ResultLayout rl = ((ResultLayout)results.get(new Integer(resultKey)));
    return rl != null && rl.labelPosition != null ? rl.labelPosition : null;
  }

  public java.util.List getRoutingPoints(int resultKey)
  {
    ResultLayout rl = ((ResultLayout)results.get(new Integer(resultKey)));
    return rl != null && rl.routingPoints != null ? rl.routingPoints : Collections.EMPTY_LIST;
  }
}
