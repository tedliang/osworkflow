package com.opensymphony.workflow.designer;

import java.awt.*;
import java.io.InputStream;
import java.io.PrintWriter;
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
        Rectangle bound = pos.getBounds();
        allCells.put(new Integer(pos.getId()), bound);
	      Map map = (Map)cellsByType.get(pos.getType());
	      if(map==null)
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
        Point p = pos.getLabelPosition();
        results.put(new Integer(pos.getId()), p);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void writeXML(PrintWriter out, int indent)
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
        ResultPosition pos = new ResultPosition(edge);
        pos.writeXML(out, indent);
      }
    }
    XMLUtil.printIndent(out, --indent);
    out.println("</layout>");
    out.flush();
    out.close();
  }

	public Rectangle getBounds(int key, Class type)
	{
		if(type==null)
		  return (Rectangle)allCells.get(new Integer(key));
		String typeName = type.getName();
		typeName = typeName.substring(typeName.lastIndexOf('.')+1, typeName.length());
		Map typeMap = (Map)cellsByType.get(typeName);
		if(typeMap==null)
		{
			return null;
		}
		Rectangle bounds = (Rectangle)typeMap.get(new Integer(key));
		return bounds;
	}

  public Point getLabelPosition(int resultKey)
  {
    return (Point)results.get(new Integer(resultKey));
  }
}
