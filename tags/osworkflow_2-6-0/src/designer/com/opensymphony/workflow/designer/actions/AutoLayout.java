package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.opensymphony.workflow.designer.WorkflowGraph;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 8:25:28 PM
 */
public class AutoLayout extends AbstractAction implements WorkspaceListener
{
  private WorkflowGraph graph;

  /**
   * Create an auto layout action.
   * This action calls an auto layout algorithm on the specified graph.
   * @param graph the graph to autolayout. If the graph is null, then the currently
   * showing graph has auto layout applied to it.
   */
  public AutoLayout(WorkflowGraph graph)
  {
    this.graph = graph;
  }

  public void actionPerformed(ActionEvent e)
  {
    if(graph==null)
    {
      WorkflowGraph selected = WorkflowDesigner.INSTANCE.getCurrentGraph();
      if(selected!=null)
        selected.autoLayout();
    }
    else
    {
      graph.autoLayout();
    }
  }

  public void workspaceChanged(WorkspaceEvent event)
  {
    if(event.getId()==WorkspaceEvent.WORKSPACE_OPENED)
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }
}
