package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author baab
 */
public class StepFunctionEditor extends FunctionEditor
{
  public StepFunctionEditor(StepCell cell)
  {
    super(cell);
  }

  private StepCell getCell()
  {
    return (StepCell)cell;
  }

  protected AbstractDescriptor getParent()
  {
    //todo need a check here for actions>0 or a more sensible way to get parent
    return (ActionDescriptor)getCell().getDescriptor().getActions().get(0);
  }

  protected ConfigFunctionDescriptor getNewFunction(String selection)
  {
    return new ConfigFunctionDescriptor(WorkflowDesigner.config.getPrefunction(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(WorkflowDesigner.config.getPreNames(), "Select step function", "Select Function", null);
  }

}
