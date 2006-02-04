package com.opensymphony.workflow.designer.model;

import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 * @author Andrea Capitani (a.capitani@leonardomultimedia.it)
 * Date: Nov 19, 2004
 * Time: 11:45:51 AM
 */
public abstract class MapTableModel extends AbstractTableModel
{
  protected Map map = new HashMap();

  public int getRowCount()
  {
    return map == null ? 0 : map.size();
  }

  public void setMap(Map m)
	{
		this.map = m;
		fireTableDataChanged();
	}
	
	public Map getMap()
	{
		return Collections.unmodifiableMap(map);
	}

  public void add(Object key, Object value)
  {
    map.put(key, value);
    fireTableDataChanged();
  }

  public void remove(Object key)
  {
    map.remove(key);
    fireTableDataChanged();
  }

  public Object get(Object key)
  {
    return map.get(key);
  }
}
