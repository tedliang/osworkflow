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
  public static WorkflowCell createCell(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location, DragData data)
  {
    if(data.equals(DragData.JOIN))
    {
      return createJoin(workflow, model, location);
    }
    else if(data.equals(DragData.SPLIT))
    {
      return createSplit(workflow, model, location);
    }
    else if(data.equals(DragData.STEP))
    {
      return createStep(workflow, model, location);
    }
    return null;
  }

  public static StepCell createStep(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
  {
    // create new step
    String name = JOptionPane.showInputDialog(ResourceManager.getString("step.add.name"));
    if (name == null || name.trim().length() == 0) 
    	return null;

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
    
		WorkflowDesigner.INSTANCE.navigator().reloadSteps(workflow);
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, step);
    
    return cell;
  }

  public static JoinCell createJoin(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
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
    
		WorkflowDesigner.INSTANCE.navigator().reloadJoins(workflow);
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, join);
    
    return cell;
  }

  public static SplitCell createSplit(WorkflowDescriptor workflow, WorkflowGraphModel model, Point location)
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

		WorkflowDesigner.INSTANCE.navigator().reloadSplits(workflow);
		WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, split);	
    
    return cell;
  }
}
