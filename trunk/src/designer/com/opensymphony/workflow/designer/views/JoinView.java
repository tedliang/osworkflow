package com.opensymphony.workflow.designer.views;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class JoinView extends VertexView
{
  private static final JoinRenderer renderer = new JoinRenderer();

  public JoinView(Object cell)
  {
    super(cell);
  }

  // Returns the Renderer for this View
  public CellViewRenderer getRenderer()
  {
    return renderer;
  }
}
