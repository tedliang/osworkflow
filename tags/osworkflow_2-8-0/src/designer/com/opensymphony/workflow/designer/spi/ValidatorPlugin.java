package com.opensymphony.workflow.designer.spi;

import java.awt.Component;
import java.util.Map;
import com.opensymphony.workflow.loader.ConfigValidatorDescriptor;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 *         Date: Nov 22, 2004
 *         Time: 10:35:07 AM 
 */
public interface ValidatorPlugin 
{
	public void setValidator(ConfigValidatorDescriptor descriptor);
	public ConfigValidatorDescriptor getValidator();
	public boolean editValidator(Map args, Component parent);
}
