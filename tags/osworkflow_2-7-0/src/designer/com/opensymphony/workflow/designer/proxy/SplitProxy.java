/*
 * Created on 2003-11-23
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.proxy;

import com.opensymphony.workflow.loader.SplitDescriptor;

/**
 * @author baab
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SplitProxy {
	private SplitDescriptor split;
	
	public SplitProxy(SplitDescriptor split){
		this.split = split;
	}
	
	public String toString(){
		return "Split id " + split.getId();
	}

}
