package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowGraph;
import com.opensymphony.workflow.designer.ResourceManager;

public class SetGridSize extends JMenuItem
{
  SetGridSizeWidthHandler customHandler;

  static class SetGridSizeWidthHandler extends AbstractAction
  {
    private WorkflowGraph graph;
    private int iSize;

    SetGridSizeWidthHandler(WorkflowGraph graph, int gridSize, String name)
    {
      super(name);
      this.graph = graph;
      this.iSize = gridSize;
    }

    public void actionPerformed(ActionEvent e)
    {
      graph.setGridVisible(iSize > 1);
      graph.setGridSize(iSize);
    }
  }

  public SetGridSize(WorkflowGraph graph, int gridSize)
  {
    super(gridSize > 1 ? gridSize + " pt" : ResourceManager.getString("grid.disable"));
    setIcon(new GridIcon(gridSize));
    customHandler = new SetGridSizeWidthHandler(graph, gridSize, this.getName());
    addActionListener(customHandler);
  }

  class GridIcon implements Icon
  {
    private int gridSize;

    public GridIcon(int gridSize)
    {
      this.gridSize = gridSize;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      g.setColor(Color.WHITE);
      g.fillRect(1, 1, getIconWidth(), getIconHeight() - 2);
      int gridStep = 2;
      g.setColor(new Color(220, 220, 220));
      if(gridSize > 1)
      {
        while(gridStep < getIconWidth())
        {
          g.drawLine(gridStep, 1, gridStep, getIconHeight() - 2);
          g.drawLine(1, gridStep, getIconWidth(), gridStep);
          gridStep += gridSize;
        }
      }
      g.setColor(Color.GRAY);
      g.drawRect(1, 1, getIconWidth(), getIconHeight() - 2);
    }

    public int getIconWidth()
    {
      return 20;
    }

    public int getIconHeight()
    {
      return getHeight();
    }
  }

}
