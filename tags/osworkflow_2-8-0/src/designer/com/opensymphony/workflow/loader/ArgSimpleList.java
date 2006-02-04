/*
 * Created on 21-dic-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.workflow.loader;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * @author acapitani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArgSimpleList implements ArgType 
{
	protected String sName = "";
	protected String sType = "";
	protected List argList = new ArrayList();
	
	public boolean init(Element at)
	{
		sName = at.getAttribute("name");
		if ((sName==null)||(sName.length()==0))
			return false;
		sType = at.getAttribute("type");
		if (sType==null)
			sType = "";
		
		List args = XMLUtil.getChildElements(at, "arg");
		for(int l = 0; l < args.size(); l++)
		{
			Element arg = (Element)args.get(l);
			String sValue = arg.getAttribute("value");
			if (sValue!=null)
				argList.add(sValue);
		}
		return true;
	}
	
  public String getName() 
  {
    return sName;
  }

  public String getType() 
  {
    return sType;
  }

  public List getArgList() 
  {
    return argList;
  }
}
