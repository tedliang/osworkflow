package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class ConfigFunctionDescriptor extends FunctionDescriptor implements ArgsAware
{
  protected String plugin;
  protected String description;
	protected String displayName;
  protected List modifiableArgs = new ArrayList();
	private PaletteDescriptor palette;

  public ConfigFunctionDescriptor(PaletteDescriptor palette)
  {
	  this.palette = palette;
  }

  public ConfigFunctionDescriptor(PaletteDescriptor palette, Element function)
  {
	  this.palette = palette;
    init(function);
  }

  public ConfigFunctionDescriptor(ConfigFunctionDescriptor other)
  {
    type = other.getType();
    plugin = other.getPlugin();
    name = other.getName();
    args.putAll(other.getArgs());
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

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
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
      if("true".equals(arg.getAttribute("modifiable")))
      {
        modifiableArgs.add(arg.getAttribute("name"));
      }
    }

    plugin = XMLUtil.getChildText(function, "plugin");
    name = XMLUtil.getChildText(function, "name");
  }

  public boolean isArgModifiable(String name)
  {
    return modifiableArgs.contains(name);
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

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String toString()
	{
		return displayName!=null ? displayName : name;
	}
}
