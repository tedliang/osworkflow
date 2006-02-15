package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import com.opensymphony.workflow.designer.swing.EnhancedResourceBundle;

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
  protected List validatorList = new ArrayList();
  protected List registerList = new ArrayList();
  protected List triggerList = new ArrayList();
  protected List argtypeList = new ArrayList();
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

	public ConfigRegisterDescriptor[] getRegisters()
	{
		// workflow registers
		ConfigRegisterDescriptor[] config = new ConfigRegisterDescriptor[registerList.size()];
		registerList.toArray(config);
		return config;		 
	}
	
	public ConfigValidatorDescriptor[] getValidators()
	{
		ConfigValidatorDescriptor[] config = new ConfigValidatorDescriptor[validatorList.size()];
		validatorList.toArray(config);
		return config;
	}
	
	public ConfigFunctionDescriptor[] getTriggerFunctions()
	{
		ConfigFunctionDescriptor[] config = new ConfigFunctionDescriptor[triggerList.size()];
		triggerList.toArray(config);
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
    for(int i = 0; i < resultList.size(); i++)
    {
      ConfigConditionDescriptor result = (ConfigConditionDescriptor)resultList.get(i);
      if(name.equals(result.getName()))
      {
        return result;
      }
    }
    return null;
  }

	public ConfigFunctionDescriptor getTriggerFunction(String name)
	{
		if (name==null)
			return null;
		for (int i=0; i<triggerList.size(); i++)
		{
			ConfigFunctionDescriptor function = (ConfigFunctionDescriptor)triggerList.get(i);
			if (name.equals(function.getName()))
				return function;		
		}
		return null;
	}
	
	public ConfigRegisterDescriptor getRegister(String name)
	{
		if (name==null)
			return null;
		for (int i=0; i<registerList.size(); i++)
		{
			ConfigRegisterDescriptor reg = (ConfigRegisterDescriptor)registerList.get(i);
			if (name.equals(reg.getVariableName()))
				return reg;
		}
		return null;
	}
	
	public ArgType getArgType(String name)
	{
		if (name==null)
			return null;
		for (int i=0; i<argtypeList.size(); i++)
		{
			ArgType type = (ArgType)argtypeList.get(i); 
			if (name.equals(type.getName()))
				return type;		
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

  public void init(Element root)
  {
    // argtypes
    Element a = XMLUtil.getChildElement(root, "argtypes");
    if (a!=null)
   	{
   		List types = XMLUtil.getChildElements(a, "argtype");
   		for (int i=0; i<types.size(); i++)
   		{
   			Element argtype = (Element)types.get(i);
   			String sClass = argtype.getAttribute("class");   
   			if (sClass!=null)
   			{
					ArgType argImpl = null;
					try
					{
						argImpl = (ArgType)Class.forName(sClass).newInstance();
						if ((argImpl!=null)&&(argImpl.init(argtype)))
						{
							argtypeList.add(argImpl);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}	
   		}
   	}
    
    // joinconditions
    Element s = XMLUtil.getChildElement(root, "statusvalues");
    defaultNextStatus = s.getAttribute("default-next");
    defaultOldStatus = s.getAttribute("default-old");
    List l = XMLUtil.getChildElements(s, "status");
    for(int i = 0; i < l.size(); i++)
    {
      Element status = (Element)l.get(i);
      StatusDescriptor statusDescriptor = new StatusDescriptor(status);
      statusDescriptor.setParent(this);
      statusList.add(statusDescriptor);
    }
    Element j = XMLUtil.getChildElement(root, "joinconditions");
    if(j != null)
    {
      List joins = XMLUtil.getChildElements(j, "condition");
      for(int i = 0; i < joins.size(); i++)
      {
        Element condition = (Element)joins.get(i);
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
      List functions = XMLUtil.getChildElements(p, "function");
      for(int i = 0; i < functions.size(); i++)
      {
        Element function = (Element)functions.get(i);
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
      List joins = XMLUtil.getChildElements(pm, "condition");
      for(int i = 0; i < joins.size(); i++)
      {
        Element condition = (Element)joins.get(i);
        PermissionConditionDescriptor pcd = new PermissionConditionDescriptor(this, condition);
	      pcd.setDescription(bundle.getString(pcd.getName() + ".long"));
	      pcd.setDisplayName(bundle.getString(pcd.getName()));
        pcd.setParent(this);
        permissionList.add(pcd);
      }
    }

    // resultconditions
    Element r = XMLUtil.getChildElement(root, "resultconditions");
    if (r != null)
    {
      List conditions = XMLUtil.getChildElements(r, "condition");
      for(int i = 0; i < conditions.size(); i++)
      {
        Element condition = (Element)conditions.get(i);
        ConfigConditionDescriptor rcd = new ConfigConditionDescriptor(this, condition);
	      rcd.setDescription(bundle.getString(rcd.getName() + ".long"));
	      rcd.setDisplayName(bundle.getString(rcd.getName()));
        rcd.setParent(this);
        resultList.add(rcd);
      }
    }

		// registers
		Element rr = XMLUtil.getChildElement(root, "registers");
		if (rr != null)
		{
			List registers = XMLUtil.getChildElements(rr, "register");
			for(int i = 0; i < registers.size(); i++)
			{
				Element register = (Element)registers.get(i);
				ConfigRegisterDescriptor rd = new ConfigRegisterDescriptor(this, register);
				rd.setDescription(bundle.getString(rd.getName() + ".long"));
				rd.setVariableName(bundle.getString(rd.getName()));
				rd.setParent(this);
				registerList.add(rd);
			}
		}
		
		// trigger-functions
		Element tf = XMLUtil.getChildElement(root, "triggerfunctions");
		if(tf != null)
		{
			List functions = XMLUtil.getChildElements(tf, "function");
			for(int i = 0; i < functions.size(); i++)
			{
				Element function = (Element)functions.get(i);
				ConfigFunctionDescriptor pd = new ConfigFunctionDescriptor(this, function);
				pd.setDescription(bundle.getString(pd.getName() + ".long"));
				pd.setDisplayName(bundle.getString(pd.getName()));
				pd.setParent(this);
				triggerList.add(pd);
			}
		}
		
		// validators
		Element vl = XMLUtil.getChildElement(root, "validators");
		if (vl != null)
		{
			List validators = XMLUtil.getChildElements(vl, "validator");
			for(int i = 0; i < validators.size(); i++)
			{
				Element validator = (Element)validators.get(i);
				ConfigValidatorDescriptor vd = new ConfigValidatorDescriptor(this, validator);
				vd.setDescription(bundle.getString(vd.getName() + ".long"));
				vd.setName(bundle.getString(vd.getName()));
				vd.setParent(this);
				validatorList.add(vd);
			}
		}
		
		
  }

	public EnhancedResourceBundle getBundle()
	{
		return bundle;
	}
}
