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
import com.opensymphony.workflow.designer.actions.CreateInitialAction;

public class WorkflowGraph extends JGraph
{
  private Layout layout = new Layout();

  private WorkflowDescriptor descriptor;
  private JPopupMenu menu;

  public WorkflowGraph(GraphModel model, WorkflowDescriptor descriptor, Layout layout, boolean doAutoLayout)
  {
    super(model);
    ToolTipManager.sharedInstance().registerComponent(this);
    this.layout = layout;
    if(descriptor != null)
    {
      this.descriptor = descriptor;
      addInitialActions();
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
    //setGridEnabled(true);
    //setGridSize(6);
    //setTolerance(2);
    setMarqueeHandler(new WorkflowMarqueeHandler());
    setCloneable(false);
    setPortsVisible(true);
    menu = new JPopupMenu();
    JMenu n = new JMenu("New");
    menu.add(n);
    n.add(new CreateInitialAction(this));
  }

  protected PortView createPortView(Object object, CellMapper mapper)
  {
    System.out.println("createPortView " + object);
    return new CustomPortView(object, this, mapper);
  }

  public class WorkflowMarqueeHandler extends BasicMarqueeHandler
  {
    // Holds the Start and the Current Point
    protected Point start, current;

    // Holds the First and the Current Port
    protected PortView port, firstPort;

    // Override to Gain Control (for PopupMenu and ConnectMode)
    public boolean isForceMarqueeEvent(MouseEvent e)
    {
      // If Right Mouse Button we want to Display the PopupMenu
      if(SwingUtilities.isRightMouseButton(e))
      // Return Immediately
        return true;
      // Find and Remember Port
      port = getSourcePortAt(e.getPoint());
      // If Port Found and in ConnectMode (=Ports Visible)
      if(port != null && isPortsVisible())
        return true;
      // Else Call Superclass
      return super.isForceMarqueeEvent(e);
    }

    // Use Xor-Mode on Graphics to Paint Connector
    protected void paintConnector(Color fg, Color bg, Graphics g)
    {
      // Set Foreground
      g.setColor(fg);
      // Set Xor-Mode Color
      g.setXORMode(bg);
      // Highlight the Current Port
      paintPort(getGraphics());
      // If Valid First Port, Start and Current Point
      if(firstPort != null && start != null && current != null)
      // Then Draw A Line From Start to Current Point
        g.drawLine(start.x, start.y, current.x, current.y);
    }

    // Use the Preview Flag to Draw a Highlighted Port
    protected void paintPort(Graphics g)
    {
      // If Current Port is Valid
      if(port != null)
      {
        // If Not Floating Port...
        boolean o = (GraphConstants.getOffset(port.getAttributes()) != null);
        // ...Then use Parent's Bounds
        Rectangle r = (o) ? port.getBounds() : port.getParentView().getBounds();
        // Scale from Model to Screen
        r = toScreen(new Rectangle(r));
        // Add Space For the Highlight Border
        r.setBounds(r.x - 3, r.y - 3, r.width + 6, r.height + 6);
        // Paint Port in Preview (=Highlight) Mode
        getUI().paintCell(g, port, r, true);
      }
    }

    public PortView getSourcePortAt(Point point)
    {
      // Scale from Screen to Model
      Point tmp = fromScreen(new Point(point));
      // Find a Port View in Model Coordinates and Remember
      return getPortViewAt(tmp.x, tmp.y);
    }

    // Find a Cell at point and Return its first Port as a PortView
    protected PortView getTargetPortAt(Point point)
    {
      // Find Cell at point (No scaling needed here)
      Object cell = getFirstCellForLocation(point.x, point.y);
      // Loop Children to find PortView
      for(int i = 0; i < getModel().getChildCount(cell); i++)
      {
        // Get Child from Model
        Object tmp = getModel().getChild(cell, i);
        // Get View for Child using the Graph's View as a Cell Mapper
        tmp = getGraphLayoutCache().getMapping(tmp, false);
        // If Child View is a Port View and not equal to First Port
        if(tmp instanceof PortView && tmp != firstPort)
        // Return as PortView
          return (PortView)tmp;
      }
      // No Port View found
      return getSourcePortAt(point);
    }

// Display PopupMenu or Remember Start Location and First Port
    public void mousePressed(final MouseEvent e)
    {
      // If Right Mouse Button
      if(SwingUtilities.isRightMouseButton(e))
      {
        // Scale From Screen to Model
        Point loc = fromScreen(e.getPoint());
        // Find Cell in Model Coordinates
        Object cell = getFirstCellForLocation(loc.x, loc.y);
        // Create PopupMenu for the Cell
        menu.show(WorkflowGraph.this, e.getX(), e.getY());
//				JPopupMenu menu = createPopupMenu(e.getPoint(), cell);
//				menu.show(graph, e.getX(), e.getY());

        // Else if in ConnectMode and Remembered Port is Valid
      }
      else if(port != null && !e.isConsumed() && isPortsVisible())
      {
        // Remember Start Location
        start = toScreen(port.getLocation(null));
        // Remember First Port
        firstPort = port;
        // Consume Event
        e.consume();
      }
      else
      // Call Superclass
        super.mousePressed(e);
    }

    // Find Port under Mouse and Repaint Connector
    public void mouseDragged(MouseEvent e)
    {
      // If remembered Start Point is Valid
      if(start != null && !e.isConsumed())
      {
        // Fetch Graphics from Graph
        Graphics g = getGraphics();
        // Xor-Paint the old Connector (Hide old Connector)
        paintConnector(Color.black, getBackground(), g);
        // Reset Remembered Port
        PortView newPort = getTargetPortAt(e.getPoint());
        if(port!=newPort && port!=null)
        {
          paintPort(g);
        }
        port = newPort;
        // If Port was found then Point to Port Location
        if(port != null)
          current = toScreen(port.getLocation(null));
        // Else If no Port was found then Point to Mouse Location
        else
          current = snap(e.getPoint());
        // Xor-Paint the new Connector
        paintConnector(getBackground(), Color.black, g);
        // Consume Event
        e.consume();
      }
      // Call Superclass
      super.mouseDragged(e);
    }

    // Connect the First Port and the Current Port in the Graph or Repaint
    public void mouseReleased(MouseEvent e)
    {
      // If Valid Event, Current and First Port
      if(e != null && !e.isConsumed() && port != null && firstPort != null && firstPort != port)
      {
        // Then Establish Connection
        System.out.println("connect " + firstPort.getCell() + "->" + port.getCell());
//				connect((Port) firstPort.getCell(), (Port) port.getCell());
        // Consume Event
        e.consume();
        // Else Repaint the Graph
      }
      else
        repaint();
      // Reset Global Vars
      firstPort = port = null;
      start = current = null;
      // Call Superclass
      super.mouseReleased(e);
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

  public void addInitialActions()
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

  public void addInitialActionsCell(InitialActionCell initialActionCell)
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

