package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class WorkflowConfigDescriptor extends AbstractDescriptor
{

  protected List joinList = new ArrayList();
  protected List preList = new ArrayList();
  protected List permissionList = new ArrayList();
  protected List resultList = new ArrayList();

  public String[] getJoinNames()
  {
    String[] names = new String[joinList.size()];

    for(int i = 0; i < names.length; i++)
    {
      ConfigConditionDescriptor condition = (ConfigConditionDescriptor)joinList.get(i);
      names[i] = condition.getName();
    }

    return names;
  }

  public String[] getPreNames()
  {
    String[] names = new String[preList.size()];

    for(int i = 0; i < names.length; i++)
    {
      ConfigFunctionDescriptor pre = (ConfigFunctionDescriptor)preList.get(i);
      names[i] = pre.getName();
    }

    return names;
  }

  public String[] getPermissionNames()
  {
    String[] names = new String[permissionList.size()];

    for(int i = 0; i < names.length; i++)
    {
      PermissionConditionDescriptor perm = (PermissionConditionDescriptor)permissionList.get(i);
      names[i] = perm.getName();
    }

    return names;
  }

  public String[] getResultNames()
  {
    String[] names = new String[resultList.size()];

    for(int i = 0; i < names.length; i++)
    {
      ConfigConditionDescriptor result = (ConfigConditionDescriptor)resultList.get(i);
      names[i] = result.getName();
    }

    return names;
  }

  public ConfigConditionDescriptor getJoinCondition(String name)
  {
    if(name == null)
    {
      return null;
    }
    for(int i = 0; i < joinList.size(); i++)
    {
      ConfigConditionDescriptor join = (ConfigConditionDescriptor)joinList.get(i);
      if(name.equals(join.getName()))
      {
        return join;
      }
    }
    return null;
  }

  public ConfigFunctionDescriptor getPrefunction(String name)
  {
    if(name == null)
    {
      return null;
    }
    for(int i = 0; i < preList.size(); i++)
    {
      ConfigFunctionDescriptor pre = (ConfigFunctionDescriptor)preList.get(i);
      if(name.equals(pre.getName()))
      {
        return pre;
      }
    }
    return null;
  }

  public PermissionConditionDescriptor getPermissionCondition(String name)
  {
    if(name == null)
    {
      return null;
    }
    for(int i = 0; i < permissionList.size(); i++)
    {
      PermissionConditionDescriptor perm = (PermissionConditionDescriptor)permissionList.get(i);
      if(name.equals(perm.getName()))
      {
        return perm;
      }
    }
    return null;
  }

  public ConfigConditionDescriptor getResultCondition(String name)
  {
    if(name == null)
    {
      return null;
    }
    for(int i = 0; i < joinList.size(); i++)
    {
      ConfigConditionDescriptor result = (ConfigConditionDescriptor)resultList.get(i);
      if(name.equals(result.getName()))
      {
        return result;
      }
    }
    return null;
  }

  public WorkflowConfigDescriptor()
  {

  }

  public WorkflowConfigDescriptor(Element root)
  {
    init(root);
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
  }

  protected void init(Element root)
  {
    // joinconditions
    Element j = XMLUtil.getChildElement(root, "joinconditions");
    if(j != null)
    {
      NodeList joins = j.getElementsByTagName("condition");
      for(int i = 0; i < joins.getLength(); i++)
      {
        Element condition = (Element)joins.item(i);
        ConfigConditionDescriptor jcd = new ConfigConditionDescriptor(condition);
        jcd.setParent(this);
        joinList.add(jcd);
      }
    }

    // prefunctions
    Element p = XMLUtil.getChildElement(root, "functions");
    if(p != null)
    {
      NodeList joins = p.getElementsByTagName("function");
      for(int i = 0; i < joins.getLength(); i++)
      {
        Element function = (Element)joins.item(i);
        ConfigFunctionDescriptor pd = new ConfigFunctionDescriptor(function);
        pd.setParent(this);
        preList.add(pd);
      }
    }

    // permissionconditions
    Element pm = XMLUtil.getChildElement(root, "permissionconditions");
    if(pm != null)
    {
      NodeList joins = pm.getElementsByTagName("condition");
      for(int i = 0; i < joins.getLength(); i++)
      {
        Element condition = (Element)joins.item(i);
        PermissionConditionDescriptor pcd = new PermissionConditionDescriptor(condition);
        pcd.setParent(this);
        permissionList.add(pcd);
      }
    }

    // resultconditions
    Element r = XMLUtil.getChildElement(root, "resultconditions");
    if(j != null)
    {
      NodeList joins = r.getElementsByTagName("condition");
      for(int i = 0; i < joins.getLength(); i++)
      {
        Element condition = (Element)joins.item(i);
        ConfigConditionDescriptor rcd = new ConfigConditionDescriptor(condition);
        rcd.setParent(this);
        resultList.add(rcd);
      }
    }

  }

}
