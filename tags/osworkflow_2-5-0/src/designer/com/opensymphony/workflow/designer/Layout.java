package com.opensymphony.workflow.designer;

import java.awt.Rectangle;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.opensymphony.workflow.loader.XMLUtil;

public class Layout
{
  private String url;

  Map map = new HashMap();
  Hashtable activities = new Hashtable();

  public Layout(Map map)
  {
    this.map = map;
  }

  public void setActivity(Map map)
  {
    this.map = map;
  }

  /* public void addActivity(Map map) {

  } */

  public Layout()
  {

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
    DocumentBuilder db = null;
    try
    {
      db = dbf.newDocumentBuilder();
      Document doc;
      doc = db.parse(in);

      NodeList mActivitycell = doc.getElementsByTagName("activity");
      for(int k = 0; k < mActivitycell.getLength(); k++)
      {
        Element cellAttr = (Element)mActivitycell.item(k);
        Activity activityCell = new Activity(cellAttr);
        Rectangle bound = activityCell.getBounds();
        activities.put(activityCell.getId(), bound);
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

    XMLUtil.printIndent(out, indent++);
    Set set = map.keySet();
    Iterator it = set.iterator();
    while(it.hasNext())
    {
      String key = (String)it.next();
      WorkflowCell cell = (WorkflowCell)map.get(key);
      Activity activityCell = new Activity(cell);
      activityCell.writeXML(out, indent);
    }
    XMLUtil.printIndent(out, --indent);
    out.println("</layout>");
    out.flush();
    out.close();
  }

  public Rectangle getBounds(String key)
  {
    return (Rectangle)activities.get(key);
  }
}
