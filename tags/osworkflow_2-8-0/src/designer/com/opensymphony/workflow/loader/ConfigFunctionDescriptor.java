package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

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
  protected Map argTypeMap = new HashMap();
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
    argTypeMap.putAll(other.getArgTypes());
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

    List args = XMLUtil.getChildElements(function, "arg");
    for(int l = 0; l < args.size(); l++)
    {
      Element arg = (Element)args.get(l);
    		
			String ss = arg.getAttribute("name");
      
      this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
      if("true".equals(arg.getAttribute("modifiable")))
      {
        modifiableArgs.add(arg.getAttribute("name"));
      	String sArgType = arg.getAttribute("argtype");
      	if (sArgType!=null)
      	{
      		ArgType at = (ArgType)palette.getArgType(sArgType);
      		if (at!=null)
      		{
      			argTypeMap.put(arg.getAttribute("name"), at);
      		}
      	}
      }
    }

    plugin = XMLUtil.getChildText(function, "plugin");
    name = XMLUtil.getChildText(function, "name");
  }

  public boolean isArgModifiable(String name)
  {
    return modifiableArgs.contains(name);
  }

	public ArgType getArgType(String name)
	{
		return (ArgType)argTypeMap.get(name);
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
	
	public Map getArgTypes()
	{
		return argTypeMap;
	}
}
