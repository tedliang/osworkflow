package com.opensymphony.workflow.designer;

import org.jgraph.graph.*;
import com.opensymphony.workflow.designer.views.*;

/**
 * @author hani Date: Dec 30, 2004 Time: 2:36:23 PM
 */
public class WorkflowCellViewFactory implements CellViewFactory
{
  public CellView createView(GraphModel model, Object cell)
  {
    if(model.isPort(cell))
    {
      return new CustomPortView(cell);
    }
    else if(model.isEdge(cell))
    {
      return new CustomEdgeView(cell);
    }
    else if(cell instanceof StepCell)
      return new StepView(cell);
    else if(cell instanceof SplitCell)
      return new SplitView(cell);
    else if(cell instanceof JoinCell)
      return new JoinView(cell);
    else if(cell instanceof InitialActionCell)
      return new InitialActionView(cell);
    else
      return new VertexView(cell);
  }

}
