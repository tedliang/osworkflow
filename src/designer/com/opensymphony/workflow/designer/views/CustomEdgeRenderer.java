package com.opensymphony.workflow.designer.views;

import java.awt.*;

import com.opensymphony.workflow.designer.ResultEdge;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.GraphConstants;

public class CustomEdgeRenderer extends EdgeRenderer
{
  public Color getForeground()
  {
    return GraphConstants.getForeground(((ResultEdge)view.getCell()).getAttributes());
  }

  public void paint(Graphics g)
  {
    Shape edgeShape = view.getShape();
    // Sideeffect: beginShape, lineShape, endShape
    if(edgeShape != null)
    {
      Graphics2D g2 = (Graphics2D)g;
      int c = BasicStroke.CAP_BUTT;
      int j = BasicStroke.JOIN_MITER;
      g2.setStroke(new BasicStroke(lineWidth, c, j));
      g2.setBackground(getForeground());
      g2.setColor(getForeground());
      translateGraphics(g);
      g.setColor(getForeground());
      if(view.beginShape != null)
      {
        if(beginFill)
          g2.fill(view.beginShape);
        g2.draw(view.beginShape);
      }
      if(view.endShape != null)
      {
        if(endFill)
          g2.fill(view.endShape);
        g2.draw(view.endShape);
      }
      if(lineDash != null) // Dash For Line Only
        g2.setStroke(new BasicStroke(lineWidth, c, j, 10.0f, lineDash, 0.0f));
      if(view.lineShape != null)
        g2.draw(view.lineShape);

      if(selected)
      { // Paint Selected
        g2.setStroke(GraphConstants.SELECTION_STROKE);
        g2.setColor(graph.getHighlightColor());
        if(view.beginShape != null)
          g2.draw(view.beginShape);
        if(view.lineShape != null)
          g2.draw(view.lineShape);
        if(view.endShape != null)
          g2.draw(view.endShape);
      }
      if(graph.getEditingCell() != view.getCell())
      {
        Object label = graph.convertValueToString(view);
        if(label != null)
        {
          g2.setStroke(new BasicStroke(1));
          g.setFont(getFont());
          paintLabel(g, label.toString(), new Point(0, 0), true);
        }
      }
    }
  }

}
