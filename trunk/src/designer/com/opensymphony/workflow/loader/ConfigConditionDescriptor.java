package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author baab
 */
public class ConfigConditionDescriptor extends ConditionDescriptor implements ArgsAware
{
  protected String plugin;
  protected String description;
	protected String displayName;
  protected List modifiableArgs = new ArrayList();
	private PaletteDescriptor palette;

	public ConfigConditionDescriptor(PaletteDescriptor palette)
  {
		this.palette = palette;
  }

  public ConfigConditionDescriptor(PaletteDescriptor palette, Element condition)
  {
	  this.palette = palette;
    init(condition);
  }

  public ConfigConditionDescriptor(ConfigConditionDescriptor other)
  {
    this.setPlugin(other.getPlugin());
    this.setName(other.getName());
    this.setNegate(other.isNegate());
    this.setType(other.getType());
    this.getArgs().putAll(other.getArgs());
	  displayName = other.displayName;
	  description = other.description;
    modifiableArgs = other.modifiableArgs;
	  palette = other.palette;
  }

	public PaletteDescriptor getPalette()
	{
		return palette;
	}

	public void setPalette(PaletteDescriptor palette)
	{
		this.palette = palette;
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
      if("true".equals(arg.getAttribute("modifiable")))
      {
        modifiableArgs.add(arg.getAttribute("name"));
      }
    }
    plugin = XMLUtil.getChildText(condition, "plugin");
    name = XMLUtil.getChildText(condition, "name");
  }

  public boolean isArgModifiable(String name)
  {
    return modifiableArgs.contains(name);
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
