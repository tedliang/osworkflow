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
import com.opensymphony.workflow.loader.ArgsAware;

/**
 * @author Gulei
 */
public class DialogUtils
{
  public static Object getUserSelection(Object[] values, String message, String title, Component parent)
  {
    return JOptionPane.showInputDialog(parent, // parent component
      message, // dialog message
      title, // dialog title
      JOptionPane.QUESTION_MESSAGE, // question message type
      ResourceManager.getIcon("saveas"), // icon
      values, // selections
      null);							// initial select
  }

  public static Map getMapDialog(ArgsAware descriptor, String type, String owner)
  {
    MapPanel panel = new MapPanel(descriptor, type, owner);
    JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = pane.createDialog(null, ResourceManager.getString("specify.properties"));
	  dialog.setResizable(true);
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
    Map args = descriptor.getArgs();
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
