package com.opensymphony.workflow.designer;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import javax.swing.*;

import com.opensymphony.workflow.loader.Workspace;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 8:56:07 PM
 */
public class Navigator extends JTree implements TreeSelectionListener
{
  private WorkflowDesigner designer;
  private DefaultMutableTreeNode rootNode;

  public Navigator(WorkflowDesigner designer)
  {
    super(new DefaultTreeModel(new DefaultMutableTreeNode("<no workspace>")));
    rootNode = (DefaultMutableTreeNode)getModel().getRoot();
    this.designer = designer;
    addTreeSelectionListener(this);
    //tree.setEditable(true);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setShowsRootHandles(true);
  }

  public void selectWorkflow(String name)
  {
    Object root = getModel().getRoot();
    int count = getModel().getChildCount(root);
    for(int i=0;i<count;i++)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)getModel().getChild(root, i);
      if(node.getUserObject().equals(name))
      {
        TreePath path = new TreePath(new Object[]{root, node});
        getSelectionModel().setSelectionPath(path);
        expandPath(path);
        designer.selectWorkflow(name);
        return;
      }
    }
  }

  public void valueChanged(TreeSelectionEvent e)
  {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
    if(node == null || node.equals(getModel().getRoot()))
      return;

    if(node.isLeaf())
    {
      String  workflowName = node.getUserObject().toString();
      designer.selectWorkflow(workflowName);
    }
  }

  public void setWorkspace(Workspace workspace)
  {
    rootNode.removeAllChildren();
    ((DefaultTreeModel)getModel()).reload();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
    if(workspace != null)
    {
      String[] workflows = workspace.getWorkflowNames();
      for(int i = 0; i < workflows.length; i++)
      {
        addWorkflow(workflows[i]);
      }
      root.setUserObject(workspace.getName());
      expandRow(0);
    }
    else
    {
      root.setUserObject("<no workspace>");
    }
  }

  public void addWorkflow(String name)
  {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
    model.insertNodeInto(childNode, root, root.getChildCount());
  }
}
