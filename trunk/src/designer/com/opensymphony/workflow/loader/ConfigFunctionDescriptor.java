package com.opensymphony.workflow.loader;

import java.io.PrintWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class ConfigFunctionDescriptor extends FunctionDescriptor
{
  protected String plugin;
  protected String description;

  public ConfigFunctionDescriptor()
  {
  }

  public ConfigFunctionDescriptor(Element function)
  {
    init(function);
  }

  public ConfigFunctionDescriptor(ConfigFunctionDescriptor other)
  {
    type = other.getType();
    plugin = other.getPlugin();
    name = other.getName();
    description = other.getDescription();

    args.putAll(other.getArgs());

  }

  public void writeXML(PrintWriter writer, int indent)
  {
    // TODO Auto-generated method stub
  }

  protected void init(Element function)
  {
    type = function.getAttribute("type");

    String n = function.getAttribute("negate");
    boolean negate;
    if("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n))
    {
      negate = true;
    }
    else
    {
      negate = false;
    }

    NodeList args = function.getElementsByTagName("arg");

    for(int l = 0; l < args.getLength(); l++)
    {
      Element arg = (Element)args.item(l);
      this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
    }

    plugin = XMLUtil.getChildText(function, "plugin");

    name = XMLUtil.getChildText(function, "name");

    description = XMLUtil.getChildText(function, "description");

  }

  public String getDescription()
  {
    return description;
  }

  public String getPlugin()
  {
    return plugin;
  }

  public void setDescription(String string)
  {
    description = string;
  }

  public void setPlugin(String string)
  {
    plugin = string;
  }

}
