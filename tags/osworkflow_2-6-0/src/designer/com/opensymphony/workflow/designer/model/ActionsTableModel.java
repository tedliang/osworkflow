package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class ActionsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("id"), ResourceManager.getString("name"),
                                         ResourceManager.getString("view"), ResourceManager.getString("auto")};

  public ActionsTableModel()
  {
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return columnIndex!=0;
  }

  public int getColumnCount()
  {
    return header.length;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    ActionDescriptor action = (ActionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue!=null)
        action.setId(((Integer)aValue).intValue());
        break;
      case 1:
        if(aValue!=null)
          action.setName(aValue.toString());
        break;
      case 2:
        action.setView(aValue!=null ? aValue.toString() : null);
        break;
      case 3:
        action.setAutoExecute(((Boolean)aValue).booleanValue());
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
        return Integer.class;
      case 1:
      case 2:
        return String.class;
      case 3:
        return Boolean.class;
      default:
        return String.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    ActionDescriptor action = (ActionDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return new Integer(action.getId());
      case 1:
        return action.getName();
      case 2:
        return action.getView()!=null ? action.getView() : "";
      case 3:
        return action.getAutoExecute() ? Boolean.TRUE : Boolean.FALSE;
      default:
        return "";
    }
  }
}
