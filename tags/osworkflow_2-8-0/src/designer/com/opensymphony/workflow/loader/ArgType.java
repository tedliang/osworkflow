/*
 * Created on 21-dic-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.workflow.loader;

import java.util.List;

import org.w3c.dom.Element;

/**
 * @author acapitani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ArgType 
{
	public boolean init(Element at);
	public String getName();
	public String getType();
	public List getArgList();
}
