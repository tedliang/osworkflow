package com.opensymphony.workflow.designer.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;

/**
 * @author jackflit
 * Date: 2003-11-27
 */
public class DragData implements Serializable
{
  public static final DataFlavor WORKFLOW_FLAVOR = new DataFlavor(DragData.class, "WORKFLOWDATA");

  public static final DragData JOIN = new DragData("JOIN");
  public static final DragData STEP = new DragData("STEP");
  public static final DragData SPLIT = new DragData("SPLIT");

  private String type;

  private DragData(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String string)
  {
    type = string;
  }

  public boolean equals(Object obj)
  {
    if(obj instanceof DragData)
    {
      return ((DragData)obj).getType().equals(type);
    }
    return false;
  }

}
