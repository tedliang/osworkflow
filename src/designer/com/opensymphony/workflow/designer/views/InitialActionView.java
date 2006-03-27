package com.opensymphony.workflow.designer.views;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class InitialActionView extends VertexView
{
  private static final InitialActionRenderer renderer = new InitialActionRenderer();

  public InitialActionView(Object cell)
  {
    super(cell);
  }

  public CellViewRenderer getRenderer()
  {
    return renderer;
  }

}
