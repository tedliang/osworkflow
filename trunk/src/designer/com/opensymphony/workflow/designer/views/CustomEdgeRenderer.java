package com.opensymphony.workflow.designer.views;

import java.awt.*;
import java.awt.geom.Point2D;

import com.opensymphony.workflow.designer.ResultEdge;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.GraphConstants;

public class CustomEdgeRenderer extends EdgeRenderer
{
  public Color getForeground()
  {
    return GraphConstants.getForeground(((ResultEdge)view.getCell()).getAttributes());
  }

  protected void paintLabel(Graphics g, String label, Point2D p,
                            boolean mainLabel)
  {
    if(p != null && label != null && label.length() > 0)
    {
      System.out.println("paintLabel " + view.getCell() + "#" + view.hashCode() + " at " + p);
      int sw = metrics.stringWidth(label);
      int sh = metrics.getHeight();
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(p.getX(), p.getY());
      if(isOpaque() && mainLabel)
      {
        g.setColor(getBackground());
        g.fillRect((-sw / 2 - 1), (-sh / 2 - 1), sw + 2,
                   sh + 2);
      }
      if(borderColor != null && mainLabel)
      {
        g.setColor(borderColor);
        g.drawRect((-sw / 2 - 1), (-sh / 2 - 1), sw + 2,
                   sh + 2);

      }
      g.setColor(fontColor);
      int dx = -sw / 2;
      int dy = +sh / 4;
      g.drawString(label, dx, dy);
      g2.translate(-p.getX(), -p.getY());
    }
  }

}
