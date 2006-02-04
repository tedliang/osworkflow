package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.loader.*;


/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class StartFunctionEditor extends FunctionEditor
{
  public StartFunctionEditor(ActionDescriptor desc)
  {
    super(desc);
  }

  protected AbstractDescriptor getParent()
  {
    return getDescriptor();
  }

	protected ConfigFunctionDescriptor getFunction()
	{
		ConfigFunctionDescriptor template = (ConfigFunctionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPreFunctions(),
		  ResourceManager.getString("function.select"),
		  ResourceManager.getString("function.select"), 
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new ConfigFunctionDescriptor(template);
		return null;
	}	
}
