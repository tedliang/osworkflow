package com.opensymphony.workflow.designer;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.designer.layout.SugiyamaLayoutAlgorithm;
import com.opensymphony.workflow.designer.layout.LayoutAlgorithm;
import com.opensymphony.workflow.designer.views.*;

public class WorkflowGraph extends JGraph
{
  private Layout layout = new Layout();

  private WorkflowDescriptor descriptor;

  public WorkflowGraph(GraphModel model, WorkflowDescriptor descriptor, Layout layout, boolean doAutoLayout)
  {
    super(model);
    ToolTipManager.sharedInstance().registerComponent(this);
    this.layout = layout;
    if(descriptor != null)
    {
      this.descriptor = descriptor;
      addInitailAction();
      addSteps();
      addSplits();
      addJoins();
      getWorkflowGraphModel().insertResultConnections();
    }
    if(doAutoLayout)
    {
      autoLayout();
    }
    setSelectNewCells(true);
    setGridEnabled(true);
    setGridSize(6);
    setTolerance(2);
    setMarqueeHandler(new WorkflowMarqueeHandler());
    setCloneable(false);
    setPortsVisible(true);
  }

  protected PortView createPortView(Object object, CellMapper mapper)
  {
    return new CustomPortView(object, this, mapper);
  }

  public class WorkflowMarqueeHandler extends BasicMarqueeHandler
  {
    public void mouseReleased(MouseEvent e)
    {
      repaint();
      super.mouseReleased(e);
    }

    public PortView getSourcePortAt(Point point)
    {
      // Scale from Screen to Model
      Point tmp = fromScreen(new Point(point));
      // Find a Port View in Model Coordinates and Remember
      return getPortViewAt(tmp.x, tmp.y);
    }

    // Show Special Cursor if Over Port
    public void mouseMoved(MouseEvent e)
    {
      // Check Mode and Find Port
      if(e != null && getSourcePortAt(e.getPoint()) != null && !e.isConsumed())
      {
        // Set Cusor on Graph (Automatically Reset)
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Consume Event
        e.consume();
      }
      // Call Superclass
      super.mouseReleased(e);
    }
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

  public void addInitailAction()
  {
    List initialActionList = descriptor.getInitialActions();
    addInitialActionView(initialActionList);
  }

  private void addInitialActionView(List initialActionList)
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

  public void addSteps()
  {
    List stepsList = descriptor.getSteps();
    for(int i = 0; i < stepsList.size(); i++)
    {
      StepDescriptor step = (StepDescriptor)stepsList.get(i);
      addStepView(step);
    }
  }

  public void addSplits()
  {
    List splitsList = descriptor.getSplits();
    for(int i = 0; i < splitsList.size(); i++)
    {
      SplitDescriptor split = (SplitDescriptor)splitsList.get(i);
      addSplitView(split);

    }
  }

  public void addJoins()
  {
    List joinsList = descriptor.getJoins();
    for(int i = 0; i < joinsList.size(); i++)
    {
      JoinDescriptor join = (JoinDescriptor)joinsList.get(i);
      addJoinView(join);

    }
  }

  private void addJoinView(JoinDescriptor descriptor)
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

  private void addSplitView(SplitDescriptor descriptor)
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

  private void addStepView(StepDescriptor descriptor)
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

  public void addStepCell(StepCell step)
  {
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(step.toString());
      if(bounds != null)
      {
        step.getAttributes().put(GraphConstants.BOUNDS, bounds);
      }
    }
    getWorkflowGraphModel().insertStep(step, null, null, null);
  }

  public void addSplitCell(SplitCell split)
  {
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(split.toString());
      if(bounds != null)
      {
        split.getAttributes().put(GraphConstants.BOUNDS, bounds);
      }
    }
    getWorkflowGraphModel().insertSplit(split, null, null, null);
  }

  public void addJoinCell(JoinCell join)
  {
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(join.toString());
      if(bounds != null)
      {
        join.getAttributes().put(GraphConstants.BOUNDS, bounds);
      }
    }
    getWorkflowGraphModel().insertJoin(join, null, null, null);
  }

  private void addInitialActionsCell(InitialActionCell initialActionCell)
  {
    if(layout != null)
    {
      Rectangle bounds = layout.getBounds(initialActionCell.toString());
      if(bounds != null)
      {
        initialActionCell.getAttributes().put(GraphConstants.BOUNDS, bounds);
      }
    }
    getWorkflowGraphModel().insertInitialActions(null, initialActionCell, null, null, null);
  }
}

