package com.opensymphony.workflow.designer.views;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellViewRenderer;
import com.jgraph.graph.VertexView;

public class SplitView extends VertexView
{
  private static final SplitRenderer renderer = new SplitRenderer();

  // Constructor for Superclass
  private Object cell;

  public SplitView(Object cell, JGraph graph, CellMapper mapper)
  {
    super(cell, graph, mapper);
    this.cell = cell;
  }

  // Returns the Renderer for this View
  public CellViewRenderer getRenderer()
  {
    return renderer;
  }

  public String toString()
  {
    return cell.toString();
  }
}
