package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResultEdge;
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

  protected ConfigFunctionDescriptor getNewFunction(String selection)
  {
    return new ConfigFunctionDescriptor(getModel().getPalette().getPrefunction(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(getModel().getPalette().getPreNames(), "Select Pre-Function", "Select Function", null);
  }

}
