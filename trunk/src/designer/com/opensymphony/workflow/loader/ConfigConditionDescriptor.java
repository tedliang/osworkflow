package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author baab
 */
public class ConfigConditionDescriptor extends ConditionDescriptor implements ArgsAware
{
  protected String plugin;
  protected String description;
	protected String displayName;
  protected List modifiableArgs = new ArrayList();
  protected Map argTypeMap = new HashMap();
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
    negate = ("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n));

    // add negate as modifiable argument
    this.args.put("negate", String.valueOf(negate));
    this.modifiableArgs.add("negate");

    List args = XMLUtil.getChildElements(condition, "arg");
    for(int l = 0; l < args.size(); l++)
    {
      Element arg = (Element)args.get(l);
      this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
      if("true".equals(arg.getAttribute("modifiable")))
      {
        modifiableArgs.add(arg.getAttribute("name"));
        String sArgType = arg.getAttribute("argtype");
        if(sArgType != null)
        {
          ArgType at = palette.getArgType(sArgType);
          if(at != null)
          {
            argTypeMap.put(arg.getAttribute("name"), at);
          }
        }
      }
    }
    plugin = XMLUtil.getChildText(condition, "plugin");
    name = XMLUtil.getChildText(condition, "name");
  }

  public boolean isArgModifiable(String name)
  {
    return modifiableArgs.contains(name);
  }

	public ArgType getArgType(String name)
	{
		return (ArgType)argTypeMap.get(name);
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
