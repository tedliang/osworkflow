package com.opensymphony.workflow.designer.model;

import java.util.*;

import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.PaletteDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 * Date: Nov 20, 2004
 * Time: 3:05:16 PM
 */
public class TriggersTableModel extends MapTableModel
{
  private String[] header = new String[]{ResourceManager.getString("id"), ResourceManager.getString("name"), ResourceManager.getString("type")};
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
 		int id = getRowTriggerID(rowIndex); 
    if (id==-1)
    	return;
    
    FunctionDescriptor function = (FunctionDescriptor)map.get(new Integer(id));
    if (function!=null)
   	{
	   	switch(columnIndex)
	    {
	      case 0:								// ID
	      	break;
	      case 1:								// Name
	        if(aValue != null)
	          function.setName(aValue.toString());
	        break;
	      case 2:								// Type
	        if(aValue != null)
	          function.setType(aValue.toString());
	        break;	
	    }
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
    // get the row id
    int id = getRowTriggerID(rowIndex);
   	if (id==-1)
   		return ""; 	
    FunctionDescriptor function = (FunctionDescriptor)map.get(new Integer(id));
    if (function!=null)
    {
	    PaletteDescriptor palette = graphModel.getPalette();
	    ConfigFunctionDescriptor config = palette.getTriggerFunction(function.getName());
	    switch(columnIndex)
	    {
	    	case 0:		// ID
	    		return String.valueOf(id);
	      case 1:		// Name
	        String name = config!=null && config.getDisplayName()!=null ? config.getDisplayName() : ResourceManager.getString("unknown");
	        return name;
	      case 2:		// Type
	        return function.getType();
	      default:
	        return "";
	    }
    }
    return "";
  }
	
	private int getRowTriggerID(int rowIndex)
	{
		int counter = 0;
		Iterator it = map.keySet().iterator();  
		
		while (it.hasNext())
		{ 
			Integer id = (Integer)it.next();
			if (counter==rowIndex)
			{
				return id.intValue();
			}
			counter++;
		}
		return -1;			// not found!!	
	}
}
