package com.opensymphony.workflow.designer.actions;

import java.awt.*;
import java.util.Map;
import java.util.Hashtable;
import javax.swing.*;

import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.designer.dnd.DragData;
import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;

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
    return createStep(workflow, null, model, location);
  }

  public static StepCell createStep(WorkflowDescriptor workflow, StepDescriptor step, WorkflowGraphModel model, Point location)
  {
    Map viewMap = new Hashtable();
    // create new step

    boolean exists = step != null;
    if(step == null)
    {
      String name = JOptionPane.showInputDialog(ResourceManager.getString("step.add.name"));
      if (name == null || name.trim().length() == 0)
        return null;

      step = DescriptorBuilder.createStep(name, Utils.getNextId(model.getContext()));
      step.setParent(workflow);
      workflow.addStep(step);
    }

    int index = 0;
    StepCell cell = new StepCell(step);
    DefaultPort port = new WorkflowPort(index++);
    cell.add(port);
    if(location != null)
    {
      Rectangle bounds = (Rectangle)cell.getAttributes().get(GraphConstants.BOUNDS);
      bounds.x = location.x;
      bounds.y = location.y;
    }
    int u = GraphConstants.PERMILLE;
    // Top Left
    AttributeMap map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(0, 0));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Top Center
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(u / 2, 0));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Top Right
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(u, 0));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Top Center
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(0, u / 2));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Middle Right
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(u, u / 2));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Bottom Left
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(0, u));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Bottom Center
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(u / 2, u));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);

    // Bottom Right
    map = new AttributeMap();
    GraphConstants.setOffset(map, new Point(u, u));
    port = new WorkflowPort(index++);
    cell.add(port);
    viewMap.put(port, map);
    model.insertStepCell(cell, viewMap, null, null);

    if(!exists)
    {
      WorkflowDesigner.INSTANCE.navigator().reloadSteps(workflow);
      WorkflowDesigner.INSTANCE.navigator().selectTreeNode(workflow, step);
    }
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
