package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.PortView;
import org.jgraph.graph.GraphConstants;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Oct 25 2003
 * Time: 6:56:10 PM
 */
public class WorkflowMarqueeHandler extends BasicMarqueeHandler
{
  // Holds the Start and the Current Point
  protected Point start, current;

  // Holds the First and the Current Port
  protected PortView port, firstPort;
  private WorkflowGraph graph;

  //
  protected boolean readyToConnect = false;

  public WorkflowMarqueeHandler(WorkflowGraph graph)
  {
    this.graph = graph;
  }

  // Override to Gain Control (for PopupMenu and ConnectMode)
  public boolean isForceMarqueeEvent(MouseEvent e)
  {
    // If Right Mouse Button we want to Display the PopupMenu
    if(SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger())
    // Return Immediately
      return true;
    // Find and Remember Port
    port = getSourcePortAt(e.getPoint());
    // If Port Found and in ConnectMode (=Ports Visible)
    if(port != null && graph.isPortsVisible())
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
    paintPort(graph.getGraphics());
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
      r = graph.toScreen(new Rectangle(r));
      // Add Space For the Highlight Border
      r.setBounds(r.x - 3, r.y - 3, r.width + 6, r.height + 6);
      // Paint Port in Preview (=Highlight) Mode
      graph.getUI().paintCell(g, port, r, true);
    }
  }

  public PortView getSourcePortAt(Point point)
  {
    // Scale from Screen to Model
    Point tmp = graph.fromScreen(new Point(point));
    // Find a Port View in Model Coordinates and Remember
    return graph.getPortViewAt(tmp.x, tmp.y);
  }

  // Find a Cell at point and Return its first Port as a PortView
  protected PortView getTargetPortAt(Point point)
  {
    // Find Cell at point (No scaling needed here)
    Object cell = graph.getFirstCellForLocation(point.x, point.y);
    // Loop Children to find PortView
    for(int i = 0; i < graph.getModel().getChildCount(cell); i++)
    {
      // Get Child from Model
      Object tmp = graph.getModel().getChild(cell, i);
      // Get View for Child using the Graph's View as a Cell Mapper
      tmp = graph.getGraphLayoutCache().getMapping(tmp, false);
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
    if(SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger())
//    if(e.isPopupTrigger())
    {
      // Scale From Screen to Model
      //Point loc = fromScreen(e.getPoint());
      // Find Cell in Model Coordinates
      Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
      // Create PopupMenu for the Cell
      if(cell == null)
      {
        graph.showMenu(e.getX(), e.getY());
      }
      else
      {
        if(!(cell instanceof InitialActionCell))
        {
          //      		System.out.println(cell);
          graph.showDelete(e.getX(), e.getY());
        }
      }

      // Else if in ConnectMode and Remembered Port is Valid
    }
    else if(port != null && !e.isConsumed() && graph.isPortsVisible())
    {
      // Remember Start Location
      start = graph.toScreen(port.getLocation(null));
      // Remember First Port
      firstPort = port;
      // Consume Event
      e.consume();
      readyToConnect = false;
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
      // ready to connect
      readyToConnect = true;

      // Fetch Graphics from Graph
      Graphics g = graph.getGraphics();
      // Xor-Paint the old Connector (Hide old Connector)
      paintConnector(Color.black, graph.getBackground(), g);
      // Reset Remembered Port
      PortView newPort = getTargetPortAt(e.getPoint());
      if(port != newPort && port != null)
      {
        paintPort(g);
      }
      port = newPort;
      // If Port was found then Point to Port Location
      if(port != null)
        current = graph.toScreen(port.getLocation(null));
      // Else If no Port was found then Point to Mouse Location
      else
        current = graph.snap(e.getPoint());
      // Xor-Paint the new Connector
      paintConnector(graph.getBackground(), Color.black, g);
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
    // first connect first is OK
    if(e != null && !e.isConsumed() && port != null && firstPort != null && readyToConnect)
    {
      readyToConnect = false;
      // Then Establish Connection
      WorkflowCell source = (WorkflowCell)((WorkflowPort)firstPort.getCell()).getParent();
      WorkflowCell target = (WorkflowCell)((WorkflowPort)port.getCell()).getParent();
      //      System.out.println("connect " + source + "->" + target);
      ConnectHelper.connect(source, target, (WorkflowGraphModel)graph.getModel());
      //		System.out.println("connectable "+ ConnectHelper.connect(source, target, (WorkflowGraphModel)graph.getModel()));

      //				connect((Port) firstPort.getCell(), (Port) port.getCell());
      // Consume Event
      e.consume();
      // Else Repaint the Graph
    }
    graph.repaint();
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
      graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
      // Consume Event
      e.consume();
    }
    // Call Superclass
    super.mouseReleased(e);
  }
}
