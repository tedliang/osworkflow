package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.View;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 21 2003
 *         Time: 9:46:11 PM
 */
public class UIFactory
{
  private static final DefaultFormatterFactory INT_FORMATTER_FACTORY;

  static
  {
    NumberFormat format = NumberFormat.getIntegerInstance();
    format.setGroupingUsed(false);
    INT_FORMATTER_FACTORY = new DefaultFormatterFactory(new NumberFormatter(format));
  }

  public static JFormattedTextField createIntegerField()
  {
    JFormattedTextField field = new JFormattedTextField();
    field.setFormatterFactory(INT_FORMATTER_FACTORY);
    return field;
  }

  public static JPanel getAddRemovePropertiesBar(ActionListener listener, String prefix, String[] names)
  {
    if(prefix == null) prefix = "";
    JButton[] buttons = new JButton[names.length];
    for(int i = 0; i < buttons.length; i++)
    {
      buttons[i] = new JButton(ResourceManager.getString(names[i]));
      buttons[i].setActionCommand(prefix + names[i]);
      buttons[i].addActionListener(listener);
    }
	  return ButtonBarFactory.buildAddRemovePropertiesBar(buttons[0], buttons[1], buttons[2]);
  }

  public static DefaultFormBuilder getDialogBuilder(String separator, Container contentPane)
  {
    FormLayout layout = new FormLayout("2dlu, left:max(40dlu;pref), 3dlu, 110dlu:grow, 7dlu");
    DefaultFormBuilder builder = createBuilder((JPanel)contentPane, layout, separator);
    builder.setDefaultDialogBorder();
    return builder;
  }

  public static DefaultFormBuilder getTwoColumnBuilder(String separator, JPanel panel)
  {
    FormLayout layout = new FormLayout("2dlu, left:pref, 3dlu, pref:grow, 2dlu", "");
    DefaultFormBuilder builder = createBuilder(panel, layout, separator);
    return builder;
  }

  private static DefaultFormBuilder createBuilder(JPanel panel, FormLayout layout, String separator)
  {
    DefaultFormBuilder builder = new DefaultFormBuilder(panel, layout, ResourceManager.getBundle());
    builder.setLeadingColumnOffset(1);
    if(separator==null)
    {
      builder.appendRow(builder.getLineGapSpec());
      builder.nextLine();
    }
    else
    {
      builder.appendSeparator(separator);
    }
    return builder;
  }

  public static JPanel getOKCancelBar(ActionListener listener, String prefix)
  {
    if(prefix == null) prefix = "";
    JButton ok = new JButton(ResourceManager.getString("ok"));
    ok.setActionCommand(prefix + "ok");
    JButton cancel = new JButton(ResourceManager.getString("cancel"));
    cancel.setActionCommand(prefix + "cancel");
    ok.addActionListener(listener);
    cancel.addActionListener(listener);
    return ButtonBarFactory.buildOKCancelBar(ok, cancel);
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
    JTextField field = new JTextField()
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
    field.setForeground(UIManager.getColor("TextField.foreground"));
    if(width!=-1)
    {
      field.setColumns(width);
    }
    return field;
  }

  public static JTextField createReadOnlyTextField()
  {
    return createReadOnlyTextField(-1);
  }
  
  public static void htmlize(JComponent component)
  {
    Font defaultFont = UIManager.getFont("Button.font");

    String stylesheet = "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: " + defaultFont.getName() + "; font-size: " + defaultFont.getSize() + "pt;	}" + "a, p, li { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: " + defaultFont.getName() + "; font-size: " + defaultFont.getSize() + "pt;	}";

    try
    {
      HTMLDocument doc = null;
      if(component instanceof JEditorPane)
      {
        doc = (HTMLDocument)((JEditorPane)component).getDocument();
      }
      else
      {
        View v = (View)component.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if(v != null)
        {
          doc = (HTMLDocument)v.getDocument();
        }
      }
      if(doc != null)
      {
        doc.getStyleSheet().loadRules(new java.io.StringReader(stylesheet), null);
      } // end of if (doc != null)
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
