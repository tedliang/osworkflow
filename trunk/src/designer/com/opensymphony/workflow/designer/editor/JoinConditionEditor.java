package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.dialogs.DialogUtils;
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
    return new ConfigConditionDescriptor(getModel().getPalette().getJoinCondition(selection));
  }

  protected String getSelection()
  {
    return DialogUtils.getUserSelection(getModel().getPalette().getJoinNames(), ResourceManager.getString("condition.select.join"), ResourceManager.getString("condition.select"), null);
  }

  protected JoinCell getCell()
  {
    return (JoinCell)cell;
  }

}
