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
import com.opensymphony.workflow.designer.swing.status.StatusBar;
import com.opensymphony.workflow.designer.swing.status.StatusDisplay;
import com.opensymphony.workflow.designer.swing.status.MemoryDisplay;
import com.opensymphony.workflow.designer.swing.plaf.BlueButtonUI;
import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;

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

  public static StatusBar createStatusBar()
  {
    StatusBar bar = new StatusBar();
    StatusDisplay progress = new StatusDisplay();
    bar.add(progress);
    bar.add(Box.createHorizontalStrut(30));
    bar.add(Box.createHorizontalGlue());
    MemoryDisplay memory = new MemoryDisplay();
    memory.setMaximumSize(new Dimension(180, memory.getMaximumSize().height));
    bar.add(memory);
    return bar;
  }

  public static JPanel createToolbar()
  {
    JToolBar bar = new JToolBar();
    bar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
    JButton step = new JButton(ResourceManager.getIcon("newstep"));
	  step.setUI(new BlueButtonUI());
    step.setToolTipText(ResourceManager.getString("createstep"));
    bar.add(step);
    DragSource ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(step, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.STEP));

    JButton join = new JButton(ResourceManager.getIcon("newjoin"));
	  join.setUI(new BlueButtonUI());
    join.setToolTipText(ResourceManager.getString("createjoin"));
    bar.add(join);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(join, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.JOIN));

    JButton split = new JButton(ResourceManager.getIcon("newsplit"));
    split.setToolTipText(ResourceManager.getString("createsplit"));
	  split.setUI(new BlueButtonUI());
    bar.add(split);
    ds = new DragSource();
    ds.createDefaultDragGestureRecognizer(split, DnDConstants.ACTION_COPY, new TypeDragGesture(ds, DragData.SPLIT));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(bar, BorderLayout.NORTH);

    return panel;
  }
}
