package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

import com.opensymphony.workflow.designer.actions.*;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.designer.layout.LayoutAlgorithm;
import com.opensymphony.workflow.designer.layout.SugiyamaLayoutAlgorithm;
import com.opensymphony.workflow.loader.*;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

public class WorkflowGraph extends JGraph implements DropTargetListener
{
  private Layout layout = new Layout();
  private Point menuLocation = new Point();

  private WorkflowDescriptor descriptor;
  private JPopupMenu genericMenu;
  private JPopupMenu edgeMenu;
  private JPopupMenu cellMenu;

  private UndoManager undoManager = new UndoManager();
  private static final Color GRID_COLOR = new Color(240, 240, 240);

  public WorkflowGraph(GraphModel model, WorkflowDescriptor descriptor, Layout layout, boolean doAutoLayout)
  {
    super(model);
    getGraphLayoutCache().setFactory(new WorkflowCellViewFactory());
    getGraphLayoutCache().setSelectsAllInsertedCells(false);
    ToolTipManager.sharedInstance().registerComponent(this);
    this.layout = layout;
    setDescriptor(descriptor);
    if(doAutoLayout)
    {
      autoLayout();
    }

    setGridEnabled(true);
    setSizeable(true);
    setGridColor(GRID_COLOR);
    setGridSize(12);
    //setTolerance(2);
    setGridVisible(true);
    setGridMode(JGraph.LINE_GRID_MODE);

    setBendable(true);
    setMarqueeHandler(new WorkflowMarqueeHandler(this));
    setCloneable(false);
    setPortsVisible(true);

    // one set of menu <==> one graph <==> one workflow descriptor
    genericMenu = new JPopupMenu();
    JMenu n = new JMenu(ResourceManager.getString("create.new"));
    genericMenu.add(n);
    n.add(new CreateStep(descriptor, getWorkflowGraphModel(), menuLocation));
    //	n.add(new CreateInitialAction(descriptor, getWorkflowGraphModel(), menuLocation));
    n.add(new CreateJoin(descriptor, getWorkflowGraphModel(), menuLocation));
    n.add(new CreateSplit(descriptor, getWorkflowGraphModel(), menuLocation));
    JMenu g = new JMenu(ResourceManager.getString("grid.size"));
    genericMenu.add(g);
    g.add(new SetGridSize(this, menuLocation, 1));
    g.add(new SetGridSize(this, menuLocation, 2));
    g.add(new SetGridSize(this, menuLocation, 4));
    g.add(new SetGridSize(this, menuLocation, 8));
    g.add(new SetGridSize(this, menuLocation, 12));
    g.add(new SetGridSize(this, menuLocation, 16));

    cellMenu = new JPopupMenu();
    cellMenu.add(new Delete(descriptor, this, menuLocation));

    edgeMenu = new JPopupMenu();
    n = new JMenu(ResourceManager.getString("edge.color"));
    edgeMenu.add(n);
    n.add(new ResultEdgeColor(this, menuLocation, Color.black, ResourceManager.getString("edge.color.black")));
    n.add(new ResultEdgeColor(this, menuLocation, Color.LIGHT_GRAY, ResourceManager.getString("edge.color.gray")));
    n.add(new ResultEdgeColor(this, menuLocation, Color.red, ResourceManager.getString("edge.color.red")));
    n.add(new ResultEdgeColor(this, menuLocation, Color.ORANGE, ResourceManager.getString("edge.color.orange")));
    n.add(new ResultEdgeColor(this, menuLocation, new Color(0, 200, 0), ResourceManager.getString("edge.color.green")));
    n.add(new ResultEdgeColor(this, menuLocation, Color.MAGENTA, ResourceManager.getString("edge.color.magenta")));
    n.add(new ResultEdgeColor(this, menuLocation, Color.blue, ResourceManager.getString("edge.color.blue")));
    n = new JMenu(ResourceManager.getString("edge.width"));
    edgeMenu.add(n);
    for(int i = 1; i <= 5; i++)
    {
      n.add(new ResultEdgeLineWidth(this, menuLocation, i));
    }
    edgeMenu.add(new Delete(descriptor, this, menuLocation));
    model.addUndoableEditListener(undoManager);
    new DropTarget(this, this);
  }

  public String convertValueToString(Object value)
  {
    if(value == null) return null;
    if(value instanceof EdgeView)
    {
      return ((EdgeView)value).getCell().toString();
    }
    else
    {
      return value.toString();
    }
  }

	public WorkflowDescriptor getDescriptor()
	{
		return descriptor;
	}

  public void setDescriptor(WorkflowDescriptor descriptor)
  {
    if(descriptor != null)
    {
      this.descriptor = descriptor;
      List initialActionList = descriptor.getInitialActions();
      addInitialActions(initialActionList);
      List stepsList = descriptor.getSteps();
      for(int i = 0; i < stepsList.size(); i++)
      {
        StepDescriptor step = (StepDescriptor)stepsList.get(i);
        addStepDescriptor(step);
      }
      List splitsList = descriptor.getSplits();
      for(int i = 0; i < splitsList.size(); i++)

      {
        SplitDescriptor split = (SplitDescriptor)splitsList.get(i);
        addSplitDescriptor(split);
      }
      List joinsList = descriptor.getJoins();
      for(int i = 0; i < joinsList.size(); i++)
      {
        JoinDescriptor join = (JoinDescriptor)joinsList.get(i);
        addJoinDescriptor(join);
      }
      getWorkflowGraphModel().insertResultConnections();
    }
  }

  public Layout getGraphLayout()
  {
    return layout;
  }

