package com.opensymphony.workflow.designer.actions;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JOptionPane;

import org.jgraph.graph.GraphConstants;

import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.SplitCell;
import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.WorkflowGraphModel;
import com.opensymphony.workflow.designer.WorkflowPort;
import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.loader.DescriptorBuilder;
import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.SplitDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author Gulei
 */
public class CellFactory
{
  public static void createCell(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location, DragData script)
  {
    if(script.equals(DragData.JOIN))
    {
      createJoin(workflow, model, location);
    }
    else if(script.equals(DragData.SPLIT))
    {
      createSplit(workflow, model, location);
    }
    else if(script.equals(DragData.STEP))
    {
      createStep(workflow, model, location);
    }

  }

  public static void createStep(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    // create new step
    String name = JOptionPane.showInputDialog("Step Name?");
    if(name == null || name.trim().length() == 0) return;

    StepDescriptor step = DescriptorBuilder.createStep(name, Utils.getNextId());
    step.setParent(workflow);
    workflow.addStep(step);

    StepCell cell = new StepCell(step);
    WorkflowPort port = new WorkflowPort();
    cell.add(port);
    Rectangle bounds = (Rectangle)cell.getAttributes().get(GraphConstants.BOUNDS);
    bounds.x = location.x;
    bounds.y = location.y;
    model.insertStepCell(cell, null, null, null);

  }

  public static void createJoin(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    JoinDescriptor join = DescriptorBuilder.createJoin(Utils.getNextId());
    join.setParent(workflow);
    workflow.addJoin(join);

    JoinCell cell = new JoinCell(join);
    WorkflowPort port = new WorkflowPort();
    cell.add(port);
    Rectangle bounds = (Rectangle)cell.getAttributes().get(GraphConstants.BOUNDS);
    bounds.x = location.x;
    bounds.y = location.y;

    model.insertJoinCell(cell, null, null, null);

  }

  public static void createSplit(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    SplitDescriptor split = DescriptorBuilder.createSplit(Utils.getNextId());
    split.setParent(workflow);
    workflow.addSplit(split);

    SplitCell cell = new SplitCell(split);
    WorkflowPort port = new WorkflowPort();
    cell.add(port);
    Rectangle bounds = (Rectangle)cell.getAttributes().get(GraphConstants.BOUNDS);
    bounds.x = location.x;
    bounds.y = location.y;

    model.insertSplitCell(cell, null, null, null);

  }

}