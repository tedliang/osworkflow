package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class ConditionsTableModel extends ListTableModel
{
  private String[] header = new String[]{"id", "type", "negate"};

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return true;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    ConditionDescriptor condition = (ConditionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue!=null)
          condition.setId(((Integer)aValue).intValue());
        break;
      case 1:
        if(aValue!=null)
          condition.setType(aValue.toString());
        break;
      case 2:
        condition.setNegate(((Boolean)aValue).booleanValue());
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
    switch(columnIndex)
    {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return Boolean.class;
      default:
        return String.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    ConditionDescriptor condition = (ConditionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return condition.hasId() ? new Integer(condition.getId()) : null;
      case 1:
        return condition.getType();
      default:
        return condition.isNegate() ? Boolean.TRUE : Boolean.FALSE;
    }
  }
}
