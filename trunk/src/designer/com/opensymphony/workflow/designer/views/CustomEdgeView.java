package com.opensymphony.workflow.designer.views;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Map;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.ResultDescriptor;
import org.jgraph.graph.*;

/**
 * User: Hani Suleiman
 * Date: Dec 2, 2003
 * Time: 2:22:44 PM
 */
public class CustomEdgeView extends EdgeView
{
  public CustomEdgeView(Object obj)
  {
    super(obj);
    GraphConstants.setOpaque(this.attributes, true);
    // Overrides static default renderer
    //if(!(renderer instanceof CustomEdgeRenderer))
    //{
    //  renderer = new CustomEdgeRenderer();
    //}
  }

  /**
   * Returns a cell handle for the view.
   */
  public CellHandle getHandle(GraphContext context)
  {
    return new CustomEdgeHandle(this, context);
  }

  public void setTarget(CellView targetView)
  {
    super.setTarget(targetView);
    if(targetView == null || targetView.getParentView() == null) return;
    if(targetView == source)
    {
      ((ResultEdge)cell).setAutoroute();
    }
    WorkflowCell cell = (WorkflowCell)targetView.getParentView().getCell();
    ResultDescriptor d = ((ResultEdge)getCell()).getDescriptor();
    if(cell instanceof StepCell)
    {
      d.setStep(cell.getId());
    }
    else if(cell instanceof JoinCell)
    {
      d.setJoin(cell.getId());
    }
    else if(cell instanceof SplitCell)
    {
      d.setSplit(cell.getId());
    }
  }

  public class CustomEdgeHandle extends EdgeHandle
  {

    CustomEdgeHandle(EdgeView edge, GraphContext ctx)
    {
      super(edge, ctx);
    }

    /**
     * Returning true signifies a mouse event adds a new point to an edge.
     */
    public boolean isAddPointEvent(MouseEvent event)
    {
      return event.isShiftDown();
    }

    /**
     * Returning true signifies a mouse event removes a given point.
     */
    public boolean isRemovePointEvent(MouseEvent event)
    {
      return event.isShiftDown();
    }

    public void mouseReleased(MouseEvent e) {
      boolean clone = e.isControlDown() && graph.isCloneable();
      GraphModel model = graph.getModel();
      Object source = (edge.getSource() != null) ? edge.getSource()
          .getCell() : null;
      Object target = (edge.getTarget() != null) ? edge.getTarget()
          .getCell() : null;
      if (model.acceptsSource(edge.getCell(), source)
          && model.acceptsTarget(edge.getCell(), target)) {
        ConnectionSet cs = createConnectionSet(edge, edge.getCell(),
            clone);
        Map nested = GraphConstants.createAttributes(
            new CellView[] { edge }, null);
        if (clone) {
          Map cellMap = graph.cloneCells(graph.getDescendants(new Object[]{edge
              .getCell() }));
          nested = GraphConstants.replaceKeys(cellMap, nested);
          cs = cs.clone(cellMap);
          Object[] cells = cellMap.values().toArray();
          graph.getGraphLayoutCache().insert(cells, nested, cs, null, null);
        } else {
          graph.getGraphLayoutCache().edit(nested, cs, null, null);
        }
      } else {
        overlay(graph.getGraphics());
      }
      currentPoint = null;
      this.label = false;
      currentLabel = -1;
      currentIndex = -1;
      firstOverlayCall = true;
      e.consume();
    }

    public void overlay(Graphics g) {
      if (edge != null && !firstOverlayCall && edge.isLeaf()) {
        //g.setColor(graph.getBackground()); // JDK 1.3
        g.setColor(graph.getForeground());
        g.setXORMode(graph.getBackground().darker());
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(graph.getScale(), graph.getScale());
        graph.getUI().paintCell(g, edge, edge.getBounds(), true);
        g2.setTransform(oldTransform);
        if (isSourceEditing() && edge.getSource() != null)
          paintPort(g, edge.getSource());
        else if (isTargetEditing() && edge.getTarget() != null)
          paintPort(g, edge.getTarget());
      }
      firstOverlayCall = false;
    }
  }

}
