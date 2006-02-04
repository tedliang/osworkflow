package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 * Date: Nov 22, 2004
 * Time: 12:54:16 AM
 */
public class TriggerFunctionEditor extends FunctionEditor
{
  public TriggerFunctionEditor(WorkflowDescriptor desc)
  {
    super(desc);
  }
	
  protected AbstractDescriptor getParent()
  {
    // return the WorkflowDescriptor
    return getDescriptor();
  }

	protected ConfigFunctionDescriptor getFunction()
	{
		ConfigFunctionDescriptor template = (ConfigFunctionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getTriggerFunctions(),
		  ResourceManager.getString("function.select.trigger"),
		  ResourceManager.getString("function.select"),
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new ConfigFunctionDescriptor(template);
		return null;
	}	
}
