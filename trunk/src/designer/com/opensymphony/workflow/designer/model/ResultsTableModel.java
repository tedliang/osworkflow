package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:37:35 AM
 */
public class ResultsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("id"), ResourceManager.getString("owner"),
                                         ResourceManager.getString("status"), ResourceManager.getString("status.old"),
                                         ResourceManager.getString("step")};

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
      case 4:
        return Integer.class;
      case 1:
      case 2:
      case 3:
        return String.class;
      default:
        return String.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    ResultDescriptor result = (ResultDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        return result.hasId() ? new Integer(result.getId()) : null;
      case 1:
        return result.getOwner()!=null ? result.getOwner() : "";
      case 2:
        return result.getStatus()!=null ? result.getStatus() : "";
      case 3:
        return result.getOldStatus()!=null ? result.getOldStatus() : "";
      case 4:
        return new Integer(result.getStep());
      default:
        return "";
    }
  }
}
