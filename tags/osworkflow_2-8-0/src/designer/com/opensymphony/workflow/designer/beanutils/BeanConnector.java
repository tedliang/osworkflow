package com.opensymphony.workflow.designer.beanutils;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;

import com.opensymphony.workflow.designer.editor.DetailPanel;
import com.opensymphony.workflow.designer.editor.ResultEditor;
import com.opensymphony.workflow.designer.editor.WorkflowEditor;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

public class BeanConnector
{
  Object source;
  DetailPanel panel = null;

  Map mappings = new HashMap();

  public BeanConnector(Object aSource)
  {
    source = aSource;
  }

  public BeanConnector()
  {
  }

  public void setSource(Object aSource)
  {
    source = aSource;

    update();
  }

  public void setPanel(DetailPanel panel)
  {
    this.panel = panel;
  }

  public void update()
  {
    // Set values
    for(Iterator iterator = mappings.entrySet().iterator(); iterator.hasNext();)
    {
      Map.Entry entry = (Map.Entry)iterator.next();
      getProperty((Component)entry.getKey());
    }
  }

  public Object getSource()
  {
    return source;
  }

  public Component connect(final Component aComponent, final String aName)
  {
    mappings.put(aComponent, aName);

    if(source != null)
    {
      getProperty(aComponent);
    }

    // Connect to component
    if(aComponent instanceof JSpinner)
    {
      ((JSpinner)aComponent).addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          setProperty(aComponent);
        }
      });
    }
    else if(aComponent instanceof JList)
    {
      ((JList)aComponent).addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          setProperty(aComponent);
        }
      });
    }
    else if(aComponent instanceof JComboBox)
    {
      ((JComboBox)aComponent).addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setProperty(aComponent);
        }
      });
    }
    else if(aComponent instanceof JComponent)
    {
      aComponent.addFocusListener(new FocusListener()
      {
        public void focusGained(FocusEvent e)
        {

        }

        public void focusLost(FocusEvent e)
        {
          if(panel != null)
          {
            if(panel instanceof ResultEditor)
            {
							setProperty(e.getComponent());
            }
          	else if (panel instanceof WorkflowEditor)
          	{
          		WorkflowDescriptor desc = (WorkflowDescriptor)panel.getDescriptor(); 
          		String oldName = desc.getName();
          		String newName = ((JTextField)e.getComponent()).getText();
          		newName = newName.trim();
          		if ((!oldName.equals(newName))&&(newName.length()>0))
          		{
								WorkflowDesigner.INSTANCE.renameWorkflow(oldName, newName);
								setProperty(e.getComponent());
          		}
          }
        }
					else
						setProperty(e.getComponent());
        }
      });
    }

    return aComponent;
  }

  public void setProperty(Component comp)
  {
    String name = (String)mappings.get(comp);
    Object value = null;
    if(comp instanceof JFormattedTextField)
    {
      try
      {
        value = ((JFormattedTextField)comp).getFormatter().stringToValue(((JFormattedTextField)comp).getText());
      }
      catch(ParseException e)
      {
        e.printStackTrace();
        return;
      }
    }
    else if(comp instanceof JTextField)
    {
      value = ((JTextField)comp).getText();
    }
    else if(comp instanceof JSpinner)
    {
      value = ((JSpinner)comp).getValue();
    }
    else if(comp instanceof JRadioButton)
    {
      JRadioButton button = (JRadioButton)comp;
      if(!button.isSelected())
        return;
      value = button.getActionCommand();
    }
    else if(comp instanceof JCheckBox)
    {
      value = new Boolean(((JCheckBox)comp).isSelected());
    }
    else if(comp instanceof JList)
    {
      value = new Integer(((JList)comp).getSelectedIndex());
    }
    else if(comp instanceof JComboBox)
    {
      value = ((JComboBox)comp).getSelectedItem();
    }

    if(source instanceof Map)
    {
      if(value == null)
        ((Map)source).remove(name);
      else
        ((Map)source).put(name, value);
    }
    else
    {
      try
      {
        PropertyUtils.setProperty(source, name, value);
      }
      catch(IllegalArgumentException e)
      {
        try
        {
          PropertyUtils.setProperty(source, name, value.toString());
        }
        catch(Exception e1)
        {
          e1.printStackTrace();
        }
      }
      catch(IndexOutOfBoundsException e)
      {
        e.printStackTrace();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  public void getProperty(Component comp)
  {
    try
    {
      String name = (String)mappings.get(comp);
      if(name == null)
        return;
      Object value;
      if(source instanceof Map)
      {
        value = ((Map)source).get(name);
      }
      else
      {
        //if we try to index into a non-existent property, just set it to null
        try
        {
          value = PropertyUtils.getProperty(source, name);
        }
        catch(NullPointerException ex)
        {
          value = null;
        }
        catch(NoSuchMethodException ex)
        {
          value = null;
        }
        catch(IndexOutOfBoundsException ex)
        {
          value = null;
        }
      }

      //         LogFactory.getLog(this.getClass()).info("Get " + name + "=" + value);

      if(comp instanceof JFormattedTextField)
      {
        if(value == null)
        {
          ((JFormattedTextField)comp).setText("");
        }
        else
          ((JFormattedTextField)comp).setValue(value);
      }
      else if(comp instanceof JTextField)
      {
        if(value == null)
          value = "";
        ((JTextField)comp).setText(value.toString());
      }
      else if(comp instanceof JSpinner)
      {
        if(value != null)
          ((JSpinner)comp).setValue(value);
      }
      else if(comp instanceof JRadioButton)
      {
        if(value == null)
          value = Boolean.FALSE;

        if(((JRadioButton)comp).getActionCommand().equals(value.toString()))
          ((JRadioButton)comp).setSelected(true);
      }
      else if(comp instanceof JCheckBox)
      {
        if(value == null)
          value = Boolean.FALSE;

        ((JCheckBox)comp).setSelected(new Boolean(value.toString()).booleanValue());
      }
      else if(comp instanceof JList)
      {
        if(value == null)
          value = "0";
        ((JList)comp).setSelectedIndex(Integer.parseInt(value.toString()));
      }
      else if(comp instanceof JComboBox)
      {
        if(value == null)
          value = "0";
        ((JComboBox)comp).setSelectedItem(value);
      }
      else if(comp instanceof JLabel)
      {
        if(value == null)
          value = "";
        ((JLabel)comp).setText(value.toString());
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
