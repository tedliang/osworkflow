package com.opensymphony.workflow.designer;

import java.awt.*;
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
import com.opensymphony.workflow.designer.views.*;
import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.SplitDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

public class WorkflowGraph extends JGraph implements DropTargetListener
{
  private Layout layout = new Layout();
  private Point menuLocation = new Point();

  private WorkflowDescriptor descriptor;
  private JPopupMenu genericMenu;
  private JPopupMenu cellMenu;

  public WorkflowGraph(GraphModel model, WorkflowDescriptor descriptor, Layout layout, boolean doAutoLayout)
  {
    super(model);
    ToolTipManager.sharedInstance().registerComponent(this);
    this.layout = layout;
    setDescriptor(descriptor);
    if(doAutoLayout)
    {
      autoLayout();
    }
    setSelectNewCells(true);

    setGridEnabled(true);
    setGridColor(new Color(240, 240, 240));
    setGridSize(12);
    setTolerance(2);
    setGridVisible(true);
    setGridMode(JGraph.LINE_GRID_MODE);

    setBendable(true);
    setMarqueeHandler(new WorkflowMarqueeHandler(this));
    setCloneable(false);
    setPortsVisible(true);

    // one set of menu <==> one graph <==> one workflow descriptor
    genericMenu = new JPopupMenu();
    JMenu n = new JMenu("New");
    genericMenu.add(n);
    n.add(new CreateStep(descriptor, getWorkflowGraphModel(), menuLocation));
    //	n.add(new CreateInitialAction(descriptor, getWorkflowGraphModel(), menuLocation));
    n.add(new CreateJoin(descriptor, getWorkflowGraphModel(), menuLocation));
    n.add(new CreateSplit(descriptor, getWorkflowGraphModel(), menuLocation));

    cellMenu = new JPopupMenu();
    cellMenu.add(new Delete(descriptor, this, menuLocation));

    new DropTarget(this, this);
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

  protected PortView createPortView(Object object, CellMapper mapper)
  {
    return new CustomPortView(object, this, mapper);
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
    InitialActionCell initialActionCell = new InitialActionCell("Start");
    // Create Vertex Attributes
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(initialActionCell.getId());
      if(bounds != null)
      {
        GraphConstants.setBounds(initialActionCell.getAttributes(), bounds);
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
      Rectangle bounds = layout.getBounds(join.getId());
      if(bounds != null)
        join.getAttributes().put(GraphConstants.BOUNDS, bounds);
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
      Rectangle bounds = layout.getBounds(split.getId());
      if(bounds != null)
        split.getAttributes().put(GraphConstants.BOUNDS, bounds);
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
    StepCell step = new StepCell(descriptor);
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(step.getId());
      if(bounds != null)
      {
        step.getAttributes().put(GraphConstants.BOUNDS, bounds);
      }
      if(step.getChildCount() == 0)
      {
        WorkflowPort port = new WorkflowPort();
        step.add(port);
      }

    }
    else
    {
    }
    // Insert into Model
    getWorkflowGraphModel().insertStepCell(step, null, null, null);
  }

  public WorkflowGraphModel getWorkflowGraphModel()
  {
    return (WorkflowGraphModel)getModel();
  }

  /**
   * Overriding method as required by JGraph API,
   * In order to return right View object corresponding to Cell.
   */
  protected VertexView createVertexView(Object v, CellMapper cm)
  {
    if(v instanceof StepCell)
      return new StepView(v, this, cm);
    if(v instanceof SplitCell)
      return new SplitView(v, this, cm);
    if(v instanceof JoinCell)
      return new JoinView(v, this, cm);
    if(v instanceof InitialActionCell)
      return new InitialActionView(v, this, cm);
    // Else Call Superclass
    return super.createVertexView(v, cm);
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
          if(flavors[i].equals(DragData.scriptFlavor))
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

}
