package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author baab
 */
public class ResultFunctionEditor extends FunctionEditor
{

  //protected ResultEdge edge;
  protected ResultDescriptor descriptor;

  public ResultFunctionEditor(ResultDescriptor desc)
  {
    super(null);
    descriptor = desc;
  }

  protected AbstractDescriptor getParent()
  {
    return descriptor;
  }

	protected ConfigFunctionDescriptor getFunction()
	{
		ConfigFunctionDescriptor template = (ConfigFunctionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPreFunctions(),
		  ResourceManager.getString("function.select.pre"),
		  ResourceManager.getString("function.select"), 
		  WorkflowDesigner.INSTANCE);
		if(template!=null)
		  return new ConfigFunctionDescriptor(template);
		return null;
	}
}
