package com.opensymphony.workflow.loader;

import java.io.PrintWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author baab
 */
public class ConfigConditionDescriptor extends ConditionDescriptor
{
  protected String plugin;
  protected String description;

  public ConfigConditionDescriptor()
  {

  }

  public ConfigConditionDescriptor(Element condition)
  {
    init(condition);
  }

  public ConfigConditionDescriptor(ConfigConditionDescriptor config)
  {
    this.setDescription(config.getDescription());
    this.setPlugin(config.getPlugin());

    this.setName(config.getName());
    this.setNegate(config.isNegate());
    this.setType(config.getType());

    this.getArgs().putAll(config.getArgs());
  }

  protected void init(Element condition)
  {
    type = condition.getAttribute("type");

    String n = condition.getAttribute("negate");
    boolean negate;
    if("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n))
    {
      negate = true;
    }
    else
    {
      negate = false;
    }

    NodeList args = condition.getElementsByTagName("arg");

    for(int l = 0; l < args.getLength(); l++)
    {
      Element arg = (Element)args.item(l);
      this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
    }

    plugin = XMLUtil.getChildText(condition, "plugin");

    name = XMLUtil.getChildText(condition, "name");

    description = XMLUtil.getChildText(condition, "description");

  }

  public void writeXML(PrintWriter writer, int indent)
  {
    // TODO Auto-generated method stub
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
