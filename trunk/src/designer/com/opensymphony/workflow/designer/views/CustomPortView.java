package com.opensymphony.workflow.designer.views;

import java.awt.*;
import javax.swing.*;

import org.jgraph.graph.*;
import org.jgraph.JGraph;

/**
 * User: Hani Suleiman
 * Date: Oct 16, 2003
 * Time: 10:23:47 AM
 */
public class CustomPortView extends PortView
{
  private static final ImageIcon ICON = new ImageIcon(CustomPortView.class.getResource("/images/port.gif"));
  private static final CellViewRenderer RENDERER = new CustomPortRenderer();

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
    int width = ICON.getIconWidth();
    int height = ICON.getIconHeight();
    bounds.x = bounds.x - width / 2;
//    bounds.y = bounds.y - height / 2;
    bounds.width = width;
    bounds.height = height;
    return bounds;
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
        g.setColor(Color.red);
        g.drawRect(1, 1, d.width - 3, d.height - 3);
        g.drawRect(2, 2, d.width - 5, d.height - 5);
      }
      else
        ICON.paintIcon(graph, g, 0, 0);
    }

  }
}
