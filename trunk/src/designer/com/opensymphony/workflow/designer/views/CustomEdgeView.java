package com.opensymphony.workflow.designer.views;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.ResultDescriptor;
import org.jgraph.graph.*;

/**
 * User: Hani Suleiman
 * Date: Dec 2, 2003
 * Time: 2:22:44 PM
 */
public class CustomEdgeView extends EdgeView
{
  public CustomEdgeView(Object obj, WorkflowGraph graph, CellMapper cm)
  {
    super(obj, graph, cm);
  }

  public void setTarget(CellView targetView)
  {
    super.setTarget(targetView);
    if(targetView==null || targetView.getParentView()==null) return;
    WorkflowCell cell = (WorkflowCell)targetView.getParentView().getCell();
    ResultDescriptor d = ((ResultEdge)getCell()).getDescriptor();
    if(cell instanceof StepCell)
    {
      d.setStep(cell.getId());
    }
    else if(cell instanceof JoinCell)
    {
      d.setJoin(cell.getId());
    }
    else if(cell instanceof SplitCell)
    {
      d.setSplit(cell.getId());
    }
  }
}
