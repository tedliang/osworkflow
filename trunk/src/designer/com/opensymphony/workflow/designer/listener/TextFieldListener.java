package com.opensymphony.workflow.designer.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * @author baab
 */
public abstract class TextFieldListener implements DocumentListener, ActionListener
{
  public void changedUpdate(DocumentEvent arg0)
  {
    try
    {
      valueChanged(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
    }
    catch(BadLocationException e)
    {
      e.printStackTrace();
    }
  }

  public void insertUpdate(DocumentEvent arg0)
  {
    try
    {
      valueChanged(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
    }
    catch(BadLocationException e)
    {
      e.printStackTrace();
    }
  }

  public void removeUpdate(DocumentEvent arg0)
  {
    try
    {
      valueChanged(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
    }
    catch(BadLocationException e)
    {
      e.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    valueChanged(((JTextComponent)e.getSource()).getText());
  }

  abstract protected void valueChanged(String msg);

}
