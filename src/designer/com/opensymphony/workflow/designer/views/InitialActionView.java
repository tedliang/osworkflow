package com.opensymphony.workflow.designer.views;

import java.awt.Point;
import java.awt.Rectangle;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InitialActionView extends VertexView {
	// Constructor for Superclass
	private Object cell;
	private static final InitialActionRenderer renderer = new InitialActionRenderer();

	public InitialActionView(Object cell, JGraph graph, CellMapper mapper) {
		super(cell, graph, mapper);
		this.cell = cell;
	}

	public String toString() {
		return cell.toString();
	}

	/**
	 * Returns the intersection of the bounding rectangle and the
	 * straight line between the source and the specified point p.
	 * The specified point is expected not to intersect the bounds.
	 */
	public Point getPerimeterPoint(Point source, Point p) {
		// Compute relative bounds
		Rectangle r = getBounds();
		int x = r.x;
		int y = r.y;
		int a = (r.width + 1) / 2;
		int b = (r.height + 1) / 2;

		// Get center
		int xCenter =  x + a;
		int yCenter =  y + b;

		// Compute angle
		int dx = p.x - xCenter;
		int dy = p.y - yCenter;
		double t = Math.atan2(dy, dx);

		// Compute Perimeter Point
		int xout = xCenter + (int) (a * Math.cos(t)) - 1;
		int yout = yCenter + (int) (b * Math.sin(t)) - 1;

		// Return perimeter point
		return new Point(xout, yout);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

}
