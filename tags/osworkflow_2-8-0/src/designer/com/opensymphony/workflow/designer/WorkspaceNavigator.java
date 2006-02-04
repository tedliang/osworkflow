package com.opensymphony.workflow.designer;

import java.util.List;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.net.MalformedURLException;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.swing.FileDropHandler;
import com.opensymphony.workflow.designer.actions.AssignPalette;
import com.opensymphony.workflow.designer.actions.DeleteWorkflow;
import com.opensymphony.workflow.designer.actions.ImportWorkflow;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 15, 2003
 *         Time: 8:56:07 PM
 */
public class WorkspaceNavigator extends JTree implements TreeSelectionListener, TreeModelListener
{
  private WorkflowDesigner designer;
  private DefaultMutableTreeNode rootNode;
  private String currentWorkflow;
  private WorkflowFactory currentWorkspace = null;
	private DefaultTreeCellRenderer  cellRenderer = new WorkspaceCellRenderer();
  
  private JPopupMenu popup;
  private DeleteWorkflow deleteWorkflow;
  private AssignPalette assignPalette;

  public WorkspaceNavigator(WorkflowDesigner designer)
  {
    super(new DefaultTreeModel(new DefaultMutableTreeNode(ResourceManager.getString("workspace.none"))));
    rootNode = (DefaultMutableTreeNode)getModel().getRoot();
    this.designer = designer;
    addTreeSelectionListener(this);
    setEditable(false);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setShowsRootHandles(true);
    setCellRenderer(cellRenderer);
    getModel().addTreeModelListener(this);

    popup = new JPopupMenu();
    popup.setInvoker(this);
    //this is kinda a hack, since we assume that importworkflow has already been registered
    //(which it has, but only because the toolbar is created before the nav, so a bit fragile
    deleteWorkflow = new DeleteWorkflow(designer);
    assignPalette = new AssignPalette(designer);
    popup.add(new JMenuItem(ActionManager.register("deleteflow", deleteWorkflow)));
    ActionManager.register("assign.palette", assignPalette);
    assignPalette.setEnabled(false);
    popup.add(new JMenuItem(assignPalette));

    addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        handlePopup(e);
      }

      public void mouseReleased(MouseEvent e)
      {
        handlePopup(e);
      }

