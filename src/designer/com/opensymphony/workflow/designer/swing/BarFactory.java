package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import javax.swing.*;

import com.opensymphony.workflow.config.WorkspaceManager;
import com.opensymphony.workflow.designer.actions.*;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.designer.dnd.TypeDragGesture;
import com.opensymphony.workflow.designer.ResourceManager;

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
    itemNew.setIcon(ResourceManager.getIcon("newfile"));
    itemNew.setHorizontalTextPosition(JButton.RIGHT);
    fileMenu.add(itemNew);

    JMenuItem item;

    item = new JMenuItem(new NewWorkspace());
    item.setHorizontalTextPosition(JMenu.RIGHT);
    item.setIcon(ResourceManager.getIcon("newspace"));
    itemNew.add(item);

    NewWorkflow newWorkflow = new NewWorkflow();
    manager.addWorkspaceListener(newWorkflow);
    item = new JMenuItem(newWorkflow);
    item.setHorizontalTextPosition(JMenu.RIGHT);
    item.setIcon(ResourceManager.getIcon("newflow"));
    itemNew.add(item);

    item = new JMenuItem(new OpenWorkspace());
    item.setIcon(ResourceManager.getIcon("openspace"));
    fileMenu.add(item);

    CloseWorkspace close = new CloseWorkspace();
    manager.addWorkspaceListener(close);
    item = new JMenuItem(close);
    item.setIcon(ResourceManager.getIcon("closespace"));
    fileMenu.add(item);

    ImportWorkflow importWorkflow = new ImportWorkflow();
    manager.addWorkspaceListener(importWorkflow);
    item = new JMenuItem(importWorkflow);
    item.setIcon(ResourceManager.getIcon("importflow"));
    fileMenu.add(item);

    PNGExport export = new PNGExport();
    manager.addWorkspaceListener(export);
    item = new JMenuItem(export);
    item.setIcon(ResourceManager.getIcon("exportflow"));
    fileMenu.add(item);

    fileMenu.addSeparator();

    SaveWorkspace save = new SaveWorkspace();
    manager.addWorkspaceListener(save);
    item = new JMenuItem(save);
    item.setIcon(ResourceManager.getIcon("savespace"));
    fileMenu.add(item);

    fileMenu.addSeparator();

    item = new JMenuItem(new Quit());
    item.setIcon(ResourceManager.getIcon("quit"));
    fileMenu.add(item);

    JMenu viewMenu = new JMenu("Auto Layout");
    AutoLayout auto = new AutoLayout(null);
    manager.addWorkspaceListener(auto);
    item = new JMenuItem(auto);
    item.setIcon(ResourceManager.getIcon("autolayout"));
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

    JButton step = new JButton(ResourceManager.getIcon("newstep"));
    step.setToolTipText("Create Step");
    bar1.add(step);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(step, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.STEP));

    JButton join = new JButton(ResourceManager.getIcon("newjoin"));
    join.setToolTipText("Create Join");
    bar1.add(join);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(join, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.JOIN));

    JButton split = new JButton(ResourceManager.getIcon("newsplit"));
    split.setToolTipText("Create Split");
    bar1.add(split);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(split, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.SPLIT));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(bar1, BorderLayout.NORTH);

    return panel;
  }
}
