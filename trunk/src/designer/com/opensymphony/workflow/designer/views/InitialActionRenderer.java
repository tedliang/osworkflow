package com.opensymphony.workflow.designer.views;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;

public class InitialActionRenderer extends VertexRenderer
{
  public void paint(Graphics g)
  {
    int b = borderWidth;
    Graphics2D g2 = (Graphics2D)g;
    Dimension d = getSize();
    boolean tmp = selected;
    if(super.isOpaque())
    {
      g.setColor(super.getBackground());
      g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
    }
    try
    {
      setBorder(null);
      setOpaque(false);
      selected = false;
      super.paint(g);
    }
    finally
    {
      selected = tmp;
    }
    if(bordercolor != null)
    {
      g.setColor(bordercolor);
      g2.setStroke(new BasicStroke(b));
      g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
    }
    if(selected)
    {
      g2.setStroke(GraphConstants.SELECTION_STROKE);
      g.setColor(highlightColor);
      g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
    }
  }
}
