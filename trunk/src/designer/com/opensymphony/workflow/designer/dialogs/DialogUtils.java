package com.opensymphony.workflow.designer.dialogs;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import com.opensymphony.workflow.designer.swing.MapPanel;
import com.opensymphony.workflow.designer.swing.JavaTextPane;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.loader.ArgsAware;

/**
 * @author Gulei
 */
public class DialogUtils
{
	private static JTextPane textArea = new JavaTextPane();

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

	public static String getTextDialog(String initialValue, Component parent)
	{
		JPanel panel = new JPanel(new GridLayout(1, 1));
		textArea.setText(initialValue!=null ? initialValue.trim() : "");
		panel.add(new JScrollPane(textArea));

		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = pane.createDialog(parent, ResourceManager.getString("specify.properties"));
		dialog.setSize(450, 190);
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
		return textArea.getText().trim();
	}

  public static Map getMapDialog(ArgsAware descriptor, String type, String owner, Component parent)
  {
  	BaseDialog dialog = new BaseDialog((Frame)parent, ResourceManager.getString("specify.properties"), true);
    dialog.getBanner().setSubtitle(ResourceManager.getString("specify.properties.long"));
    MapPanel panel = new MapPanel(descriptor, type, owner);
    dialog.getContentPane().add(panel);
    dialog.setResizable(true);
    
    boolean result = dialog.ask(parent);
    if(!result) return null;

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
