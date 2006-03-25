package com.opensymphony.workflow.designer.views;

import java.awt.*;

import org.jgraph.graph.PortView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.CellView;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 10:23:47 AM
 */
public class OutsidePortView extends PortView
{
  private final CellViewRenderer RENDERER = new OutsidePortRenderer();

  public OutsidePortView(Object object)
  {
    super(object);
  }

  public CellViewRenderer getRenderer()
  {
    return RENDERER;
  }

  class OutsidePortRenderer extends PortRenderer
  {
    public void paint(Graphics g)
    {
      
      //TODO need to figure out how we can only paint these if we have focus,
//      CellView parent = view.getParentView();
      //or if a mouse is dragged into the containing cell
//      WorkflowPort port = (WorkflowPort)getCell();
//      if(port.getEdges().size() > 0)
        super.paint(g);
    }
  }
}
