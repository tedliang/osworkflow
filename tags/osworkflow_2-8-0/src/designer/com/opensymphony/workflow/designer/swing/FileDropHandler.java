package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.*;

public abstract class FileDropHandler
{
  private transient java.awt.dnd.DropTargetListener dropListener;

  public FileDropHandler(Component target)
  {
    this(target, true);
  }

  public FileDropHandler(final Component target, final boolean recursive)
  {
    dropListener = new DropTargetListener()
    {
      public void dragEnter(DropTargetDragEvent evt)
      {
        if(isDragOk(evt))
        {
          evt.acceptDrag(DnDConstants.ACTION_COPY);
        }
        else
        {
          evt.rejectDrag();
        }
      }

      public void dragOver(DropTargetDragEvent evt)
      {}

      public void drop(DropTargetDropEvent evt)
      {
        try
        {
          Transferable tr = evt.getTransferable();

          if(tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
          {
            evt.acceptDrop(DnDConstants.ACTION_COPY);

            List fileList = (List)tr.getTransferData(DataFlavor.javaFileListFlavor);

            File[] filesTemp = new File[fileList.size()];
            fileList.toArray(filesTemp);
            final File[] files = filesTemp;

            filesDropped(files);

            evt.getDropTargetContext().dropComplete(true);
          }
          else
          {
            evt.rejectDrop();
          }
        }
        catch(IOException io)
        {
          io.printStackTrace();
          evt.rejectDrop();
        }
        catch(UnsupportedFlavorException ufe)
        {
          ufe.printStackTrace();
          evt.rejectDrop();
        }
      }

      public void dragExit(DropTargetEvent evt)
      {
      }

      public void dropActionChanged(DropTargetDragEvent evt)
      {
        if(isDragOk(evt))
        {
          evt.acceptDrag(DnDConstants.ACTION_COPY);
        }
        else
        {
          evt.rejectDrag();
        }
      }
    };

    makeDropTarget(target, recursive);
  }

  private void makeDropTarget(final Component target, boolean recursive)
  {
    final DropTarget dt = new DropTarget();
    try
    {
      dt.addDropTargetListener(dropListener);
    }
    catch(TooManyListenersException e)
    {
      e.printStackTrace();
    }

    target.addHierarchyListener(new HierarchyListener()
    {
      public void hierarchyChanged(HierarchyEvent evt)
      {
        Component parent = target.getParent();
        if(parent == null)
        {
          target.setDropTarget(null);
        }
        else
        {
          new DropTarget(target, dropListener);
        }
      }
    });
    if(target.getParent() != null)
      new DropTarget(target, dropListener);

    if(recursive && (target instanceof Container))
    {
      Container cont = (Container)target;
      Component[] comps = cont.getComponents();

      for(int i = 0; i < comps.length; i++)
        makeDropTarget(comps[i], recursive);
    }
  }

  private boolean isDragOk(final DropTargetDragEvent evt)
  {
    boolean ok = false;

    DataFlavor[] flavors = evt.getCurrentDataFlavors();
    int i = 0;
    while(!ok && i < flavors.length)
    {
      if(flavors[i].equals(DataFlavor.javaFileListFlavor))
        ok = true;
      i++;
    }
    return ok;
  }

  public abstract void filesDropped(File[] files);

  public static void main(String[] args)
  {
    JFrame frame = new JFrame("FileDrop");
    final JTree tree = new JTree();
    frame.getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);

    new FileDropHandler(tree)
    {
      public void filesDropped(File[] files)
      {
        for(int i = 0; i < files.length; i++)
        {
          System.out.println("dropped " + files[i]);
        }
      }
    };

    frame.setBounds(100, 100, 300, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.show();
  }
}