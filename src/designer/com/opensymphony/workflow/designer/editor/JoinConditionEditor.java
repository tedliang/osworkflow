package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author baab
 */
public class JoinConditionEditor extends ConditionEditor
{

  public JoinConditionEditor(JoinCell cell)
  {
    super(cell);
  }

  protected AbstractDescriptor getParent()
  {
    return getCell().getJoinDescriptor();
  }

  protected ConfigConditionDescriptor getNewCondition(String selection)
  {
    return new ConfigConditionDescriptor(WorkflowDesigner.config.getJoinCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(WorkflowDesigner.config.getJoinNames(), "Select join condition", "Select Condition", null);
  }

  protected JoinCell getCell()
  {
    return (JoinCell)cell;
  }

}
