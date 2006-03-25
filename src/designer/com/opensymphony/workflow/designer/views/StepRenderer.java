package com.opensymphony.workflow.designer.views;

import java.awt.*;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;

public class StepRenderer extends VertexRenderer
{
  public void paint(Graphics g)
  {
    int b = borderWidth;
    Graphics2D g2 = (Graphics2D)g;
    Dimension d = getSize();
    boolean tmp = selected;
    int roundRectArc = StepRenderer.getArcSize(d.width - b, d.height - b);
    if(super.isOpaque())
    {
      g.setColor(super.getBackground());
      if(gradientColor != null && !preview)
      {
        setOpaque(false);
        g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(),
                                      getHeight(), gradientColor, true));
      }
      g.fillRoundRect(b - 1,
                      b - 1,
                      d.width - b,
                      d.height - b,
                      roundRectArc,
                      roundRectArc);
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
      g.drawRoundRect(b - 1,
                      b - 1,
                      d.width - b,
                      d.height - b,
                      roundRectArc,
                      roundRectArc);
    }
    if(selected)
    {
      g2.setStroke(GraphConstants.SELECTION_STROKE);
      g.setColor(highlightColor);
      g.drawRoundRect(b - 1,
                      b - 1,
                      d.width - b,
                      d.height - b,
                      roundRectArc,
                      roundRectArc);
    }
  }

  /**
   * getArcSize calculates an appropriate arc for the corners of the rectangle for boundary size cases of width and
   * height
   */
  public static int getArcSize(int width, int height)
  {
    int arcSize;

    // The arc width of a activity rectangle is 1/5th of the larger
    // of the two of the dimensions passed in, but at most 1/2
    // of the smaller of the two. 1/5 because it looks nice and 1/2
    // so the arc can complete in the given dimension

    if(width <= height)
    {
      arcSize = height / 5;
      if(arcSize > (width / 2))
      {
        arcSize = width / 2;
      }
    }
    else
    {
      arcSize = width / 5;
      if(arcSize > (height / 2))
      {
        arcSize = height / 2;
      }
    }

    return arcSize;
  }
}
