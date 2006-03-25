package com.opensymphony.workflow.designer.views;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;

public class JoinRenderer extends VertexRenderer
{
  public void paint(Graphics g)
  {
    int b = borderWidth;
    Graphics2D g2 = (Graphics2D)g;
    Dimension d = getSize();
    boolean tmp = selected;

    //       g.setColor(Color.lightGray);

    if(super.isOpaque())
    {
      g.setColor(super.getBackground());
      g.fillRect(b - 1, b - 1, d.width - b, d.height - b);
    }
    try
    {
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
      //         g.drawRoundRect(b - 1, b - 1, d.width - b, d.height - b, arcw, arch);
      g.drawRect(b - 1, b - 1, d.width - b, d.height - b);

    }
    if(selected)
    {
      g2.setStroke(GraphConstants.SELECTION_STROKE);
      g.setColor(highlightColor);
      g.drawRect(b - 1, b - 1, d.width - b, d.height - b);
    }
  }

}
