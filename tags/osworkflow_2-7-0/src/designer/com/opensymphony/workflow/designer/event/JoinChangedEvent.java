/*
 * Created on 2003-11-22
 *
 * source is JoinCell
 * should know model? in whitch model
 */
package com.opensymphony.workflow.designer.event;

import java.util.EventObject;
import java.util.Map;

import com.opensymphony.workflow.designer.WorkflowGraphModel;

/**
 * @author Gulei
 */
public class JoinChangedEvent extends EventObject
{

  private WorkflowGraphModel model;
  private Map args;

  /**
   * @param source should be JoinCell
   */
  public JoinChangedEvent(Object source)
  {
    super(source);
  }

  public JoinChangedEvent(Object source, WorkflowGraphModel model)
  {
    super(source);
    this.model = model;
  }

  public Map getArgs()
  {
    return args;
  }

  public WorkflowGraphModel getModel()
  {
    return model;
  }

  public void setArgs(Map map)
  {
    args = map;
  }

  public void setModel(WorkflowGraphModel model)
  {
    this.model = model;
  }

}
