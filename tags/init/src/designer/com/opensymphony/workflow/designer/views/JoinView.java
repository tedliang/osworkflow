/**
 * Created on Feb 13, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer.views;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellViewRenderer;
import com.jgraph.graph.VertexView;

public class JoinView extends VertexView
{
  private static final JoinRenderer renderer = new JoinRenderer();

  // Constructor for Superclass
  private Object cell;

  public JoinView(Object cell, JGraph graph, CellMapper mapper)
  {
    super(cell, graph, mapper);
    this.cell = cell;
  }

  // Returns the Renderer for this View
  public CellViewRenderer getRenderer()
  {
    return renderer;
  }
}
