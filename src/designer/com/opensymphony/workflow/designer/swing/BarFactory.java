package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import javax.swing.*;

import com.opensymphony.workflow.config.WorkspaceManager;
import com.opensymphony.workflow.designer.actions.*;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.designer.dnd.TypeDragGesture;

/**
 * @author jackflit
 * Date: 2003-11-27
 */
public class BarFactory
{
  public static JMenuBar createMenubar(WorkspaceManager manager)
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");

    JMenu itemNew = new JMenu("New");
    itemNew.setIcon(ImageLoader.getIcon("new.gif"));
    itemNew.setHorizontalTextPosition(JButton.RIGHT);
    fileMenu.add(itemNew);

    JMenuItem item;

    item = new JMenuItem(new NewWorkspace());
    item.setHorizontalTextPosition(JMenu.RIGHT);
    item.setIcon(ImageLoader.getIcon("new.gif"));
    itemNew.add(item);

    NewWorkflow newWorkflow = new NewWorkflow();
    manager.addWorkspaceListener(newWorkflow);
    item = new JMenuItem(newWorkflow);
    item.setHorizontalTextPosition(JMenu.RIGHT);
    item.setIcon(ImageLoader.getIcon("add.gif"));
    itemNew.add(item);

    item = new JMenuItem(new OpenWorkspace());
    item.setIcon(ImageLoader.getIcon("open.gif"));
    fileMenu.add(item);

    CloseWorkspace close = new CloseWorkspace();
    manager.addWorkspaceListener(close);
    item = new JMenuItem(close);
    item.setIcon(ImageLoader.getIcon("close.gif"));
    fileMenu.add(item);

    ImportWorkflow importWorkflow = new ImportWorkflow();
    manager.addWorkspaceListener(importWorkflow);
    item = new JMenuItem(importWorkflow);
    item.setIcon(ImageLoader.getIcon("import.gif"));
    fileMenu.add(item);

    PNGExport export = new PNGExport();
    manager.addWorkspaceListener(export);
    item = new JMenuItem(export);
    item.setIcon(ImageLoader.getIcon("export.gif"));
    fileMenu.add(item);

    fileMenu.addSeparator();

    SaveWorkspace save = new SaveWorkspace();
    manager.addWorkspaceListener(save);
    item = new JMenuItem(save);
    item.setIcon(ImageLoader.getIcon("save.gif"));
    fileMenu.add(item);

    fileMenu.addSeparator();

    item = new JMenuItem(new Quit());
    item.setIcon(ImageLoader.getIcon("close.gif"));
    fileMenu.add(item);

    JMenu viewMenu = new JMenu("Auto Layout");
    AutoLayout auto = new AutoLayout(null);
    manager.addWorkspaceListener(auto);
    item = new JMenuItem(auto);
    item.setIcon(ImageLoader.getIcon("layout.gif"));
    viewMenu.add(item);

    menuBar.add(fileMenu);
    menuBar.add(viewMenu);

    return menuBar;
  }

  public static JPanel createToolbar()
  {
    JToolBar bar1 = new JToolBar("toolbar");
    bar1.addSeparator();
    DragSource ds = new DragSource();

    JButton step = new JButton(ImageLoader.getIcon("step.gif"));
    step.setToolTipText("Create Step");
    bar1.add(step);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(step, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.STEP));

    JButton join = new JButton(ImageLoader.getIcon("join.gif"));
    join.setToolTipText("Create Join");
    bar1.add(join);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(join, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.JOIN));

    JButton split = new JButton(ImageLoader.getIcon("split.gif"));
    split.setToolTipText("Create Split");
    bar1.add(split);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(split, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.SPLIT));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(bar1, BorderLayout.NORTH);

    return panel;
  }
}
