/*
 * Created on 2003-11-21
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.workflow.loader;

import java.io.PrintWriter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jackflit
 * Date: 2003-11-21
 */
public class PermissionConditionDescriptor extends ConfigConditionDescriptor {

	protected String owner;

	public PermissionConditionDescriptor() {
	}

	public PermissionConditionDescriptor(Element condition) {
		init(condition);
	}
	
	public PermissionConditionDescriptor(PermissionConditionDescriptor permission){
		type = permission.getType();
		plugin = permission.getPlugin();
		name = permission.getName(); 
		description = permission.getDescription();
		owner = permission.getOwner();

		args.putAll(permission.getArgs());
		
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.util.XMLizable#writeXML(java.io.PrintWriter, int)
	 */
	public void writeXML(PrintWriter writer, int indent) {
		// TODO Auto-generated method stub

	}
	
	protected void init(Element condition) {
		type = condition.getAttribute("type");



		NodeList args = condition.getElementsByTagName("arg");

		for (int l = 0; l < args.getLength(); l++) {
			Element arg = (Element) args.item(l);
			this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
		}
		
		plugin = XMLUtil.getChildText(condition, "plugin");
		
		name = XMLUtil.getChildText(condition, "name");
		
		description = XMLUtil.getChildText(condition, "description");
		
		owner = XMLUtil.getChildText(condition, "owner");

	}


	/**
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param string
	 */
	public void setOwner(String string) {
		owner = string;
	}

}
