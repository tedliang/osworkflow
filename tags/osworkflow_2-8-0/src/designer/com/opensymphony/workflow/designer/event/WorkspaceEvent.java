package com.opensymphony.workflow.designer.event;

import java.util.EventObject;

import com.opensymphony.workflow.loader.WorkflowFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 20, 2003
 * Time: 11:52:02 PM
 */
public class WorkspaceEvent extends EventObject
{
  public static final int WORKSPACE_OPENED = 1;
  public static final int WORKSPACE_CLOSED = 2;
  private int id;
  private WorkflowFactory workspace;
  private boolean consumed;

  public WorkspaceEvent(Object source)
  {
    super(source);
  }

  public WorkspaceEvent(Object source, WorkflowFactory workspace, int id)
  {
    super(source);
    this.id = id;
    this.workspace = workspace;
  }

  public int getId()
  {
    return id;
  }

  public WorkflowFactory getWorkspace()
  {
    return workspace;
  }

  public boolean isConsumed()
  {
    return consumed;
  }

  public void consume()
  {
    consumed = true;
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer(getClass().getName());
    sb.append('[');
    sb.append("id=");
    sb.append(id==WORKSPACE_OPENED ? "WORKSPACE_OPENED " : "WORKSPACE_CLOSED ");
    sb.append("source=").append(source);
    sb.append(']');
    return sb.toString();
  }
}
