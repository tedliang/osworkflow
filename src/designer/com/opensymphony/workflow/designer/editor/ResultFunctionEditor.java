package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author baab
 */
public class ResultFunctionEditor extends FunctionEditor
{

  protected ResultEdge edge;

  public ResultFunctionEditor(ResultEdge cell)
  {
    super(null);
    edge = cell;
  }

  protected AbstractDescriptor getParent()
  {
    return edge.getDescriptor();
  }

	protected ConfigFunctionDescriptor getFunction()
	{
		ConfigFunctionDescriptor template = (ConfigFunctionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPreFunctions(),
		  ResourceManager.getString("function.select.pre"),
		  ResourceManager.getString("function.select"), null);
		if(template!=null)
		  return new ConfigFunctionDescriptor(template);
		return null;
	}
}
