package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class FunctionsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("name"), ResourceManager.getString("type")};

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    FunctionDescriptor function = (FunctionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue != null)
          function.setName(aValue.toString());
        break;
      case 1:
        if(aValue != null)
          function.setType(aValue.toString());
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
    FunctionDescriptor function = (FunctionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        String name = function.getName();
        return name==null ? "<unknown>" : name;
      case 1:
        return function.getType();
      default:
        return "";
    }
  }
}
