package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 21 2003
 *         Time: 9:46:11 PM
 */
public class UIFactory
{
  public static JPanel getButtonBar(ActionListener listener, String prefix, String[] names)
  {
    if(prefix == null) prefix = "";
    JButton[] buttons = new JButton[names.length];
    for(int i = 0; i < buttons.length; i++)
    {
      buttons[i] = new JButton(ResourceManager.getString(names[i]));
      buttons[i].setActionCommand(prefix + names[i]);
      buttons[i].addActionListener(listener);
    }
	  return ButtonBarFactory.buildCenteredBar(buttons);
  }

  public static JPanel getButtonBar(ActionListener[] listener, String prefix, String[] names)
  {
    if(prefix == null) prefix = "";
    if(listener.length!=names.length) throw new IllegalArgumentException("listener.length=" + listener.length + " but names.length=" + names.length);
    JButton[] buttons = new JButton[names.length];
    for(int i = 0; i < buttons.length; i++)
    {
      buttons[i] = new JButton(ResourceManager.getString(names[i]));
      buttons[i].setActionCommand(prefix + names[i]);
      buttons[i].addActionListener(listener[i]);
    }
    return ButtonBarFactory.buildCenteredBar(buttons);
  }

  public static JComponent createTablePanel(JTable table)
  {
    Color background = UIManager.getColor("window");
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.getViewport().setBackground(background);
    scrollPane.setOpaque(false);
    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
    scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
    JPanel panel = new JPanel(new GridLayout(0, 1))
    {
      public void updateUI()
      {
        super.updateUI();
        setBackground(UIManager.getColor("window"));
      }
    };
    panel.setPreferredSize(new Dimension(220, 100));
    panel.add(scrollPane);
    return panel;
  }

  public static JTextField createReadOnlyTextField(int width)
  {
    JTextField field = new JTextField(width)
    {
      public boolean isFocusable()
      {
        return false;
      }

      protected void processEvent(AWTEvent e)
      {
      }
    };
    field.setEditable(false);
    field.setOpaque(false);
    field.setForeground(Color.black);
    return field;
  }
}
