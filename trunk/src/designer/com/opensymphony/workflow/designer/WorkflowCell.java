package com.opensymphony.workflow.designer;

import java.awt.*;

import javax.swing.*;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * @author Harsh Vijaywargiya Mar 9, 2003 1:04:57 PM
 * Description
 */
public class WorkflowCell extends DefaultGraphCell
{
  public static final Rectangle defaultBounds = new Rectangle(10, 10, 100, 30);
  protected int id;
  protected String name;

  /**
   * @param cellName
   */
  public WorkflowCell(Object cellName)
  {
    super(cellName);
    GraphConstants.setBounds(attributes, defaultBounds);
    GraphConstants.setOpaque(attributes, true);
    GraphConstants.setBackground(attributes, Color.blue.darker());
    GraphConstants.setForeground(attributes, Color.white);
    GraphConstants.setBorder(attributes, BorderFactory.createRaisedBevelBorder());
    GraphConstants.setEditable(attributes, false);
    GraphConstants.setVerticalAlignment(attributes, SwingConstants.TOP);
    //GraphConstants.setFont(attributes, GraphConstants.defaultFont.deriveFont(Font.BOLD, 12));
  }

  /**
   * Returns the mId.
   * @return int
   */
  public int getId()
  {
    return id;
  }

  /**
   * Returns the mName.
   * @return String
   */
  public String getName()
  {
    return name;
  }
}
