package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.WorkflowGraph;
import com.opensymphony.workflow.designer.views.CustomEdgeView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;

public class ResultEdgeColor extends JMenuItem
{
  ResultEdgeColorHandler customHandler;

  class ResultEdgeColorHandler extends AbstractAction
  {
    private WorkflowGraph graph;
    private Point location;
    private Color cColor;

    ResultEdgeColorHandler(WorkflowGraph graph, Point location, Color color)
    {
      this.graph = graph;
      this.location = location;
      this.cColor = color;
    }

    public void actionPerformed(ActionEvent e)
    {
      Object cell = graph.getFirstCellForLocation(location.x, location.y);
      if(cell == null)
      {
        return;
      }
      else
      {
        CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
        if(view instanceof CustomEdgeView)
        {
          GraphConstants.setForeground(((ResultEdge)cell).getAttributes(), cColor);
          view.update();
          view.refresh(false);
          graph.getSelectionModel().setSelectionCell(view.getCell());
        }
      }
    }
  }

  public ResultEdgeColor(WorkflowGraph graph, Point location, Color color, String name)
  {
    super(name);
    setIcon(new ColorIcon(color));
    customHandler = new ResultEdgeColorHandler(graph, location, color);
    addActionListener(customHandler);
  }

  class ColorIcon implements Icon
  {
    private Color color;

    public ColorIcon(Color color)
    {
      this.color = color;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      g.setColor(color);
      g.fillRect(1, 2, 16, getHeight() - 5);
      g.setColor(Color.BLACK);
      g.drawRect(1, 2, 16, getHeight() - 5);
    }

    public int getIconWidth()
    {
      return 16;
    }

    public int getIconHeight()
    {
      return getHeight();
    }
  }
}
