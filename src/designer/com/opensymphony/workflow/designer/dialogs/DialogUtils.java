package com.opensymphony.workflow.designer.dialogs;

import java.awt.Component;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.opensymphony.workflow.designer.swing.MapPanel;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Gulei
 */
public class DialogUtils
{
  public static String getUserSelection(String[] values, String message, String title, Component parent)
  {
    return (String)JOptionPane.showInputDialog(parent, // parent component
      message, // dialog message
      title, // dialog title
      JOptionPane.QUESTION_MESSAGE, // question message type
      ResourceManager.getIcon("saveas"), // icon
      values, // selections
      null);							// initial select
  }

  public static Map getMapDialog(Map args, String type, String name, String description)
  {
    MapPanel panel = new MapPanel(args, type, name, description);
    JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = pane.createDialog(null, "title");
    dialog.show();

    Integer value = (Integer)pane.getValue();
    if(value == null)
    {
      return null;
    }
    if(value.intValue() != JOptionPane.OK_OPTION)
    {
      return null;
    }

    Map edits = panel.getEdits();
    Set keys = args.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      Object key = iter.next();
      JTextField field = (JTextField)edits.get(key);
      String newValue = field.getText();
      args.put(key, newValue);
    }

    return args;
  }

  public static Map getMapDialogWithOwner(Map args, String type, String name, String description, String owner)
  {
    MapPanel panel = new MapPanel(args, type, name, description, owner);
    JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = pane.createDialog(null, "title");
    dialog.show();

    Integer value = (Integer)pane.getValue();

    // dialog canceled
    if(value == null)
    {
      return null;
    }

    if(value.intValue() != JOptionPane.OK_OPTION)
    {
      return null;
    }

    Map edits = panel.getEdits();
    Set keys = args.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      Object key = iter.next();
      JTextField field = (JTextField)edits.get(key);
      String newValue = field.getText();
      args.put(key, newValue);
    }

    return args;
  }

}
