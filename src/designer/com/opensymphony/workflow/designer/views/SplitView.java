package com.opensymphony.workflow.designer.views;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class SplitView extends VertexView
{
  private static final SplitRenderer renderer = new SplitRenderer();

  public SplitView(Object cell)
  {
    super(cell);
  }

  // Returns the Renderer for this View
  public CellViewRenderer getRenderer()
  {
    return renderer;
  }
}
