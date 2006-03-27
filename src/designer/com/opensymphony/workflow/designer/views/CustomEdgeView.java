package com.opensymphony.workflow.designer.views;

import java.awt.event.MouseEvent;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.ResultDescriptor;
import org.jgraph.graph.*;

/**
 * @author Hani Suleiman
 * Date: Dec 2, 2003 Time: 2:22:44 PM
 */
public class CustomEdgeView extends EdgeView
{
  public CustomEdgeView(Object obj)
  {
    super(obj);
    GraphConstants.setOpaque(this.attributes, true);
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

  public static class CustomEdgeHandle extends EdgeHandle
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
  }
}