      private void handlePopup(MouseEvent e)
      {
        if(e.isPopupTrigger())
        {
          int row = getRowForLocation(e.getX(), e.getY());
          if(row != -1)
          {
            TreePath path = getPathForRow(row);
            if(path.getPathCount() != 2) return;
            String workflowName = path.getLastPathComponent().toString();
            Point p = new Point(e.getX(), e.getY());
            SwingUtilities.convertPointToScreen(p, WorkspaceNavigator.this);
            deleteWorkflow.setWorkflow(workflowName);
            assignPalette.setWorkflow(workflowName);
            popup.setLocation(p.x, p.y);
            popup.setVisible(true);
          }
        }
      }
    });

    new FileDropHandler(this, true)
    {
      public void filesDropped(File[] files)
      {
        for(int i = 0; i < files.length; i++)
        {
          File file = files[i];
          if(file.getName().endsWith(".xml") && !file.isDirectory())
          {
            try
            {
              ((ImportWorkflow)ActionManager.get("importflow")).importURL(file.toURL());
            }
            catch(MalformedURLException e)
            {
              e.printStackTrace();
            }
          }
        }
      }
    };
  }

  public void selectWorkflow(String name)
  {
    Object root = getModel().getRoot();
    int count = getModel().getChildCount(root);
    for(int i = 0; i < count; i++)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)getModel().getChild(root, i);
      if (node.getUserObject() instanceof WorkflowDescriptor)
      {
      	WorkflowDescriptor desc = (WorkflowDescriptor)node.getUserObject();
      	if(desc.getName().equals(name))
      	{
					designer.selectWorkflow(name);
        	
        	TreePath path = new TreePath(new Object[]{root, node});
        	getSelectionModel().setSelectionPath(path);
        	expandPath(path);
        	return;
      	}
      }
    }
  }

  public void removeWorkflow(String name)
  {
    Object root = getModel().getRoot();
    DefaultTreeModel model = (DefaultTreeModel)getModel();
    int count = model.getChildCount(root);
    for(int i = 0; i < count; i++)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(root, i);
      if (node.getUserObject() instanceof WorkflowDescriptor)
      {
      	WorkflowDescriptor workflow = (WorkflowDescriptor)node.getUserObject();
      	if (workflow.getName().equals(name))
      	{ 
					model.removeNodeFromParent(node);
					return;
      	}
      }
    }
  }
	
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
		if (node == null) 
			return;
		designer.showDetails(node.getUserObject());
		Object user = node.getUserObject();
		if (user instanceof AbstractDescriptor)
		{
			if (user instanceof WorkflowDescriptor)
			{
				designer.selectWorkflow(((WorkflowDescriptor)user).getName());
			}
			else if (user instanceof ResultDescriptor)
			{
        // select the current workflow
				TreeNode[] nodes = node.getPath();
				if (nodes.length>2)
				{
					DefaultMutableTreeNode workflowNode = (DefaultMutableTreeNode)nodes[1];
					if (workflowNode.getUserObject() instanceof WorkflowDescriptor)
					{
						designer.selectWorkflow(((WorkflowDescriptor)workflowNode.getUserObject()).getName());	 	
					}
				}
				designer.selectCell((AbstractDescriptor)user);
			}
			else
			{
				AbstractDescriptor parentDescriptor = ((AbstractDescriptor)user).getParent();
				if (parentDescriptor instanceof WorkflowDescriptor)
				{
					designer.selectWorkflow(((WorkflowDescriptor)parentDescriptor).getName());
					designer.selectCell((AbstractDescriptor)user);
				}
			}
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

  public void setWorkspace(WorkflowFactory workspace)
  {
    removeChildNodes(rootNode);
    ((DefaultTreeModel)getModel()).reload();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
    if(workspace != null)
    {
      String[] workflows;
      currentWorkspace = workspace;
      try {
				workflows = workspace.getWorkflowNames();
      }
      catch(FactoryException t)
      {
      	return;
      }
      
      for(int i = 0; i < workflows.length; i++)
      {
        try 
        {
        	WorkflowDescriptor descriptor = workspace.getWorkflow(workflows[i], false);
        	addWorkflow(descriptor);
        }
        catch(FactoryException fe)
        {
        }
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
		if (currentWorkspace!=null)
		{
			try 
			{
				WorkflowDescriptor descriptor = currentWorkspace.getWorkflow(name);
				addWorkflow(descriptor);
			}
			catch(FactoryException fe)
			{        	
			}
		}
	}
	
  public void addWorkflow(WorkflowDescriptor desc)
  {
    if (desc!=null)
   	{
   		// Add the workflow
   		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(desc) {
   									public String toString()
										{
											if (getUserObject() instanceof WorkflowDescriptor)
											{
												return ((WorkflowDescriptor)getUserObject()).getName();
											}
											else
											{
												return super.toString();
											}
										}
   								};
			DefaultTreeModel model = (DefaultTreeModel)getModel();
    	DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
    	model.insertNodeInto(childNode, root, root.getChildCount());
   	
   		// Add all the workflow childs (folders)
   		// meta
   		// registers
   		// trigger-functions
   		// initial-actions (folder)
			DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(ResourceManager.getString("initialactions.folder"));
   		model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
   		addActions(desc.getInitialActions(), folderNode);
   		// global-actions (folder)
			folderNode = new DefaultMutableTreeNode(ResourceManager.getString("globalactions.folder"));
			model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
			addActions(desc.getGlobalActions(), folderNode);
   		// common-actions (folder)
			folderNode = new DefaultMutableTreeNode(ResourceManager.getString("commonactions.folder"));
			model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
			addCommonActions(desc, folderNode);
   		// steps (folder)
			folderNode = new DefaultMutableTreeNode(ResourceManager.getString("steps.folder"));
			model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
			addSteps(desc, folderNode);
   		// splits (folder)
			folderNode = new DefaultMutableTreeNode(ResourceManager.getString("splits.folder"));
			model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
			addSplits(desc, folderNode);
   		// joins (folder)
			folderNode = new DefaultMutableTreeNode(ResourceManager.getString("joins.folder"));
			model.insertNodeInto(folderNode, childNode, childNode.getChildCount());
			addJoins(desc, folderNode);
   	}
  }

	private void addActions(List actions, DefaultMutableTreeNode node)
	{
		if ((actions!=null)&&(node!=null))
		{
			DefaultTreeModel model = (DefaultTreeModel)getModel();
			removeChildNodes(node); 
		
			for (Iterator iterator=actions.iterator(); iterator.hasNext();)
			{
				ActionDescriptor action = (ActionDescriptor)iterator.next(); 
				DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action) {
										public String toString()
										{
											if (getUserObject() instanceof ActionDescriptor)
											{
												return ((ActionDescriptor)getUserObject()).getName();
											}
											else
											{
												return super.toString();
											}
										}
									};
				model.insertNodeInto(actionNode, node, node.getChildCount());
				addActionResults(action, actionNode);
			}			 
		}
	}
	
	private void addActionResults(ActionDescriptor action, DefaultMutableTreeNode node)
	{
		if ((action==null)||(node==null))
			return;
		
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		removeChildNodes(node);
		
		//	add the action unconditional-results
		ResultDescriptor unconditional = action.getUnconditionalResult();
		if (unconditional!=null)
		{
			DefaultMutableTreeNode unconditionalNode = new DefaultMutableTreeNode(unconditional){
								 public String toString()
								 {
									 if (getUserObject() instanceof ResultDescriptor)
									 {
										 String name = ((ResultDescriptor)getUserObject()).getDisplayName();
										 if ((name==null)||(name.length()==0))
										 {
										 		ActionDescriptor a = (ActionDescriptor)((ResultDescriptor)getUserObject()).getParent();
										 		if (a!=null)
										 			name = a.getName();
										 		else
										 			name = "";		
										 }
										 return name;
									 }
									 else
									 {
										 return super.toString();
									 }
								 }
							 };
			model.insertNodeInto(unconditionalNode, node, node.getChildCount());
		}
		// add the action conditional-results
		List results = action.getConditionalResults(); 			
		if (results!=null)
		{
			for (Iterator it=results.iterator(); it.hasNext();)
			{
				ConditionalResultDescriptor conditional = (ConditionalResultDescriptor)it.next();
				DefaultMutableTreeNode conditionalNode = new DefaultMutableTreeNode(conditional){
														 public String toString()
														 {
															 if (getUserObject() instanceof ConditionalResultDescriptor)
															 {
																 String name = ((ConditionalResultDescriptor)getUserObject()).getDisplayName();
																 if ((name==null)||(name.length()==0))
																 {
																 		ActionDescriptor a = (ActionDescriptor)((ConditionalResultDescriptor)getUserObject()).getParent();
																 		if (a!=null)
																 			name = a.getName();
																 		else
																 			name = "";
																 }
																 return name;
															 }
															 else
															 {
																 return super.toString();
															 }
														 }
													 };
				model.insertNodeInto(conditionalNode, node, node.getChildCount());
			}
		}
	}
	
	private void addCommonActions(WorkflowDescriptor desc, DefaultMutableTreeNode node)
	{
		if ((desc!=null)&&(node!=null))
		{
			DefaultTreeModel model = (DefaultTreeModel)getModel();
			removeChildNodes(node);
			
			Collection actions = desc.getCommonActions().values();  
			for (Iterator iterator=actions.iterator(); iterator.hasNext();)
			{
				ActionDescriptor action = (ActionDescriptor)iterator.next();
				DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action) {
										public String toString()
										{
											if (getUserObject() instanceof ActionDescriptor)
											{
												return ((ActionDescriptor)getUserObject()).getName();
											}
											else
											{
												return super.toString();
											}
										}
									};
				model.insertNodeInto(actionNode, node, node.getChildCount());
				addActionResults(action, actionNode);
			}			 
		}
	}
	
	private void addSplitResults(SplitDescriptor split, DefaultMutableTreeNode node)
	{
		if ((split==null)||(node==null))
			return;
	
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		removeChildNodes(node);
		
		List results = split.getResults();  			
		if (results!=null)
		{
			for (Iterator it=results.iterator(); it.hasNext();)
			{
				ResultDescriptor unconditional = (ResultDescriptor)it.next();
				DefaultMutableTreeNode resultNode = new DefaultMutableTreeNode(unconditional){
														 public String toString()
														 {
															 if (getUserObject() instanceof ResultDescriptor)
															 {
																 String name = ((ResultDescriptor)getUserObject()).getDisplayName();
																 if ((name==null)||(name.length()==0))
																 {
																		// TODO verificare il nome da assegnare nel caso il displayname sia vuoto
																		name = "Split Result";
																 }
																 return name;
															 }
															 else
															 {
																 return super.toString();
															 }
														 }
													 };
				model.insertNodeInto(resultNode, node, node.getChildCount());
			}
		}
	}

	private void addJoinResult(JoinDescriptor join, DefaultMutableTreeNode node)
	{
		if ((join==null)||(node==null))
			return;

		DefaultTreeModel model = (DefaultTreeModel)getModel();
		removeChildNodes(node);

		ResultDescriptor result = join.getResult();  			
		if (result!=null)
		{
			DefaultMutableTreeNode resultNode = new DefaultMutableTreeNode(result){
														 public String toString()
														 {
															 if (getUserObject() instanceof ResultDescriptor)
															 {
																 String name = ((ResultDescriptor)getUserObject()).getDisplayName();
																 if ((name==null)||(name.length()==0))
																 {
																		// TODO verificare il nome da assegnare nel caso il displayname sia vuoto
																		name = "Join Result";
																 }
																 return name;
															 }
															 else
															 {
																 return super.toString();
															 }
														 }
													 };
			model.insertNodeInto(resultNode, node, node.getChildCount());
		}
	}

	private void addSteps(WorkflowDescriptor desc, DefaultMutableTreeNode node)
	{
		if ((desc!=null)&&(node!=null))
		{
			DefaultTreeModel model = (DefaultTreeModel)getModel();
			removeChildNodes(node);
			
			List steps = desc.getSteps();  
			for (Iterator iterator=steps.iterator(); iterator.hasNext();)
			{
				StepDescriptor step = (StepDescriptor)iterator.next();
				DefaultMutableTreeNode stepNode = new DefaultMutableTreeNode(step) {
										public String toString()
										{
											if (getUserObject() instanceof StepDescriptor)
											{
												return ((StepDescriptor)getUserObject()).getName();
											}
											else
											{
												return super.toString();
											}
										}
									};
				model.insertNodeInto(stepNode, node, node.getChildCount());
				addActions(step.getActions(), stepNode); 
			}			 
		}
	}
  
  private void addSplits(WorkflowDescriptor desc, DefaultMutableTreeNode node)
  {
		if ((desc!=null)&&(node!=null))
		{
			DefaultTreeModel model = (DefaultTreeModel)getModel();
			removeChildNodes(node); 
			
			List splits = desc.getSplits();   
			for (Iterator iterator=splits.iterator(); iterator.hasNext();)
			{
				SplitDescriptor split = (SplitDescriptor)iterator.next();
				DefaultMutableTreeNode splitNode = new DefaultMutableTreeNode(split) {
										public String toString()
										{
											if (getUserObject() instanceof SplitDescriptor)
											{
												String s = "Split #" + ((SplitDescriptor)getUserObject()).getId();  
												return s;  
											}
											else
											{
												return super.toString();
											}
										}
									};
				model.insertNodeInto(splitNode, node, node.getChildCount());
				addSplitResults(split, splitNode);
			}			 
		}
  }
  
  private void addJoins(WorkflowDescriptor desc, DefaultMutableTreeNode node)
  {
		if ((desc!=null)&&(node!=null))
		{
			DefaultTreeModel model = (DefaultTreeModel)getModel();
			removeChildNodes(node);		
				
			List joins = desc.getJoins();    
			for (Iterator iterator=joins.iterator(); iterator.hasNext();)
			{
				JoinDescriptor join = (JoinDescriptor)iterator.next();
				DefaultMutableTreeNode joinNode = new DefaultMutableTreeNode(join) {
										public String toString()
										{
											if (getUserObject() instanceof JoinDescriptor)
											{
												String s = "Join #" + ((JoinDescriptor)getUserObject()).getId();   
												return s;  
											}
											else
											{
												return super.toString();
											}
										}
									};
				model.insertNodeInto(joinNode, node, node.getChildCount());
				addJoinResult(join, joinNode);
			}			 
		}
  }
  
  public void selectTreeNode(WorkflowDescriptor workflow, AbstractDescriptor desc)
 	{
  	// select a tree item  
  	if ((workflow==null)||(desc==null))
  		return;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
		// loop over the workspace childs (the workflows)
		for (int i=0; i<root.getChildCount(); i++)
		{
			DefaultMutableTreeNode workflowNode = (DefaultMutableTreeNode)root.getChildAt(i);
			if (workflowNode.getUserObject()==workflow)
			{
				// search over all the childs of the workflow
				if (workflowNode.getUserObject()==desc)
				{
					selectExpandNode(workflowNode);
				}
				else
				{
					DefaultMutableTreeNode node = findNodeRecursive(workflowNode, desc);
					if (node!=null)
					{
						selectExpandNode(node);
					}
				}
				break;		 
			}
		}
  }
 	
	private DefaultMutableTreeNode findNodeRecursive(DefaultMutableTreeNode parentNode, AbstractDescriptor desc)
	{
		if ((parentNode==null)||(desc==null))
			return null;
			
		for (int i=0; i<parentNode.getChildCount(); i++)
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(i);  
			if (childNode.getUserObject()==desc)
			{
				return childNode;
			}
			else
			{
				DefaultMutableTreeNode node = findNodeRecursive(childNode, desc);
				if (node!=null)
					return node;
			}
		}
		return null;
	}
 	
 	private DefaultMutableTreeNode findFolderNode(WorkflowDescriptor workflow, String folderName)
 	{
		if (workflow==null)
			return null;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
		DefaultMutableTreeNode workflowNode = findNodeRecursive(root, workflow);
		if (workflowNode!=null)
		{
			for (int z=0; z<workflowNode.getChildCount(); z++)
			{
				DefaultMutableTreeNode folderNode = (DefaultMutableTreeNode)workflowNode.getChildAt(z);
				if (folderNode.getUserObject() instanceof String)
				{
					String s = (String)folderNode.getUserObject();
					if (s.equals(folderName))
					{
						return folderNode; 							
					}
				}
			}
		}
		return null;
 	}
 	
 	private void selectExpandNode(DefaultMutableTreeNode node)
 	{
		TreeNode[] nodes = node.getPath();
		TreePath path = new TreePath(nodes);  
		getSelectionModel().setSelectionPath(path);
		expandPath(path);
		scrollPathToVisible(path);
	}
 	
 	public void reloadSteps(WorkflowDescriptor workflow)
 	{
 		DefaultMutableTreeNode stepFolder = findFolderNode(workflow, ResourceManager.getString("steps.folder")); 	
 		if (stepFolder!=null)
 		{
 			addSteps(workflow, stepFolder);
 		}
 	}
 	
	public void reloadSplits(WorkflowDescriptor workflow)
	{
		DefaultMutableTreeNode splitsFolder = findFolderNode(workflow, ResourceManager.getString("splits.folder")); 	
		if (splitsFolder!=null)
		{
			addSplits(workflow, splitsFolder);
		}
	}
 	
	public void reloadJoins(WorkflowDescriptor workflow)
	{
		DefaultMutableTreeNode joinsFolder = findFolderNode(workflow, ResourceManager.getString("joins.folder")); 	
		if (joinsFolder!=null)
		{
			addJoins(workflow, joinsFolder);
		}
	}
 	
 	public void reloadInitialAction(WorkflowDescriptor workflow)
 	{
		DefaultMutableTreeNode iaFolder = findFolderNode(workflow, ResourceManager.getString("initialactions.folder")); 	
		if (iaFolder!=null)
		{
			addActions(workflow.getInitialActions(), iaFolder);
		}
 	}
 	
 	public void reloadStep(WorkflowDescriptor workflow, StepDescriptor step)
 	{
 		DefaultMutableTreeNode stepNode = findNodeRecursive(findFolderNode(workflow, ResourceManager.getString("steps.folder")), step);
 		if (stepNode!=null)
 		{
 			addActions(step.getActions(), stepNode);	
 		}
 	}
 	
	public void reloadSplit(WorkflowDescriptor workflow, SplitDescriptor split)
	{
		DefaultMutableTreeNode splitNode = findNodeRecursive(findFolderNode(workflow, ResourceManager.getString("splits.folder")), split);
		if (splitNode!=null)
		{
			addSplitResults(split, splitNode);	
		}
	}
 	
 	public void reloadJoin(WorkflowDescriptor workflow, JoinDescriptor join)
 	{
		DefaultMutableTreeNode joinNode = findNodeRecursive(findFolderNode(workflow, ResourceManager.getString("joins.folder")), join);
		if (joinNode!=null)
		{
			addJoinResult(join, joinNode);	
		}
 	}
 	
 	public void reloadAction(WorkflowDescriptor workflow, ActionDescriptor action)
 	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
		DefaultMutableTreeNode workflowNode = findNodeRecursive(root, workflow);
 		DefaultMutableTreeNode actionNode = findNodeRecursive(workflowNode, action);  
 		if (actionNode!=null)
 		{
 			addActionResults(action, actionNode);
 		}
 	}
 	
 	public void reloadWorkflow(WorkflowDescriptor workflow)
 	{
 		reloadInitialAction(workflow);
 		reloadSteps(workflow);
 		reloadJoins(workflow);
 		reloadSplits(workflow);
 	}
 	
	private void removeChildNodes(DefaultMutableTreeNode parentNode)
	{
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		while (model.getChildCount(parentNode)>0)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)parentNode.getFirstChild(); 
			model.removeNodeFromParent(node);
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
  
  
  /*
   * @author acapitani
   */
  static class WorkspaceCellRenderer extends DefaultTreeCellRenderer
	{ 	
		public WorkspaceCellRenderer()
		{
			super();
		//	setClosedIcon(ResourceManager.getIcon("action"));
		//	setOpenIcon(ResourceManager.getIcon("action"));
		//	setLeafIcon(ResourceManager.getIcon("step"));
		}
  	
		public Component getTreeCellRendererComponent(JTree tree, Object value,
									boolean sel,
									boolean expanded,
									boolean leaf, int row,
									boolean hasFocus)
			{
				Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof DefaultMutableTreeNode)
				{
					Object obj = ((DefaultMutableTreeNode)value).getUserObject();
					if (obj instanceof WorkflowDescriptor)
					{
						// workflow icon
						setIcon(ResourceManager.getIcon("layout"));
					}
					else if (obj instanceof ActionDescriptor)
					{
						// action icon
						setIcon(ResourceManager.getIcon("action"));
					}
					else if (obj instanceof ConditionalResultDescriptor)
					{
						// conditional-result icon
						setIcon(ResourceManager.getIcon("conditional.result"));
					}
					else if (obj instanceof ResultDescriptor)
					{
						// unconditional-result icon
						setIcon(ResourceManager.getIcon("unconditional.result"));
					}
					else if (obj instanceof StepDescriptor)
					{
						// step icon
						setIcon(ResourceManager.getIcon("step"));
					}
					else if (obj instanceof SplitDescriptor)
					{
						// split icon
						setIcon(ResourceManager.getIcon("split"));
					}
					else if (obj instanceof JoinDescriptor)
					{
						setIcon(ResourceManager.getIcon("join"));
					}
				}
				return result; 
			} 	
		}
}
