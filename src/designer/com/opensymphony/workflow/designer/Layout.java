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

  Collection entries;
  Map activities = new HashMap();

  public Layout()
  {
    entries = new ArrayList();
  }

  public Layout(Collection entries)
  {
    this.entries = entries;
  }

  public void setActivity(Collection entries)
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
    Iterator it = entries.iterator();
    while(it.hasNext())
    {
      WorkflowCell cell = (WorkflowCell)it.next();
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
