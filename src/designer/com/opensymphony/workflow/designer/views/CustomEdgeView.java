package com.opensymphony.workflow.designer.views;

import java.awt.*;

import com.opensymphony.workflow.designer.WorkflowGraph;
import org.jgraph.graph.*;

/**
 * User: Hani Suleiman
 * Date: Dec 2, 2003
 * Time: 2:22:44 PM
 */
public class CustomEdgeView extends EdgeView
{
  public CustomEdgeView(Object obj, WorkflowGraph graph, CellMapper cm)
  {
    super(obj, graph, cm);
  }

  public CellHandle getHandle(GraphContext context)
  {
    return new CustomEdgeHandle(this, context);
  }

  public class CustomEdgeHandle extends EdgeView.EdgeHandle
  {
    public CustomEdgeHandle(EdgeView edge, GraphContext ctx)
    {
      super(edge, ctx);
    }

    protected boolean snap(boolean source, Point point)
    {
      boolean connect = graph.isConnectable() && isEdgeConnectable;
      Object port = graph.getPortForLocation(point.x, point.y);
      if(port != null && connect)
      {
        CellView portView = graph.getGraphLayoutCache().getMapping(port, false);
        if(GraphConstants.isConnectable(portView.getParentView().getAllAttributes()))
        {
          Object cell = edge.getCell();
          if(source && edge.getSource() != portView && getModel().acceptsSource(cell, port))
          {
            overlay(graph.getGraphics());
            edge.setSource(portView);
            edge.update();
            overlay(graph.getGraphics());
          }
          else if(!source && edge.getTarget() != portView && getModel().acceptsTarget(cell, port))
          {
            overlay(graph.getGraphics());
            edge.setTarget(portView);
            edge.update();
            overlay(graph.getGraphics());
          }
          return true;
        }
      }
      return false;
    }
  }
}
