package com.opensymphony.workflow.designer;

import java.awt.*;

import org.jgraph.graph.GraphConstants;

public class InitialActionCell extends WorkflowCell
{
  public InitialActionCell(String userObject)
  {
    super(userObject);
    GraphConstants.setBackground(attributes, Color.red.darker());
   // GraphConstants.setBorder(attributes, BorderFactory.createEmptyBorder());
  }

  public String getKey()
  {
    String myClassName = InitialActionCell.class.toString();
    return (myClassName);
  }
}
