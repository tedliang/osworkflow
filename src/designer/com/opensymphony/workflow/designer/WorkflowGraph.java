package com.opensymphony.workflow.designer;

import java.util.*;
import java.util.List;
import java.awt.*;

import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.designer.layout.SugiyamaLayoutAlgorithm;
import com.opensymphony.workflow.designer.layout.LayoutAlgorithm;
import com.opensymphony.workflow.designer.views.*;
import com.opensymphony.workflow.designer.actions.CreateStep;
import com.opensymphony.workflow.designer.actions.Delete;

public class WorkflowGraph extends JGraph
{
  private Layout layout = new Layout();
  private Point menuLocation = new Point();

  private WorkflowDescriptor descriptor;
  private JPopupMenu menu;
  private Delete delete;

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
    //setGridEnabled(true);
    //setGridSize(6);
    //setTolerance(2);
    setBendable(true);
    setMarqueeHandler(new WorkflowMarqueeHandler(this));
    setCloneable(false);
    setPortsVisible(true);
    delete = new Delete(getWorkflowGraphModel());
    menu = new JPopupMenu();
    JMenu n = new JMenu("New");
    menu.add(n);
    n.add(new CreateStep(getWorkflowGraphModel(), menuLocation));
    menu.add(new JMenuItem(delete));
    delete.setEnabled(false);
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
      Rectangle bounds = layout.getBounds(initialActionCell.toString());
      if(bounds != null)
      {
        GraphConstants.setBounds(initialActionCell.getAttributes(), bounds);
      }
    }
    getWorkflowGraphModel().insertInitialActions(initialActionList, initialActionCell, null, null, null);
  }

  public void addJoinDescriptor(JoinDescriptor descriptor)
  {
    JoinCell join = new JoinCell(descriptor);
    // Create Vertex Attributes
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(join.toString());
      if(bounds != null)
        join.getAttributes().put(GraphConstants.BOUNDS, bounds);
    }
    getWorkflowGraphModel().insertJoinCell(join, null, null, null);
  }

  public void addSplitDescriptor(SplitDescriptor descriptor)
  {
    SplitCell split = new SplitCell(descriptor);
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(split.toString());
      if(bounds != null)
        split.getAttributes().put(GraphConstants.BOUNDS, bounds);
    }
    getWorkflowGraphModel().insertSplitCell(split, null, null, null);
  }

  public void addStepDescriptor(StepDescriptor descriptor)
  {
    StepCell step = new StepCell(descriptor);
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(step.toString());
      if(bounds != null)
        step.getAttributes().put(GraphConstants.BOUNDS, bounds);
    }
    // Insert into Model
    getWorkflowGraphModel().insertStepCell(step, null, null, null);
  }

  private WorkflowGraphModel getWorkflowGraphModel()
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

  public void showMenu(Object cell, int x, int y)
  {
    delete.setEnabled(cell!=null);
    menuLocation.x = x;
    menuLocation.y = y;
    menu.show(this, x, y);
  }
}

