package com.opensymphony.workflow.designer.dnd;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 * @author jackflit
 * Date: 2003-11-27
 */
public class TypeDragGesture implements DragGestureListener, DragSourceListener
{

  private DragSource ds;
  private DragData type;

  public TypeDragGesture(DragSource ds, DragData type)
  {
    this.ds = ds;
    this.type = type;
  }

  DragDataTransferable transferable;

  public void dragGestureRecognized(DragGestureEvent dge)
  {

    transferable = new DragDataTransferable(type);

    ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
  }

  public void dragEnter(DragSourceDragEvent dsde)
  {

  }

  public void dragOver(DragSourceDragEvent dsde)
  {

  }

  public void dropActionChanged(DragSourceDragEvent dsde)
  {

  }

  public void dragDropEnd(DragSourceDropEvent dsde)
  {
    if(dsde.getDropSuccess())
    {
      //			System.out.println("Succeeded");
    }
    else
    {
      //			System.out.println("Failed");
    }
  }

  public void dragExit(DragSourceEvent dse)
  {
  }

}
