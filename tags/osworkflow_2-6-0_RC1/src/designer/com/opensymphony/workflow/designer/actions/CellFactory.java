package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.GraphConstants;

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
    String name = JOptionPane.showInputDialog(ResourceManager.getString("step.add.name"));
    if(name == null || name.trim().length() == 0) return;

    StepDescriptor step = DescriptorBuilder.createStep(name, Utils.getNextId(model.getContext()));
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
    JoinDescriptor join = DescriptorBuilder.createJoin(Utils.getNextId(model.getContext()));
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
    SplitDescriptor split = DescriptorBuilder.createSplit(Utils.getNextId(model.getContext()));
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
