package com.opensymphony.workflow.designer.views;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class InitialActionView extends VertexView
{
  private Object cell;
  private static final InitialActionRenderer renderer = new InitialActionRenderer();

  public InitialActionView(Object cell)
  {
    super(cell);
    this.cell = cell;
  }

  public String toString()
  {
    return cell.toString();
  }

  /**
   * Returns the intersection of the bounding rectangle and the
   * straight line between the source and the specified point p.
   * The specified point is expected not to intersect the bounds.
   */
  public Point getPerimeterPoint(Point source, Point p)
  {
    // Compute relative bounds
    Rectangle2D r = getBounds();
    double a = (r.getWidth() + 1) / 2;
    double b = (r.getHeight() + 1) / 2;

    // Get center
    int xCenter = (int)r.getCenterX();
    int yCenter = (int)r.getCenterY();

    // Compute angle
    int dx = p.x - xCenter;
    int dy = p.y - yCenter;
    double t = Math.atan2(dy, dx);

    // Compute Perimeter Point
    int xout = xCenter + (int)(a * Math.cos(t)) - 1;
    int yout = yCenter + (int)(b * Math.sin(t)) - 1;

    // Return perimeter point
    return new Point(xout, yout);
  }

  public CellViewRenderer getRenderer()
  {
    return renderer;
  }

}
