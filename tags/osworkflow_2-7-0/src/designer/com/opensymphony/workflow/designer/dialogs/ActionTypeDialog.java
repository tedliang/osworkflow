package com.opensymphony.workflow.designer.dialogs;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;

/**
 * Date: Mar 17, 2004
 * Time: 10:33:21 PM
 *
 * @author hani
 */
public class ActionTypeDialog extends BaseDialog implements ItemListener
{
  private JComboBox actionType = new JComboBox(new String[]{ResourceManager.getString("result.conditional"), ResourceManager.getString("result.unconditional")});
  private JComboBox relatedAction;
  private StepDescriptor source;
  private WorkflowGraphModel model;

  public ActionTypeDialog(Frame owner, StepDescriptor source) throws HeadlessException
  {
    super(owner, ResourceManager.getString("result.select"), true);
    this.source = source;
    getBanner().setTitle(ResourceManager.getString("result.create.title"));
    getBanner().setSubtitle(ResourceManager.getString("result.create.subtitle"));
    DefaultFormBuilder builder = UIFactory.getDialogBuilder(null, getContentPane());
    builder.append(ResourceManager.getString("result.select.long"), actionType);

    java.util.List relatedActions = new ArrayList(source.getActions());
    relatedActions.add(0, ResourceManager.getString("result.create.newaction"));
    relatedAction = new JComboBox(relatedActions.toArray());
    relatedAction.setEditable(true);
    relatedAction.setRenderer(new DefaultListCellRenderer()
    {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof ActionDescriptor)
        {
          ActionDescriptor action = (ActionDescriptor)value;
          label.setText(action.getName());
        }
        return label;
      }
    });
    relatedAction.addItemListener(this);
    actionType.addItemListener(this);

    builder.append(ResourceManager.getString("result.related.action"), relatedAction);
    builder.appendRow(builder.getLineGapSpec());
    builder.nextLine();
  }

  public int getType()
  {
    switch(actionType.getSelectedIndex())
    {
      case 0:
        return ConnectHelper.CONDITIONAL;
      default:
        return ConnectHelper.UNCONDITIONAL;
    }
  }

  public ActionDescriptor getRelatedAction()
  {
    int index = relatedAction.getSelectedIndex();
    if(index < 1)
    {
      ActionDescriptor sourceAction = DescriptorBuilder.createAction(source, index == 0 ? source.getName() : (String)relatedAction.getEditor().getItem(), Utils.getNextId(model.getContext()));
      Utils.checkId(model.getContext(), sourceAction);
      return sourceAction;
    }
    return (ActionDescriptor)relatedAction.getSelectedItem();
  }

  public WorkflowGraphModel getModel()
  {
    return model;
  }

  public void setModel(WorkflowGraphModel model)
  {
    this.model = model;
  }

  public void itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
    {
      if(e.getSource() == relatedAction || e.getSource() == actionType)
      {
        //don't allow unconditional results if we already have one for the current action
        if(actionType.getSelectedIndex() == 1 && relatedAction.getSelectedIndex() > 0)
        {
          ActionDescriptor action = (ActionDescriptor)relatedAction.getSelectedItem();
          if(action.getUnconditionalResult() != null)
          {
            JOptionPane.showMessageDialog(this, "Action already has an unconditional result.\nTo create a new one, delete the existing one first.", "Duplicate unconditional result", JOptionPane.ERROR_MESSAGE);
            if(e.getSource() == relatedAction)
              relatedAction.setSelectedIndex(0);
            else
              actionType.setSelectedIndex(1);
          }
        }
      }
    }
  }
}