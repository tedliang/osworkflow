package com.opensymphony.workflow.designer.views;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class SplitView extends VertexView
{
  private static final SplitRenderer renderer = new SplitRenderer();

  public SplitView(Object cell, JGraph graph, CellMapper mapper)
  {
    super(cell, graph, mapper);
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
