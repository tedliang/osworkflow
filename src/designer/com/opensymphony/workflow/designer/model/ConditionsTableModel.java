package com.opensymphony.workflow.designer.model;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConditionsDescriptor;
import com.opensymphony.workflow.loader.PaletteDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:04:16 AM
 */
public class ConditionsTableModel extends ListTableModel
{
  private String[] header = new String[]{ResourceManager.getString("name"), ResourceManager.getString("type"), ResourceManager.getString("negate")};
  private WorkflowGraphModel graphModel;
  private int type;
  public static final int JOIN = 1;
  public static final int PERMISSION = 2;
  public static final int RESULT = 3;

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

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
		AbstractDescriptor dd = (AbstractDescriptor)list.get(rowIndex); 
    if (dd instanceof ConditionsDescriptor)
   		return null;
    
    ConditionDescriptor condition = (ConditionDescriptor)list.get(rowIndex);
    PaletteDescriptor palette = graphModel.getPalette();
    switch(columnIndex)
    {
      case 0:
        ConfigConditionDescriptor descriptor = null;
        switch(type)
        {
          case JOIN:
            descriptor = palette.getJoinCondition(condition.getName());
            break;
          case PERMISSION:
            descriptor = palette.getPermissionCondition(condition.getName());
            break;
          case RESULT:
            descriptor = palette.getResultCondition(condition.getName());
            break;
        }
        return descriptor != null && descriptor.getDisplayName() != null ? descriptor.getDisplayName() : ResourceManager.getString("unknown");
      case 1:
        return condition.getType();
      case 2:
        return String.valueOf(condition.isNegate());
    }
    return null;
  }
}
