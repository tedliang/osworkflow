package com.opensymphony.workflow.designer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import org.jgraph.graph.DefaultGraphCell;

public class RelationshipsNavigator extends JTree implements TreeSelectionListener, TreeModelListener
{
  private WorkflowDesigner designer;
  private WorkflowGraph currentGraph = null;
  private DefaultGraphCell currentCell = null;
  private DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

  public RelationshipsNavigator(WorkflowDesigner designer)
  {
    super(new DefaultTreeModel(new DefaultMutableTreeNode(ResourceManager.getString("relationships"))));
    setRootVisible(false);
    this.designer = designer;
    addTreeSelectionListener(this);
    //setEditable(true); TODO: Handle renaming of actions and results
    setEditable(false);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setShowsRootHandles(true);
    getModel().addTreeModelListener(this);
    cellRenderer.setClosedIcon(ResourceManager.getIcon("action"));
    cellRenderer.setOpenIcon(ResourceManager.getIcon("action"));
    cellRenderer.setLeafIcon(ResourceManager.getIcon("result"));
    setCellRenderer(cellRenderer);
  }

  private void clearRelationships(String rootName)
  {
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);
    setRootVisible(true);
    model.setRoot(root);
    model.reload(root);
  }

  public void showRelationships(Object node, WorkflowGraph graph)
  {
    String rootName;

    if(node instanceof DefaultGraphCell)
    {
      currentGraph = graph;
      currentCell = (DefaultGraphCell)node;
      if((node instanceof WorkflowCell) && (!(node instanceof InitialActionCell)))
      {
        rootName = ((WorkflowCell)node).getName();
      }
      else
      {
        rootName = ((DefaultGraphCell)node).toString();
      }
      clearRelationships(rootName);
    }
    if(node instanceof StepCell)
    {
      showStepRelationships((StepCell)node);
    }
    // TODO: handle other cell types
  }

  public void showStepRelationships(StepCell cell)
  {
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
    StepDescriptor stepDescriptor = cell.getDescriptor();
    Object[] cells = new Object[]{cell};
    java.util.Set edgeSet = WorkflowGraphModel.getEdges(((WorkflowGraphModel)currentGraph.getModel()), cells);
    for(int i = 0; i < stepDescriptor.getActions().size(); i++)
    {
      ActionDescriptor action = (ActionDescriptor)(stepDescriptor.getActions().get(i));
      DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action.getName());
      model.insertNodeInto(actionNode, root, root.getChildCount());
      java.util.Iterator edges = edgeSet.iterator();
      while(edges.hasNext())
      {
        ResultEdge edge = (ResultEdge)edges.next();
        if(edge.getDescriptor().getParent() == action)
        {
          model.insertNodeInto(new DefaultMutableTreeNode(edge), actionNode, actionNode.getChildCount());
        }
      }
    }
    if(stepDescriptor.getActions().size() > 0)
    {
      setRootVisible(false);
    }
    model.reload(root);
  }

  public void valueChanged(TreeSelectionEvent e)
  {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
    if(node == null || node.equals(getModel().getRoot()))
      return;

    if(node.isLeaf())
    {
      if(node.getUserObject() instanceof DefaultGraphCell)
      {
        designer.showDetails(node.getUserObject());
      }
    }
  }

  public DefaultGraphCell getCell()
  {
    return currentCell;
  }

  public void treeNodesChanged(TreeModelEvent e)
  {
  }

  public void treeNodesInserted(TreeModelEvent e)
  {
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  public void treeStructureChanged(TreeModelEvent e)
  {
  }

}
