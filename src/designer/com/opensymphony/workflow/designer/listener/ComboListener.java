package com.opensymphony.workflow.designer.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * @author baab
 */
public abstract class ComboListener implements ActionListener
{

  public void actionPerformed(ActionEvent evt)
  {
    JComboBox cb = (JComboBox)evt.getSource();

    // Get the new item
    Object newItem = cb.getSelectedItem();
    valueChanged((String)newItem);
  }

  abstract protected void valueChanged(String newValue);

}
