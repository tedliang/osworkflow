package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.PermissionDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class PermissionsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("id"), ResourceManager.getString("name")};

  public PermissionsTableModel()
  {
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return true;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    PermissionDescriptor permission = (PermissionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue!=null)
          permission.setId(((Integer)aValue).intValue());
        break;
      case 1:
        permission.setName(aValue!=null ? aValue.toString() : null);
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
      default:
        return String.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    PermissionDescriptor permission = (PermissionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return new Integer(permission.getId());
      case 1:
        return permission.getName();
      default:
        return "";
    }
  }
}
