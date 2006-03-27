package com.opensymphony.workflow.designer.views;

import org.jgraph.graph.*;

public class StepView extends VertexView
{
  private static final StepRenderer renderer = new StepRenderer();

  public StepView(Object cell)
  {
    super(cell);
  }

  // Returns the Renderer for this View
  public CellViewRenderer getRenderer()
  {
    return renderer;
  }
}
