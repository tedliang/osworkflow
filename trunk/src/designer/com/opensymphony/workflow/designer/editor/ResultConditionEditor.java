package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author baab
 */
public class ResultConditionEditor extends ConditionEditor
{
  private ResultEdge edge;

  public ResultConditionEditor(ResultEdge cell)
  {
    super(null);
    edge = cell;
  }

  protected AbstractDescriptor getParent()
  {
    return edge.getDescriptor();
  }

  protected ConfigConditionDescriptor getNewCondition(String selection)
  {
    return new ConfigConditionDescriptor(WorkflowDesigner.palette.getResultCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(WorkflowDesigner.palette.getResultNames(), "Select result condition", "Select Condition", null);
  }

}
