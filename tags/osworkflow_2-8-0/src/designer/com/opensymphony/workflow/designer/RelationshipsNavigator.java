package com.opensymphony.workflow.designer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.Component;

import com.opensymphony.workflow.loader.*;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

public class RelationshipsNavigator extends JTree implements TreeSelectionListener, TreeModelListener
{
  private WorkflowDesigner designer;
  private WorkflowGraph currentGraph = null;
  private DefaultGraphCell currentCell = null;
  private Object currentObject = null;
  private DefaultTreeCellRenderer cellRenderer = new WorkflowCellRenderer();

  public RelationshipsNavigator(WorkflowDesigner designer)
  {
    super(new DefaultTreeModel(new DefaultMutableTreeNode(ResourceManager.getString("relationships"))));
    setRootVisible(false);
    this.designer = designer;
    addTreeSelectionListener(this);
    setEditable(true);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setShowsRootHandles(true);
    getModel().addTreeModelListener(this);
    setCellRenderer(cellRenderer);
  }

  private void clearRelationships(Object rootObject)
  {
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootObject);
    setRootVisible(true);
    model.setRoot(root);
    model.reload(root);
    currentObject = root.getUserObject();
  }

  public void showRelationships(Object node, WorkflowGraph graph)
  {
    if(node instanceof DefaultGraphCell)
    {
      currentGraph = graph;
      currentCell = (DefaultGraphCell)node;
      clearRelationships(node);
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
    java.util.Set edgeSet = WorkflowGraphModel.getEdges(currentGraph.getModel(), cells);
    for(int i = 0; i < stepDescriptor.getActions().size(); i++)
    {
      ActionDescriptor action = (ActionDescriptor)(stepDescriptor.getActions().get(i));
	  DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action) {
        public String toString()
        {
          if(getUserObject() instanceof ActionDescriptor)
          {
            return ((ActionDescriptor)getUserObject()).getName();
          }
          else
          {
            return super.toString();
          }
        }
      };
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

    currentObject = node.getUserObject();
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
    DefaultMutableTreeNode node;

    node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
    try
    {
      int index = e.getChildIndices()[0];
      node = (DefaultMutableTreeNode)(node.getChildAt(index));
    }
    catch(NullPointerException exc)
    {
    }
    if(currentObject instanceof ActionDescriptor)
    {
      ((ActionDescriptor)currentObject).setName((String)node.getUserObject());
    }
    else if(currentObject instanceof ResultEdge)
    {
      ((ResultEdge)currentObject).getDescriptor().setDisplayName((String)node.getUserObject());
    }
    else if(currentObject instanceof StepCell)
    {
      ((StepCell)currentObject).getDescriptor().setName((String)node.getUserObject());
    }
    node.setUserObject(currentObject);
    RefreshUIForNode(node);
    currentGraph.paintAll(currentGraph.getGraphics());
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    model.reload(node);
    designer.refreshUI();
  }

  protected void RefreshUIForNode(DefaultMutableTreeNode node)
  {
    Object currentObject = node.getUserObject();
    if(currentObject != null)
    {
      CellView currentView = currentGraph.getGraphLayoutCache().getMapping(currentObject, false);
      if(currentView != null)
      {
        currentView.update();
        //currentView.refresh(false);
        currentGraph.updateAutoSize(currentView);
      }
      for(int i = 0; i < node.getChildCount(); i++)
      {
        RefreshUIForNode((DefaultMutableTreeNode)node.getChildAt(i));
      }
    }
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

  static class WorkflowCellRenderer extends DefaultTreeCellRenderer
  {

    public WorkflowCellRenderer()
    {
      super();
      setClosedIcon(ResourceManager.getIcon("action"));
      setOpenIcon(ResourceManager.getIcon("action"));
      setLeafIcon(ResourceManager.getIcon("step"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus)
    {
      Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if(value instanceof DefaultMutableTreeNode)
      {
        if(((DefaultMutableTreeNode)value).getUserObject() instanceof ResultEdge)
        {
          ResultEdge edge = (ResultEdge)((DefaultMutableTreeNode)value).getUserObject();
          if(!sel)
          {
            setForeground(GraphConstants.getForeground(edge.getAttributes()));
          }
          if(tree.isEnabled())
          {
            if(edge.getDescriptor() instanceof ConditionalResultDescriptor)
            {
              setIcon(ResourceManager.getIcon("conditional.result"));
            }
            else
            {
              setIcon(ResourceManager.getIcon("unconditional.result"));
            }
          }
        }
      }
      return result;
    }

  }

}
