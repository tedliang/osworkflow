package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class ConditionsTableModel extends ListTableModel
{
  private String[] header = new String[]{"name", "type"};

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    ConditionDescriptor condition = (ConditionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue != null)
          condition.setName(aValue.toString());
        break;
      case 1:
        if(aValue != null)
          condition.setType(aValue.toString());
        break;
    }
  }

  public int getColumnCount()
  {
    return header.length;
  }

  public String getColumnName(int column)
  {
    return header[column];
  }

  public Class getColumnClass(int columnIndex)
  {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    ConditionDescriptor condition = (ConditionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return condition.getName();
      case 1:
        return condition.getType();
    }
    return null;
  }
}
