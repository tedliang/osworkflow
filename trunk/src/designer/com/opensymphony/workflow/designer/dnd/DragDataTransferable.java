package com.opensymphony.workflow.designer.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Gulei
 */
public class DragDataTransferable implements Transferable
{
  private DragData data;

  public DragDataTransferable(DragData data)
  {
    this.data = data;
  }

  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[]{DragData.scriptFlavor};
  }

  public boolean isDataFlavorSupported(DataFlavor arg0)
  {
    return arg0.equals(DragData.scriptFlavor);
  }

  public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException
  {
    if(arg0.equals(DragData.scriptFlavor))
    {
      return data;
    }
    else
    {
      throw new UnsupportedFlavorException(arg0);
    }
  }

}
