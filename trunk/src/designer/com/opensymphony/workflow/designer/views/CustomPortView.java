package com.opensymphony.workflow.designer.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import com.opensymphony.workflow.designer.swing.ImageLoader;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 10:23:47 AM
 */
public class CustomPortView extends PortView
{
  private static final ImageIcon ICON = ImageLoader.getIcon("port.gif");
  private static final CellViewRenderer RENDERER = new CustomPortRenderer();
  private static final int WIDTH = ICON.getIconWidth();
  private static final int HEIGHT = ICON.getIconHeight();

  public CustomPortView(Object object, JGraph graph, CellMapper mapper)
  {
    super(object, graph, mapper);
  }

  /**
   * Returns the bounds for the port view.
   */
  public Rectangle getBounds()
  {
    Rectangle bounds = new Rectangle(getLocation(null));
    bounds.x = bounds.x - WIDTH / 2;
    bounds.width = WIDTH;
    bounds.height = HEIGHT;
    return bounds;
  }

  public Point getLocation(EdgeView edge)
  {
    Point p = super.getLocation(edge);
//    if(edge!=null && edge.getSource()==edge.getTarget())
//    {
//      int index = ((WorkflowPort)getCell()).getEdgeIndex((Edge)edge.getCell());
//      p.y = p.y - (5 * index);
//    }
    return p;
  }

  public CellViewRenderer getRenderer()
  {
    return RENDERER;
  }

  static class CustomPortRenderer extends PortRenderer
  {
    public void paint(Graphics g)
    {
      g.setColor(graph.getBackground());
      //g.setXORMode(graph.getBackground());
      if(preview)
      {
        Dimension d = getSize();
        g.setColor(java.awt.Color.red);
        g.drawRect(1, 1, d.width - 3, d.height - 3);
        g.drawRect(2, 2, d.width - 5, d.height - 5);
      }
      else
        ICON.paintIcon(graph, g, 0, 0);
    }

  }
}
