package com.opensymphony.workflow.designer;

import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.ButtonBarBuilder;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 9:46:11 PM
 */
public class UIFactory
{
  public static JPanel getTableButtonBar(ActionListener listener, String prefix)
  {
    if(prefix==null) prefix = "";
    JButton[] buttons = new JButton[3];
    JButton add = new JButton("Add");
    add.setActionCommand(prefix + "add");
    buttons[0] = add;
    add.addActionListener(listener);
    JButton remove = new JButton("Remove");
    remove.setActionCommand(prefix + "remove");
    remove.addActionListener(listener);
    buttons[1] = remove;
    JButton props = new JButton("Properties...");
    props.setActionCommand(prefix + "properties");
    props.addActionListener(listener);
    buttons[2] = props;
    JPanel panel = new JPanel();
    ButtonBarBuilder builder = new ButtonBarBuilder(panel);
    builder.addGriddedGrowingButtons(buttons);
    return panel;
  }
}
