package com.opensymphony.workflow.designer.views;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import com.opensymphony.workflow.designer.StepCell;

/**
 * @author apatel
 */
public class StepView extends VertexView
{
  private static final StepRenderer renderer = new StepRenderer();

  public StepView(Object cell, JGraph graph, CellMapper mapper)
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
