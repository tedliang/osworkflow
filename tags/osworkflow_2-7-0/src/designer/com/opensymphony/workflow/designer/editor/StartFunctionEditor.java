package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-24
 */
public class StartFunctionEditor extends FunctionEditor
{
  public StartFunctionEditor(InitialActionCell cell)
  {
    super(cell);
  }

  protected InitialActionCell getCell()
  {
    return (InitialActionCell)cell;
  }

  protected AbstractDescriptor getParent()
  {
    return getCell().getActionDescriptor();
  }

	protected ConfigFunctionDescriptor getFunction()
	{
		ConfigFunctionDescriptor template = (ConfigFunctionDescriptor)DialogUtils.getUserSelection(getModel().getPalette().getPreFunctions(),
		  ResourceManager.getString("function.select"),
		  ResourceManager.getString("function.select"), null);
		if(template!=null)
		  return new ConfigFunctionDescriptor(template);
		return null;
	}	
}
