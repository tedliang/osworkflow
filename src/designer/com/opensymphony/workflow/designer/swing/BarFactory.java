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
import com.opensymphony.workflow.designer.ActionManager;

/**
 * @author jackflit
 * Date: 2003-11-27
 */
public class BarFactory
{
  public static JMenuBar createMenubar(WorkspaceManager manager)
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu(ResourceManager.getString("menu.file"));

    JMenu itemNew = new JMenu(ResourceManager.getString("menu.new"));
    itemNew.setIcon(ResourceManager.getIcon("newfile"));
    itemNew.setHorizontalTextPosition(JMenu.RIGHT);
    fileMenu.add(itemNew);

    JMenuItem item;

    Action newSpace = new NewWorkspace();
    item = new JMenuItem(ActionManager.register("newspace", newSpace));
    itemNew.add(item);

    NewWorkflow newWorkflow = new NewWorkflow();
    manager.addWorkspaceListener(newWorkflow);
    item = new JMenuItem(ActionManager.register("newflow", newWorkflow));
    itemNew.add(item);

    item = new JMenuItem(ActionManager.register("openspace", new OpenWorkspace()));
    fileMenu.add(item);

    CloseWorkspace close = new CloseWorkspace();
    manager.addWorkspaceListener(close);
    item = new JMenuItem(ActionManager.register("closespace", close));
    fileMenu.add(item);

    ImportWorkflow importWorkflow = new ImportWorkflow();
    manager.addWorkspaceListener(importWorkflow);
    item = new JMenuItem(ActionManager.register("importflow", importWorkflow));
    fileMenu.add(item);

    PNGExport export = new PNGExport();
    manager.addWorkspaceListener(export);
    item = new JMenuItem(ActionManager.register("pngexport", export));
    fileMenu.add(item);

    fileMenu.addSeparator();

    SaveWorkspace save = new SaveWorkspace();
    manager.addWorkspaceListener(save);
    item = new JMenuItem(ActionManager.register("savespace", save));
    fileMenu.add(item);

    fileMenu.addSeparator();

    item = new JMenuItem(ActionManager.register("quit", new Quit()));
    fileMenu.add(item);

    JMenu viewMenu = new JMenu(ResourceManager.getString("menu.layout"));
    AutoLayout auto = new AutoLayout(null);
    manager.addWorkspaceListener(auto);
    item = new JMenuItem(ActionManager.register("autolayout", auto));
    viewMenu.add(item);

    menuBar.add(fileMenu);
    menuBar.add(viewMenu);

    return menuBar;
  }

  public static JPanel createToolbar()
  {
    JToolBar bar1 = new JToolBar();
    bar1.addSeparator();

    JButton step = new JButton(ResourceManager.getIcon("newstep"));
    step.setToolTipText(ResourceManager.getString("createstep"));
    bar1.add(step);
    DragSource ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(step, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.STEP));

    JButton join = new JButton(ResourceManager.getIcon("newjoin"));
    join.setToolTipText(ResourceManager.getString("createjoin"));
    bar1.add(join);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(join, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.JOIN));

    JButton split = new JButton(ResourceManager.getIcon("newsplit"));
    split.setToolTipText(ResourceManager.getString("createsplit"));
    bar1.add(split);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(split, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.SPLIT));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(bar1, BorderLayout.NORTH);

    return panel;
  }
}
