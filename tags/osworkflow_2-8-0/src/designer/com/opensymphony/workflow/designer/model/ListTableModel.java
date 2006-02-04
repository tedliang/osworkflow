package com.opensymphony.workflow.designer.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 10:20:51 PM
 */
public abstract class ListTableModel extends AbstractTableModel
{
  protected List list = new ArrayList();

  public int getRowCount()
  {
    return list == null ? 0 : list.size();
  }

  public void setList(List l)
  {
    this.list = l;
    fireTableDataChanged();
  }

	public List getList()
	{
		return Collections.unmodifiableList(list);
	}

  public void add(Object item)
  {
    list.add(item);
    fireTableRowsInserted(list.size(), list.size());
  }

  public void remove(int index)
  {
    list.remove(index);
    fireTableRowsDeleted(index, index);
  }

  public Object get(int i)
  {
    return list.get(i);
  }
}
