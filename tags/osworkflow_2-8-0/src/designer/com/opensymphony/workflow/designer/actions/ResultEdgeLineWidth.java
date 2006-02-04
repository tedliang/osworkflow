package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.WorkflowGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;

public class ResultEdgeLineWidth extends JMenuItem
{
  ResultEdgeLineWidthHandler customHandler;

  static class ResultEdgeLineWidthHandler extends AbstractAction
  {
    private WorkflowGraph graph;
    private Point location;
    private int width;

    ResultEdgeLineWidthHandler(WorkflowGraph graph, Point location, int lineWidth, String name)
    {
      super(name);
      this.graph = graph;
      this.location = location;
      this.width = lineWidth;
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
        CellView view = (graph.getGraphLayoutCache().getMapping(cell, false));
        if(graph.getModel().isEdge(cell))
        {
          GraphConstants.setLineWidth(((ResultEdge)cell).getAttributes(), width);
          view.update();
          view.refresh(graph.getModel(), graph.getGraphLayoutCache(), false);
          graph.getSelectionModel().setSelectionCell(view.getCell());
        }
      }
    }
  }

  public ResultEdgeLineWidth(WorkflowGraph graph, Point location, int lineWidth)
  {
    super(lineWidth + " pt");
    setIcon(new LineIcon(lineWidth));
    customHandler = new ResultEdgeLineWidthHandler(graph, location, lineWidth, this.getName());
    addActionListener(customHandler);
  }

  class LineIcon implements Icon
  {
    private int lineWidth;

    public LineIcon(int lineWidth)
    {
      this.lineWidth = lineWidth;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      g.setColor(Color.BLACK);
      g.fillRect(1, (getHeight() - lineWidth) / 2, 16, lineWidth);
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
