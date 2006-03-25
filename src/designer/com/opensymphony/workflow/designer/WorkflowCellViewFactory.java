package com.opensymphony.workflow.designer;

import java.util.Map;
import java.util.HashMap;

import org.jgraph.graph.*;
import com.opensymphony.workflow.designer.views.*;

/**
 * @author hani Date: Dec 30, 2004 Time: 2:36:23 PM
 */
public class WorkflowCellViewFactory extends DefaultCellViewFactory
{
  private Map edgeMap = new HashMap();

  public CellView createView(GraphModel model, Object cell)
  {
    if(model.isPort(cell))
    {
      WorkflowPort port = (WorkflowPort)cell;
      if(port.getIndex() == 0)
        return new CustomPortView(cell);
      return new OutsidePortView(cell);
    }
    else if(model.isEdge(cell))
    {
      //return new CustomEdgeView(cell);
      CustomEdgeView view = (CustomEdgeView)edgeMap.get(cell);
      if(view == null)
      {
        view = new CustomEdgeView(cell);
        edgeMap.put(cell ,view);
      }
      return view;
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
      return super.createView(model, cell);
  }

}
