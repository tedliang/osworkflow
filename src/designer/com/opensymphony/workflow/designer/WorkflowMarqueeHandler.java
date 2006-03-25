package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.PortView;
import org.jgraph.graph.CellView;

/**
 * @author Hani Suleiman (hani@formicary.net) Date: Oct 25 2003 Time: 6:56:10 PM
 */
public class WorkflowMarqueeHandler extends BasicMarqueeHandler
{
  // Holds the Start and the Current Point
  protected Point2D start, current;

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
    port = graph.getPortViewAt(e.getX(), e.getY());
    // If Port Found and in ConnectMode (=Ports Visible)
    if(port != null && graph.isPortsVisible())
    {
      WorkflowPort workflowPort = (WorkflowPort)port.getCell();
      //int edgeCount = workflowPort.getEdgeCount();
      //if we have any edges on this port, then assume we're trying to move the edge, rather
      //than create a new one
      if(workflowPort.getIndex() == 0)
      {
        return true;
      }
    }
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
      g.drawLine((int)start.getX(), (int)start.getY(), (int)current.getX(), (int)current.getY());
  }

  // Use the Preview Flag to Draw a Highlighted Port
  protected void paintPort(Graphics g)
  {
    // If Current Port is Valid
    if(port != null)
    {
      // If Not Floating Port...
      //boolean isFloating = (GraphConstants.getOffset(port.getAttributes()) != null);
      // ...Then use Parent's Bounds
      //Rectangle2D r = isFloating ? port.getBounds() : port.getParentView().getBounds();
      Rectangle2D r = port.getBounds();
      // Scale from Model to Screen
      r = graph.toScreen(r);
      // Add Space For the Highlight Border
      Rectangle2D growRect = new Rectangle2D.Double(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
      //r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
      // Paint Port in Preview (=Highlight) Mode
      graph.getUI().paintCell(g, port, growRect, true);
    }
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
          if(cell instanceof WorkflowEdge)
          {
            graph.showEdgeMenu(e.getX(), e.getY());
          }
          else
          {
            graph.showDelete(e.getX(), e.getY());
          }
        }
      }

      // Else if in ConnectMode and Remembered Port is Valid
    }
    else if(port != null && !e.isConsumed() && graph.isPortsVisible())
    {
      // Remember Start Location
      start = graph.toScreen(port.getLocation());
      // Remember First Port
      firstPort = port;
      // Consume Event
      e.consume();
      readyToConnect = false;
    }
    else
    {
    // Call Superclass
      super.mousePressed(e);
    }
  }

  // Find Port under Mouse and Repaint Connector
  public void mouseDragged(MouseEvent e)
  {
    // If remembered Start Point is Valid
    if(start != null && !e.isConsumed())
    {
//      CellView nearestCell = graph.getTopmostViewAt(e.getX(), e.getY(), false, true);
//      if(nearestCell != null && nearestCell instanceof StepView)
//      {
//        System.out.println("in cell " + nearestCell.getClass() + " children " + nearestCell.getChildViews().length);
//      }
      // ready to connect
      readyToConnect = true;

      // Fetch Graphics from Graph
      Graphics g = graph.getGraphics();
      // Xor-Paint the old Connector (Hide old Connector)
      paintConnector(Color.black, graph.getBackground(), g);
      // Reset Remembered Port
      PortView newPort = graph.getPortViewAt(e.getX(), e.getY());
      if(port != newPort && port != null)
      {
        paintPort(g);
      }
      port = newPort;
      // If Port was found then Point to Port Location
      if(port != null)
        current = graph.toScreen(port.getLocation());
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
      WorkflowPort sourcePort = (WorkflowPort)firstPort.getCell();
      WorkflowCell source = (WorkflowCell)sourcePort.getParent();
      WorkflowPort targetPort = (WorkflowPort)port.getCell();
      WorkflowCell target = (WorkflowCell)targetPort.getParent();
      source.setSelectedPort(source.getIndex(sourcePort));
      target.setSelectedPort(target.getIndex(targetPort));
      ConnectHelper.connect(source, target, (WorkflowGraphModel)graph.getModel());
      //		System.out.println("connectable "+ ConnectHelper.connect(source, target, (WorkflowGraphModel)graph.getModel()));

      //				connect((Port) firstPort.getCell(), (Port) port.getCell());
      // Consume Event
      e.consume();
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
    PortView portView = graph.getPortViewAt(e.getX(), e.getY());
    if(portView != null && !e.isConsumed())
    {
      // Set Cusor on Graph (Automatically Reset)
      WorkflowPort workflowPort = (WorkflowPort)portView.getCell();
      if(workflowPort.getIndex() == 0)
      //
      //int edgeCount = workflowPort.getEdgeCount();
      //if(edgeCount == 0)
      {
        graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
        e.consume();
      }
    }
    // Call Superclass
    super.mouseReleased(e);
  }

  public CellView getCurrentCell()
  {
    return null;
  }
}
