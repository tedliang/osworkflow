package com.opensymphony.workflow.loader;

import java.io.PrintWriter;

import org.w3c.dom.Element;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Dec 5 2003
 * Time: 11:01:35 PM
 */
public class StatusDescriptor extends AbstractDescriptor
{
  private String name;

  public StatusDescriptor(Element status)
  {
    name = status.getAttribute("name");
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
  }
}
