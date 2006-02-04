package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.AbstractDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 10:27:26 AM
 */
public abstract class DetailPanel extends JPanel
{
  private AbstractDescriptor descriptor;
  private WorkflowGraphModel model;
  private WorkflowGraph graph;

  private boolean componentsInited = false;

  public AbstractDescriptor getDescriptor()
  {
    return descriptor;
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

  public WorkflowGraph getGraph()
  {
  	return graph;
  }

  public void setGraph(WorkflowGraph graph)
  {
  	this.graph = graph;
  }

  public final void setDescriptor(AbstractDescriptor descriptor)
  {
    if(!componentsInited)
    {
      initComponents();
      componentsInited = true;
    }
    this.descriptor = descriptor;
    setName(descriptor.getClass().getName());
    updateView();
  }
	
  protected abstract void initComponents();

  protected abstract void updateView();

  public String getTitle()
  {
    return ResourceManager.getString("details");
  }
}
