/**
 * Created on Feb 13, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import java.awt.*;

import com.opensymphony.workflow.loader.JoinDescriptor;
import org.jgraph.graph.GraphConstants;

public class JoinCell extends WorkflowCell implements Keyable
{
  private JoinDescriptor descriptor;

  // Construct Cell for Userobject
  public JoinCell(JoinDescriptor userObject)
  {
    super("Join id " + userObject.getId());
    descriptor = userObject;
    id = descriptor.getId();
    GraphConstants.setBackground(attributes, Color.gray);
    // Set black border
  }

  public JoinCell(int id)
  {
    super("Join id " + id);
    this.id = id;
  }

  public JoinDescriptor getJoinDescriptor()
  {
    return descriptor;
  }

  public String getKey()
  {
    String myClassName = JoinCell.class.toString();
    return (myClassName + id);
  }
}