  public void autoLayout()
  {
    if(descriptor.getSteps().size() > 0)
    {
      LayoutAlgorithm algo = new SugiyamaLayoutAlgorithm();
      Properties p = new Properties();
      p.put(SugiyamaLayoutAlgorithm.KEY_HORIZONTAL_SPACING, "110");
      p.put(SugiyamaLayoutAlgorithm.KEY_VERTICAL_SPACING, "70");
      algo.perform(this, true, p);
    }
  }

  public void addInitialActions(List initialActionList)
  {
    InitialActionCell initialActionCell = new InitialActionCell(ResourceManager.getString("action.initial.step"));
    // Create Vertex Attributes
    if(layout != null)
    {
      double[] bounds = layout.getBounds(initialActionCell.getId(), "InitialActionCell");
      if(bounds != null)
      {
        Rectangle2D rect = new Rectangle();
        rect.setRect(bounds[0], bounds[1], bounds[2], bounds[3]);
        initialActionCell.getAttributes().put(GraphConstants.BOUNDS, rect);
      }
      if(initialActionCell.getChildCount() == 0)
      {
        WorkflowPort port = new WorkflowPort();
        initialActionCell.add(port);
      }
    }
    else
    {
      WorkflowPort port = new WorkflowPort();
      initialActionCell.add(port);

    }
    getWorkflowGraphModel().insertInitialActions(initialActionList, initialActionCell, null, null, null);
  }

  public void addJoinDescriptor(JoinDescriptor descriptor)
  {
    JoinCell join = new JoinCell(descriptor);
    // Create Vertex Attributes
    if(layout != null)
    {
      double[] bounds = layout.getBounds(join.getId(), "JoinCell");
      if(bounds != null)
      {
        Rectangle2D rect = new Rectangle();
        rect.setRect(bounds[0], bounds[1], bounds[2], bounds[3]);
        join.getAttributes().put(GraphConstants.BOUNDS, rect);
      }
      if(join.getChildCount() == 0)
      {
        WorkflowPort port = new WorkflowPort();
        join.add(port);
      }

    }
    else
    {
      WorkflowPort port = new WorkflowPort();
      join.add(port);

    }
    getWorkflowGraphModel().insertJoinCell(join, null, null, null);
  }

  public void addSplitDescriptor(SplitDescriptor descriptor)
  {
    SplitCell split = new SplitCell(descriptor);
    if(layout != null)
    {
      double[] bounds = layout.getBounds(split.getId(), "SplitCell");
      if(bounds != null)
      {
        Rectangle2D rect = new Rectangle();
        rect.setRect(bounds[0], bounds[1], bounds[2], bounds[3]);
        split.getAttributes().put(GraphConstants.BOUNDS, rect);
      }
      if(split.getChildCount() == 0)
      {
        WorkflowPort port = new WorkflowPort();
        split.add(port);
      }
    }
    else
    {
      WorkflowPort port = new WorkflowPort();
      split.add(port);

    }
    getWorkflowGraphModel().insertSplitCell(split, null, null, null);
  }

  public void addStepDescriptor(StepDescriptor descriptor)
  {
    StepCell step = CellFactory.createStep((WorkflowDescriptor)descriptor.getParent(), descriptor, getWorkflowGraphModel(), null);
    if(layout != null)
    {
      double[] bounds = layout.getBounds(step.getId(), "StepCell");
      if(bounds != null)
      {
        Rectangle2D rect = new Rectangle();
        rect.setRect(bounds[0], bounds[1], bounds[2], bounds[3]);
        step.getAttributes().put(GraphConstants.BOUNDS, rect);
      }
    }
    // Insert into Model
   // getWorkflowGraphModel().insertStepCell(step, null, null, null);
  }

  public WorkflowGraphModel getWorkflowGraphModel()
  {
    return (WorkflowGraphModel)getModel();
  }

  public void showMenu(int x, int y)
  {
    menuLocation.x = x;
    menuLocation.y = y;
    genericMenu.show(this, x, y);
  }

  public void showDelete(int x, int y)
  {
    menuLocation.x = x;
    menuLocation.y = y;
    cellMenu.show(this, x, y);
  }

  public void showEdgeMenu(int x, int y)
  {
    menuLocation.x = x;
    menuLocation.y = y;
    edgeMenu.show(this, x, y);
  }

  public boolean removeEdge(ResultEdge edge)
  {
    return getWorkflowGraphModel().removeEdge(edge);
  }

  public boolean removeStep(StepCell step)
  {
    getWorkflowGraphModel().removeStep(step);
    return true;
  }

  public boolean removeJoin(JoinCell join)
  {
    return getWorkflowGraphModel().removeJoin(join);
  }

  public boolean removeSplit(SplitCell split)
  {
    return getWorkflowGraphModel().removeSplit(split);
  }

  public void dragEnter(DropTargetDragEvent dtde)
  {
  }

  public void dragOver(DropTargetDragEvent dtde)
  {
  }

  public void dropActionChanged(DropTargetDragEvent dtde)
  {

  }

  public void drop(DropTargetDropEvent dtde)
  {
    try
    {
      //Ok, get the dropped object and try to figure out what it is.
      Transferable tr = dtde.getTransferable();
      DataFlavor[] flavors = tr.getTransferDataFlavors();
      for(int i = 0; i < flavors.length; i++)
      {
        if(flavors[i].isFlavorSerializedObjectType())
        {
          if(flavors[i].equals(DragData.WORKFLOW_FLAVOR))
          {
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            DragData o = (DragData)tr.getTransferData(flavors[i]);
            dtde.dropComplete(true);
            CellFactory.createCell(this.descriptor, this.getWorkflowGraphModel(), dtde.getLocation(), o);
            return;
          }
        }
      }
      dtde.rejectDrop();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      dtde.rejectDrop();
    }
  }

  public void dragExit(DropTargetEvent dte)
  {

  }

  public UndoManager getUndoManager()
  {
    return undoManager;
  }
}
