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

  public PermissionConditionDescriptor()
  {
  }

  public PermissionConditionDescriptor(Element condition)
  {
    init(condition);
  }

  public PermissionConditionDescriptor(PermissionConditionDescriptor permission)
  {
    type = permission.getType();
    plugin = permission.getPlugin();
    name = permission.getName();
    description = permission.getDescription();
    owner = permission.getOwner();
    args.putAll(permission.getArgs());
    modifiableArgs = permission.modifiableArgs;
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
