package com.opensymphony.workflow.loader;

import java.io.PrintWriter;

import org.w3c.dom.Element;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class PermissionConditionDescriptor extends ConfigConditionDescriptor
{
  protected String owner;

  public PermissionConditionDescriptor(PaletteDescriptor palette)
  {
	  super(palette);
  }

  public PermissionConditionDescriptor(PaletteDescriptor palette, Element condition)
  {
	  super(palette, condition);
  }

  public PermissionConditionDescriptor(PermissionConditionDescriptor permission)
  {
	  super(permission);
    owner = permission.getOwner();
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
  }

  protected void init(Element condition)
  {
    super.init(condition);
    owner = XMLUtil.getChildText(condition, "owner");
  }

  public String getOwner()
  {
    return owner;
  }

  public void setOwner(String string)
  {
    owner = string;
  }
}
