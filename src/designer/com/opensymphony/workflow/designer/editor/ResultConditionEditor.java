package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
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
    return new ConfigConditionDescriptor(getModel().getPalette().getResultCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(getModel().getPalette().getResultNames(), ResourceManager.getString("condition.select.result"), ResourceManager.getString("condition.select"), null);
  }

}
