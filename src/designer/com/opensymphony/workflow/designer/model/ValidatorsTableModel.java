package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.ValidatorDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class ValidatorsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("type")};

  public ValidatorsTableModel()
  {
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public int getColumnCount()
  {
    return header.length;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    ValidatorDescriptor validator = (ValidatorDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue!=null)
        	validator.setType(aValue.toString());
        break;
    }
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
        return String.class;
      default:
        return String.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    ValidatorDescriptor validator = (ValidatorDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return validator.getType();
			default:
        return "";
    }
  }
}
