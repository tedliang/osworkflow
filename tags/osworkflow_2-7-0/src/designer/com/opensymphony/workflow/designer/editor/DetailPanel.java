package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.opensymphony.workflow.designer.*;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 10:27:26 AM
 */
public abstract class DetailPanel extends JPanel
{
  private WorkflowCell cell;
  private WorkflowEdge edge;
  private WorkflowGraphModel model;

  private boolean componentsInited = false;

  public WorkflowCell getCell()
  {
    return cell;
  }

  public WorkflowEdge getEdge()
  {
    return edge;
  }

  protected void viewClosed()
  {
  }

  public final void closeView()
  {
    viewClosed();
  }

  public WorkflowGraphModel getModel()
  {
    return model;
  }

  public void setModel(WorkflowGraphModel model)
  {
    this.model = model;
  }

  public final void setCell(WorkflowCell cell)
  {
    if(!componentsInited)
    {
      initComponents();
      componentsInited = true;
    }
    this.cell = cell;
    setName(cell.getClass().getName());
    updateView();
  }

  protected abstract void initComponents();

  protected abstract void updateView();

  public String getTitle()
  {
    return ResourceManager.getString("details");
  }

  public void setEdge(WorkflowEdge edge)
  {
    if(!componentsInited)
    {
      initComponents();
      componentsInited = true;
    }
    componentsInited = true;
    this.edge = edge;
    setName(edge.getClass().getName());
    updateView();
  }
}
