package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.PermissionConditionDescriptor;

/**
 * @author Gulei
 */
public class DefaultPermissionPlugin implements PermissionCondition
{

  PermissionConditionDescriptor perm;

  public void setPermissionCondition(PermissionConditionDescriptor descriptor)
  {
    perm = descriptor;
  }

  public PermissionConditionDescriptor getPermissionCondition()
  {
    return perm;
  }

  public boolean editPermissionCondition(Map args, Component parent)
  {
    Map newArg = DialogUtils.getMapDialog(perm, perm.getType(), perm.getOwner(), parent);
    if(newArg == null)
    {
      return false;
    }

    Map oldArg = perm.getArgs();
    Set keys = oldArg.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      Object key = iter.next();
      oldArg.put(key, newArg.get(key));
    }
    return true;
  }

}
