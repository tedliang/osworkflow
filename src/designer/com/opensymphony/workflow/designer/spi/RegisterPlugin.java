package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;
import com.opensymphony.workflow.loader.ConfigRegisterDescriptor;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:35:07 AM 
 */
public interface RegisterPlugin 
{
	public void setRegister(ConfigRegisterDescriptor descriptor);
	public ConfigRegisterDescriptor getRegister();
	public boolean editRegister(Map args, Component parent);
}
