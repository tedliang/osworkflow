package com.opensymphony.workflow.designer.model;

import java.util.*;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 * Date: Nov 16, 2004
 * Time: 16:56:16 PM
 */
public class AttributesTableModel extends MapTableModel
{
  private String[] header = new String[]{ResourceManager.getString("name"), ResourceManager.getString("value")};

  public AttributesTableModel()
  {
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    String sKey = getRowAttributeKey(rowIndex);
    if (sKey.length()>0)
    {
    	switch(columnIndex)
    	{
    		case 0:			// Name (Key)
    			break;
    		case 1:
    			map.put(sKey, aValue);
    			break;
    		default:
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
    String sKey = getRowAttributeKey(rowIndex);
    if (sKey.length()>0)
    {
    	switch(columnIndex)
    	{
    		case 0:			// Name (Key)
    			return sKey;
    		case 1:		 // Value
					String sValue = (String)map.get(sKey); 
    			if (sValue!=null)
    				return sValue;	 
    		default:
    			return "";
    	}
    }
    return "";
  }
  
  private String getRowAttributeKey(int rowIndex)
  {
  	int counter = 0;
  	Iterator it = map.keySet().iterator();
  	while (it.hasNext())
  	{
  		String sKey = (String)it.next();
  		if (counter==rowIndex)
  			return sKey;
  		counter++;  
  	}
  	return "";
  }
}
