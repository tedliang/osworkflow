package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.opensymphony.workflow.designer.swing.EnhancedResourceBundle;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class PaletteDescriptor extends AbstractDescriptor
{
  protected List statusList = new ArrayList();
  protected List joinList = new ArrayList();
  protected List preList = new ArrayList();
  protected List permissionList = new ArrayList();
  protected List resultList = new ArrayList();
  protected String defaultOldStatus = null;
  protected String defaultNextStatus = null;
	private EnhancedResourceBundle bundle;

	public PaletteDescriptor(Element root, EnhancedResourceBundle bundle)
  {
	  this.bundle = bundle;
    init(root);
  }

	public ConfigConditionDescriptor[] getJoinConditions()
	{
		ConfigConditionDescriptor[] config = new ConfigConditionDescriptor[joinList.size()];
		joinList.toArray(config);
		return config;
	}

  public ConfigFunctionDescriptor[] getPreFunctions()
  {
	  ConfigFunctionDescriptor[] array = new ConfigFunctionDescriptor[preList.size()];
	  preList.toArray(array);
	  return array;
  }

  public PermissionConditionDescriptor[] getPermissionConditions()
  {
	  PermissionConditionDescriptor[] array = new PermissionConditionDescriptor[permissionList.size()];
	  permissionList.toArray(array);
	  return array;
  }

	public ConfigConditionDescriptor[] getResultConditions()
	{
		ConfigConditionDescriptor[] config = new ConfigConditionDescriptor[resultList.size()];
		resultList.toArray(config);
		return config;
	}

  public String[] getStatusNames()
  {
    String[] names = new String[statusList.size()];

    for(int i = 0; i < names.length; i++)
    {
      StatusDescriptor status = (StatusDescriptor)statusList.get(i);
      names[i] = status.getName();
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

  public String getDefaultOldStatus()
  {
    return defaultOldStatus;
  }

  public String getDefaultNextStatus()
  {
    return defaultNextStatus;
  }

  public void writeXML(PrintWriter writer, int indent)
  {
    throw new UnsupportedOperationException();
  }

  protected void init(Element root)
  {
    // joinconditions
    Element s = XMLUtil.getChildElement(root, "statusvalues");
    defaultNextStatus = s.getAttribute("default-next");
    defaultOldStatus = s.getAttribute("default-old");
    NodeList l = s.getElementsByTagName("status");
    for(int i = 0; i < l.getLength(); i++)
    {
      Element status = (Element)l.item(i);
      StatusDescriptor statusDescriptor = new StatusDescriptor(status);
      statusDescriptor.setParent(this);
      statusList.add(statusDescriptor);
    }
    Element j = XMLUtil.getChildElement(root, "joinconditions");
    if(j != null)
    {
      NodeList joins = j.getElementsByTagName("condition");
      for(int i = 0; i < joins.getLength(); i++)
      {
        Element condition = (Element)joins.item(i);
        ConfigConditionDescriptor jcd = new ConfigConditionDescriptor(this, condition);
	      jcd.setDescription(bundle.getString(jcd.getName() + ".long"));
	      jcd.setDisplayName(bundle.getString(jcd.getName()));
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
        ConfigFunctionDescriptor pd = new ConfigFunctionDescriptor(this, function);
	      pd.setDescription(bundle.getString(pd.getName() + ".long"));
	      pd.setDisplayName(bundle.getString(pd.getName()));
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
        PermissionConditionDescriptor pcd = new PermissionConditionDescriptor(this, condition);
	      pcd.setDescription(bundle.getString(pcd.getName() + ".long"));
	      pcd.setDisplayName(bundle.getString(pcd.getName()));
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
        ConfigConditionDescriptor rcd = new ConfigConditionDescriptor(this, condition);
	      rcd.setDescription(bundle.getString(rcd.getName() + ".long"));
	      rcd.setDisplayName(bundle.getString(rcd.getName()));
        rcd.setParent(this);
        resultList.add(rcd);
      }
    }

  }

	public EnhancedResourceBundle getBundle()
	{
		return bundle;
	}
}
