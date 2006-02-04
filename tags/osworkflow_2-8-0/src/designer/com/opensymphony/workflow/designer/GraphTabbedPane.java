package com.opensymphony.workflow.designer;

import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

/**
 * User: Hani Suleiman
 * Date: Jan 9, 2004
 * Time: 1:22:46 PM
 */
public class GraphTabbedPane extends JTabbedPane
{
  private List graphs = new ArrayList();

  public GraphTabbedPane()
  {
    // register a change listener
    addChangeListener(new ChangeListener()
    {
      // this method is called whenever the selected tab changes
      public void stateChanged(ChangeEvent evt)
      {
        WorkflowDesigner.INSTANCE.showSelectedCellDetails();
      }
    });
  }

  public WorkflowGraph getCurrentGraph()
  {
    int index = getSelectedIndex();
    if(index == -1 || index >= graphs.size()) return null;
    return (WorkflowGraph)graphs.get(index);
  }

  public boolean selectWorkflow(String workflowName)
  {
    for(int i = 0; i < getTabCount(); i++)
    {
      String name = getTitleAt(i);
      if(name.equals(workflowName))
      {
        setSelectedIndex(i);
        getCurrentGraph().getSelectionModel().clearSelection();
        return true;
      }
    }
    return false;
  }

  public void addGraph(WorkflowGraph graph)
  {
    graphs.add(graph);
    add(graph.getName(), new JScrollPane(graph));
    setSelectedIndex(getComponentCount() - 1);
  }

  public void removeAll()
  {
    super.removeAll();
    graphs.clear();
  }

  public WorkflowGraph[] getGraphs()
  {
    WorkflowGraph[] g = new WorkflowGraph[graphs.size()];
    graphs.toArray(g);
    return g;
  }

  public WorkflowGraph getGraph(String workflowName)
  {
    for(int i = 0; i < graphs.size(); i++)
    {
      WorkflowGraph graph = (WorkflowGraph)graphs.get(i);
      if(graph.getName().equals(workflowName))
      {
        return graph;
      }
    }
    return null;
  }

  public void renameGraph(String name, String newName)
  {
    for(int i = 0; i < graphs.size(); i++)
    {
      WorkflowGraph graph = (WorkflowGraph)graphs.get(i);
      if(graph.getName().equals(name))
      {
        graph.setName(newName);
        setTitleAt(i, newName);
        return;
      }
    }
  }

  public void removeGraph(String name)
  {
    for(int i = 0; i < graphs.size(); i++)
    {
      WorkflowGraph graph = (WorkflowGraph)graphs.get(i);
      if(graph.getName().equals(name))
      {
        removeTabAt(i);
        graphs.remove(i);
        revalidate();
        return;
      }
    }
  }

  public void removeGraph(WorkflowGraph graph)
  {
    for(int i = 0; i < graphs.size(); i++)
    {
      WorkflowGraph g = (WorkflowGraph)graphs.get(i);
      if(graph == g)
      {
        removeTabAt(i);
        graphs.remove(i);
        revalidate();
        return;
      }
    }
  }
}
