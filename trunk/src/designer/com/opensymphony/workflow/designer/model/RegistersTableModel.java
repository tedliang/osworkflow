package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.RegisterDescriptor;
import com.opensymphony.workflow.loader.PaletteDescriptor;
import com.opensymphony.workflow.loader.ConfigRegisterDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 * Date: Nov 16, 2004
 * Time: 18:20:16 PM
 */
public class RegistersTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("name"), ResourceManager.getString("type")};
  private WorkflowGraphModel graphModel;

  public WorkflowGraphModel getGraphModel()
  {
    return graphModel;
  }

  public void setGraphModel(WorkflowGraphModel graphModel)
  {
    this.graphModel = graphModel;
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    RegisterDescriptor register = (RegisterDescriptor)list.get(rowIndex);
    switch(columnIndex)
    {
      case 0:
        if(aValue != null)
          register.setVariableName(aValue.toString());
        break;
      case 1:
        if(aValue != null)
          register.setType(aValue.toString());
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
    RegisterDescriptor register = (RegisterDescriptor)list.get(rowIndex);
    PaletteDescriptor palette = graphModel.getPalette();
    ConfigRegisterDescriptor config = palette.getRegister(register.getVariableName());
    switch(columnIndex)
    {
      case 0:
        String name = config!=null ? config.getVariableName() : ResourceManager.getString("unknown");
        return name;
      case 1:
        return register.getType();
      default:
        return "";
    }
  }
}
