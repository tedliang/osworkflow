package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 */
public class ConfigValidatorDescriptor extends ValidatorDescriptor implements ArgsAware
{
  protected String name;
  protected String plugin;
  protected String description;
	protected List modifiableArgs = new ArrayList();
	protected Map argTypeMap = new HashMap();
	private PaletteDescriptor palette;

	public ConfigValidatorDescriptor(PaletteDescriptor palette)
  {
		this.palette = palette;
  }

  public ConfigValidatorDescriptor(PaletteDescriptor palette, Element validator)
  {
	  this.palette = palette;
    init(validator);
  }

  public ConfigValidatorDescriptor(ConfigValidatorDescriptor other)
  {
    this.setPlugin(other.getPlugin());
    this.setName(other.getName());
    this.setType(other.getType());
    this.getArgs().putAll(other.getArgs());
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

  protected void init(Element validator)
  {
    type = validator.getAttribute("type");
    
    List args = XMLUtil.getChildElements(validator, "arg");
    for(int l = 0; l < args.size(); l++)
    {
      Element arg = (Element)args.get(l);
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
    plugin = XMLUtil.getChildText(validator, "plugin");
    name = XMLUtil.getChildText(validator, "name");
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

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
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
		return getName();
	}
}
