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
	protected String displayName;

  public ConfigConditionDescriptor()
  {

  }

  public ConfigConditionDescriptor(Element condition)
  {
    init(condition);
  }

  public ConfigConditionDescriptor(ConfigConditionDescriptor config)
  {
    this.setPlugin(config.getPlugin());
    this.setName(config.getName());
    this.setNegate(config.isNegate());
    this.setType(config.getType());
    this.getArgs().putAll(config.getArgs());
	  displayName = config.displayName;
	  description = config.description;
  }

  protected void init(Element condition)
  {
    type = condition.getAttribute("type");

    String n = condition.getAttribute("negate");
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
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
  }

  public String getDescription()
  {
    return description;
  }

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
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

	public String toString()
	{
		return displayName!=null ? displayName : name;
	}
}