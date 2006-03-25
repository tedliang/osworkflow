package com.opensymphony.workflow.designer.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import javax.swing.*;

import org.jgraph.graph.*;

import com.opensymphony.workflow.designer.ResourceManager;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 10:23:47 AM
 */
public class CustomPortView extends PortView
{
  private static final ImageIcon ICON = ResourceManager.getIcon("port");
  private static final CellViewRenderer RENDERER = new CustomPortRenderer();
  private static final int WIDTH = ICON.getIconWidth();
  private static final int HEIGHT = ICON.getIconHeight();

  public CustomPortView(Object object)
  {
    super(object);
  }

  /**
   * Returns the bounds for the port view.
   */
  public Rectangle2D getBounds()
  {
    Point2D location = getLocation();
    return new Rectangle2D.Double(location.getX() - WIDTH / 2, location.getY(), WIDTH, HEIGHT);
  }

  public CellViewRenderer getRenderer()
  {
    return RENDERER;
  }

  static class CustomPortRenderer extends PortRenderer
  {
    public void paint(Graphics g)
    {
      g.setColor(getBackground());
      //g.setXORMode(graph.getBackground());
      if(preview)
      {
        Dimension d = getSize();
        g.setColor(java.awt.Color.red);
        g.drawRect(1, 1, d.width - 3, d.height - 3);
        g.drawRect(2, 2, d.width - 5, d.height - 5);
      }
      else
        ICON.paintIcon(this, g, 0, 0);
    }

  }
}
