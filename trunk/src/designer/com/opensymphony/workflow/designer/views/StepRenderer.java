/**
 * Created on Feb 5, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer.views;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.jgraph.graph.GraphConstants;
import com.jgraph.graph.VertexRenderer;

public class StepRenderer extends VertexRenderer  {
   public void paint(Graphics g) {
      int b = borderWidth;
      Graphics2D g2 = (Graphics2D) g;
      Dimension d = getSize();
      boolean tmp = selected;
      if (super.isOpaque()) {
         g.setColor(super.getBackground());
         int arcw = d.width / 5;
         int arch = d.height / 5;
         g.fillRoundRect(b - 1, b - 1, d.width - b, d.height - b, arcw, arch);
      }
      try {
         selected = false;
         super.paint(g);
      }
      finally {
         selected = tmp;
      }
      if (bordercolor != null) {
         g.setColor(bordercolor);
         g2.setStroke(new BasicStroke(b));
         int arcw = d.width / 5;
         int arch = d.height / 5;
         g.drawRoundRect(b - 1, b - 1, d.width - b, d.height - b, arcw, arch);

      }
      if (selected) {
         g2.setStroke(GraphConstants.SELECTION_STROKE);
         g.setColor(graph.getHighlightColor());
         int arcw = d.width / 5;
         int arch = d.height / 5;
         g.drawRoundRect(b - 1, b - 1, d.width - b, d.height - b, arcw, arch);
      }
   }

}
