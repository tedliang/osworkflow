package com.opensymphony.workflow.designer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.opensymphony.workflow.loader.Workspace;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 15, 2003
 *         Time: 8:56:07 PM
 */
public class Navigator extends JTree implements TreeSelectionListener, TreeModelListener
{
	private WorkflowDesigner designer;
	private DefaultMutableTreeNode rootNode;
	private String currentWorkflow;

	public Navigator(WorkflowDesigner designer)
	{
		super(new DefaultTreeModel(new DefaultMutableTreeNode(ResourceManager.getString("workspace.none"))));
		rootNode = (DefaultMutableTreeNode)getModel().getRoot();
		this.designer = designer;
		addTreeSelectionListener(this);
		setEditable(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		getModel().addTreeModelListener(this);
	}

	public void selectWorkflow(String name)
	{
		Object root = getModel().getRoot();
		int count = getModel().getChildCount(root);
		for(int i = 0; i < count; i++)
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
			currentWorkflow = node.getUserObject().toString();
			designer.selectWorkflow(currentWorkflow);
		}
	}

	public void treeNodesChanged(TreeModelEvent e)
	{
		DefaultMutableTreeNode node;
		node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
		/*
		 * If the event lists children, then the changed
		 * node is the child of the node we've already
		 * gotten.  Otherwise, the changed node and the
		 * specified node are the same.
		 */
		try
		{
			int index = e.getChildIndices()[0];
			node = (DefaultMutableTreeNode)(node.getChildAt(index));
		}
		catch(NullPointerException exc)
		{}
		String newValue = (String)node.getUserObject();
		designer.renameWorkflow(currentWorkflow, newValue);
		currentWorkflow = newValue;
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
			root.setUserObject(ResourceManager.getString("workspace.none"));
		}
	}

	public void addWorkflow(String name)
	{
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		model.insertNodeInto(childNode, root, root.getChildCount());
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
