package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.event.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.config.WorkspaceManager;
import com.opensymphony.workflow.designer.editor.*;
import com.opensymphony.workflow.designer.swing.*;
import com.opensymphony.workflow.designer.swing.status.StatusBar;
import com.opensymphony.workflow.designer.dialogs.NewWorkspaceDialog;
import com.opensymphony.workflow.loader.PaletteDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.Workspace;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 8:36:20 PM
 */
public class WorkflowDesigner extends JFrame implements GraphSelectionListener, GraphModelListener
{
  public static final String WORKSPACE_SUFFIX = ".wsf";

  private Navigator navigator;
  private RelationshipsNavigator relationshipsNavigator;
  private WorkspaceManager manager = new WorkspaceManager();
  private GraphTabbedPane graphTabs = new GraphTabbedPane();
  private JSplitPane mainSplitPane;
  private EmptyBorderSplitPane detailsRelationsSplitPane;
  private EmptyBorderSplitPane leftSplitPane;
  private CardPanel detailPanel = new CardPanel();
  private FramePanel detailFramePanel;
  private FramePanel relationshipsFramePanel;
  public static WorkflowDesigner INSTANCE = null;
  private PaletteDescriptor palette = null;
  public StatusBar statusBar;

  public WorkflowDesigner(Splash splash)
  {
    super(ResourceManager.getString("app.name"));
    INSTANCE = this;

    setJMenuBar(BarFactory.createMenubar(manager));
    splash.setProgress(30);
    navigator = new Navigator(this);
    JScrollPane sp = new JScrollPane(detailPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    detailFramePanel = new FramePanel(ResourceManager.getString("details"), false);
    detailFramePanel.setContent(sp);
	relationshipsNavigator = new RelationshipsNavigator(this);

    splash.setProgress(40);
    loadPalette();
	splash.setProgress(50);

    // create workspace view
    FramePanel flowsPanel = new FramePanel(ResourceManager.getString("workspace"), false);
    flowsPanel.setContent(new JScrollPane(navigator));

	// create workspace view
	relationshipsFramePanel = new FramePanel(ResourceManager.getString("relationships"), false);
	relationshipsFramePanel.setContent(new JScrollPane(relationshipsNavigator));

    // layout
	detailsRelationsSplitPane = new EmptyBorderSplitPane(JSplitPane.VERTICAL_SPLIT, relationshipsFramePanel, detailFramePanel);
	leftSplitPane = new EmptyBorderSplitPane(JSplitPane.VERTICAL_SPLIT, flowsPanel, detailsRelationsSplitPane);
    mainSplitPane = new EmptyBorderSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, new JScrollPane(graphTabs));
    graphTabs.setVisible(false);
    mainSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.MAIN_DIVIDER_LOCATION, 150));
    leftSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.DETAIL_DIVIDER_LOCATION, 50));
	detailsRelationsSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.DETAIL_DIVIDER_LOCATION, 150));

    splash.setProgress(60);
    //Provide a preferred size for the split pane
    String bounds = Prefs.INSTANCE.get(Prefs.DESIGNER_BOUNDS, "100, 100, 800, 600");
    StringTokenizer tok = new StringTokenizer(bounds, ",");
    int x = Integer.parseInt(tok.nextToken().trim());
    int y = Integer.parseInt(tok.nextToken().trim());
    int w = Integer.parseInt(tok.nextToken().trim());
    int h = Integer.parseInt(tok.nextToken().trim());
    setLocation(x, y);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(BarFactory.createToolbar(), BorderLayout.NORTH);
    splash.setProgress(65);
    getContentPane().add(mainSplitPane, BorderLayout.CENTER);
    statusBar = BarFactory.createStatusBar();
    getContentPane().add(statusBar, BorderLayout.SOUTH);

    splash.setProgress(70);
    mainSplitPane.setPreferredSize(new Dimension(w, h));

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        quit();
      }
    });
    String lastOpened = Prefs.INSTANCE.get(Prefs.LAST_WORKSPACE, null);
    if(lastOpened != null)
    {
	    try
	    {
        if(lastOpened.indexOf(":/") == -1)
        {
          openWorkspace(new File(lastOpened).toURL());
        }
        else
        {
  		    openWorkspace(new URL(lastOpened));
        }
	    }
	    catch(MalformedURLException e)
	    {
		    e.printStackTrace();
	    }
	    String workflow = Prefs.INSTANCE.get(Prefs.WORKFLOW_CURRENT, null);
      if(workflow != null)
      {
        navigator.selectWorkflow(workflow);
      }
    }
  }

  public void graphChanged(GraphModelEvent e)
  {
  	if (detailPanel.getVisibleCard()==null)
  	{
  		return;
  	}

	WorkflowCell      detailCell = ((DetailPanel)detailPanel.getVisibleCard()).getCell();
	WorkflowEdge      detailEdge = ((DetailPanel)detailPanel.getVisibleCard()).getEdge();
	DefaultGraphCell  relationsCell = relationshipsNavigator.getCell();

	Object[] cells = e.getChange().getChanged();
	for (int i=0; i<cells.length; i++)
	{
		if (cells[i] instanceof WorkflowCell)
		{
			if (cells[i]==detailCell)
			{
				showDetails(detailCell);
			}
		}
		else if (cells[i] instanceof WorkflowEdge)
		{
			if (cells[i]==detailEdge)
			{
				showDetails(detailEdge);
			}
		}
		if (cells[i] instanceof DefaultGraphCell)
		{
			showRelationships(relationsCell);
		}
	}
  }

  public WorkflowGraph getCurrentGraph()
  {
	  return graphTabs.getCurrentGraph();
  }

	public void deleteWorkflow(String workflowName)
	{
		graphTabs.removeGraph(workflowName);
		navigator.removeWorkflow(workflowName);
		manager.getCurrentWorkspace().deleteWorkflow(workflowName);
	}

  public void closeCurrentWorkflow()
  {
    graphTabs.removeGraph(graphTabs.getCurrentGraph());
  }

  public void createGraph(String workflowName)
  {
    Workspace currentWorkspace = manager.getCurrentWorkspace();
    Layout layout = currentWorkspace.getLayout(workflowName);
    WorkflowGraphModel model = new WorkflowGraphModel(layout);
    model.setPalette(palette);
	model.addGraphModelListener(this);
    boolean hasLayout = layout != null;
    if(layout == null) layout = new Layout();
    WorkflowDescriptor descriptor;
    try
    {
      descriptor = currentWorkspace.getWorkflow(workflowName);
    }
    catch(FactoryException e)
    {
      e.printStackTrace();
      return;
    }
    WorkflowGraph graph = new WorkflowGraph(model, descriptor, layout, !hasLayout);
    graph.addGraphSelectionListener(this);
	  graph.setName(workflowName);
	  graphTabs.addGraph(graph);
	  graphTabs.setVisible(true);
  }

  public void quit()
  {
    Point location = getLocation();
    Prefs.INSTANCE.putInt(Prefs.MAIN_DIVIDER_LOCATION, mainSplitPane.getDividerLocation());
    Prefs.INSTANCE.putInt(Prefs.DETAIL_DIVIDER_LOCATION, leftSplitPane.getDividerLocation());
    Prefs.INSTANCE.put(Prefs.DESIGNER_BOUNDS, location.x + "," + location.y + "," + mainSplitPane.getWidth() + "," + mainSplitPane.getHeight());
    try
    {
      Prefs.INSTANCE.flush();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
  }

	public void renameWorkflow(String oldName, String newName)
	{
		graphTabs.renameGraph(oldName, newName);
		manager.getCurrentWorkspace().renameWorkflow(oldName, newName);
	}

  public void valueChanged(GraphSelectionEvent e)
  {
	  Object lastAdded = null;
	  for(int i = e.getCells().length-1; i >= 0; i--)
	  {
		  if(e.isAddedCell(i))
		  {
			  lastAdded = e.getCells()[i];
			  break;
		  }
	  }
    if(lastAdded instanceof WorkflowCell || lastAdded instanceof WorkflowEdge)
    {
		showDetails(lastAdded);
		showRelationships(lastAdded);
    }
  }

  public void showDetails(Object node)
  {
    String panelName = node.getClass().getName();
    DetailPanel current = (DetailPanel)detailPanel.getVisibleCard();
    if(current != null) current.closeView();
    DetailPanel panel = (DetailPanel)detailPanel.showCard(panelName);
    if(panel == null)
    {
      if(node instanceof StepCell)
      {
        panel = new StepEditor();
      }
      else if(node instanceof SplitCell)
      {
        panel = new SplitEditor();
      }
      else if(node instanceof JoinCell)
      {
        panel = new JoinEditor();
      }
      else if(node instanceof ResultEdge)
      {
        panel = new ResultEditor();
      }
      else if(node instanceof InitialActionCell)
      {
        panel = new InitialActionEditor();
      }
      if(panel != null)
      {
        panel.setName(panelName);
        //detailPanel.showCard(panel);
      }
    }
    if(panel != null)
    {
      WorkflowGraph currentGraph = graphTabs.getCurrentGraph();
      panel.setModel(currentGraph.getWorkflowGraphModel());
      if(node instanceof WorkflowCell)
      {
        panel.setCell((WorkflowCell)node);
      }
      else if(node instanceof WorkflowEdge)
      {
        panel.setEdge((WorkflowEdge)node);
      }

      //String title = panel.getTitle();
      String title = currentGraph.convertValueToString(node);
      detailFramePanel.setTitle(ResourceManager.getString("details") + (title != null ? (" - " + title) : ""));
      detailPanel.showCard(panel);
    }
    else
    {
      System.out.println("WARN: no detail panel for " + node.getClass());
    }
  }

  public void showRelationships(Object node)
  {
	WorkflowGraph currentGraph = graphTabs.getCurrentGraph();
  	relationshipsNavigator.showRelationships(node, currentGraph);
	String title = currentGraph.convertValueToString(node);
	relationshipsFramePanel.setTitle(ResourceManager.getString("relationships") + (title != null ? (" - " + title) : ""));
  }

  public void openWorkspace(URL file)
  {
    if(file != null)
    {
      try
      {
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, file.toString());
        manager.loadWorkspace(file);
	      Workspace workspace = manager.getCurrentWorkspace();
	      navigator.setWorkspace(workspace);
	      String[] workflows = workspace.getWorkflowNames();
	      for(int i = 0; i < workflows.length; i++)
	      {
		      createGraph(workflows[i]);
	      }
      }
      catch(Exception t)
      {
        t.printStackTrace();
      }
    }
  }

  public void checkWorkspaceExists()
  {
    if(manager.getCurrentWorkspace()==null)
    {
      NewWorkspaceDialog newSpace = new NewWorkspaceDialog(this, ResourceManager.getString("workspace.new"), true);
	    newSpace.pack();
	    newSpace.getBanner().setTitle("");
	    newSpace.getBanner().setSubtitle(ResourceManager.getString("workspace.new.long"));
      Utils.centerComponent(this, newSpace);
	    Dimension size = newSpace.getSize();
	    newSpace.setSize(size.width+10, size.height+15);
      newSpace.show();
    }
  }

  private void save(WorkflowGraph graph)
  {
    Layout layout = graph.getGraphLayout();
    WorkflowGraphModel model = (WorkflowGraphModel)graph.getModel();
    layout.setAllEntries(model.getActivitiesList());
    String workflowName = graph.getName();
    manager.getCurrentWorkspace().setLayout(workflowName, layout);
    try
    {
      manager.getCurrentWorkspace().saveWorkflow(workflowName, manager.getCurrentWorkspace().getWorkflow(workflowName), graph, true);
    }
    catch(InvalidWorkflowDescriptorException e)
    {
      try
      {
        System.out.println("Error saving workflow: " + e);
        PrintWriter out = new PrintWriter(System.out);
        manager.getCurrentWorkspace().getWorkflow(workflowName).writeXML(out, 0);
        out.flush();
      }
      catch(FactoryException e1)
      {
        e1.printStackTrace();
      }
      JOptionPane.showMessageDialog(this, ResourceManager.getString("error.save.workflow", new Object[]{e.getMessage()}),
                                    ResourceManager.getString("error.save.workflow.long", new Object[]{workflowName}), JOptionPane.ERROR_MESSAGE);
    }
    catch(FactoryException e)
    {
      e.printStackTrace();
    }
  }

  public void saveOpenGraphs()
  {
	  WorkflowGraph[] graphs = graphTabs.getGraphs();
	  for(int i = 0; i < graphs.length; i++)
	  {
		  save(graphs[i]);
	  }
  }

  public void saveWorkspace()
  {
    manager.saveWorkspace();
  }

  public Workspace newWorkspace()
  {
    closeWorkspace();
    Workspace workspace = new Workspace();
    manager.setCurrentWorkspace(workspace);
    navigator.setWorkspace(workspace);
    return workspace;
  }

  public void closeWorkspace()
  {
    //don't bother doing anything if we have no workspace visible
    if(!graphTabs.isVisible()) return;
	  graphTabs.removeAll();
    manager.setCurrentWorkspace(null);
    navigator.setWorkspace(null);
    Prefs.INSTANCE.remove(Prefs.LAST_WORKSPACE);
    graphTabs.setVisible(false);
  }

  public void newWorkflowCreated(String name)
  {
    navigator.addWorkflow(name);
    navigator.selectWorkflow(name);
  }

  public Navigator navigator()
  {
    return navigator;
  }

  public void selectWorkflow(String workflowName)
  {
	  if(graphTabs.selectWorkflow(workflowName))
	  {
		  Prefs.INSTANCE.put(Prefs.WORKFLOW_CURRENT, workflowName);
		  return;
	  }
    createGraph(workflowName);
    Prefs.INSTANCE.put(Prefs.WORKFLOW_CURRENT, workflowName);
    graphTabs.setVisible(true);
  }

  private void loadPalette()
  {
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);

      DocumentBuilder db = null;

      try
      {
        db = dbf.newDocumentBuilder();
      }
      catch(ParserConfigurationException e)
      {
        e.printStackTrace();
        System.exit(1);
      }

      InputStream is = WorkflowDesigner.class.getResourceAsStream("/META-INF/palette.xml");
      Document doc = db.parse(is);
	    ResourceBundle bundle = ResourceBundle.getBundle("META-INF/palette", Locale.getDefault(), getClass().getClassLoader());
      Element root = (Element)doc.getElementsByTagName("plugin").item(0);

      palette = new PaletteDescriptor(root, new EnhancedResourceBundle(bundle));
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
