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

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
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
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.LookUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 8:36:20 PM
 */
public class WorkflowDesigner extends JFrame implements GraphSelectionListener
{
  public static final String WORKSPACE_SUFFIX = ".wsf";

  private Navigator navigator;
  private WorkspaceManager manager = new WorkspaceManager();
  private GraphTabbedPane graphTabs = new GraphTabbedPane();
  // Current WorkSpace File
  //private List graphs = new ArrayList();
  //private List layouts = new ArrayList();
  private JSplitPane mainSplitPane;
  private EmptyBorderSplitPane leftSplitPane;
  private CardPanel detailPanel = new CardPanel();
  private FramePanel detailFramePanel;
  public static WorkflowDesigner INSTANCE = null;
  private PaletteDescriptor palette = null;
  private static Splash splash;
  public StatusBar statusBar;

  public WorkflowDesigner()
  {
    super(ResourceManager.getString("app.name"));
    INSTANCE = this;
    setJMenuBar(BarFactory.createMenubar(manager));
    splash.setProgress(30);
    navigator = new Navigator(this);
    JScrollPane sp = new JScrollPane(detailPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    detailFramePanel = new FramePanel(ResourceManager.getString("details"), false);
    detailFramePanel.setContent(sp);

    splash.setProgress(40);
    loadConfiguration();
    // create workspace view
    splash.setProgress(50);
    FramePanel flowsPanel = new FramePanel(ResourceManager.getString("workspace"), false);
    flowsPanel.setContent(new JScrollPane(navigator));

    // layout
    leftSplitPane = new EmptyBorderSplitPane(JSplitPane.VERTICAL_SPLIT, flowsPanel, detailFramePanel);
    mainSplitPane = new EmptyBorderSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, new JScrollPane(graphTabs));
    graphTabs.setVisible(false);
    //    graphTabs.setFont(FontHelper.TAB_ARIAL);
    mainSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.MAIN_DIVIDER_LOCATION, 150));
    leftSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.DETAIL_DIVIDER_LOCATION, 150));

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
        if(lastOpened.indexOf("://") == -1)
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

  public void createGraph(String workflowName)
  {
    Workspace currentWorkspace = manager.getCurrentWorkspace();
    Layout layout = currentWorkspace.getLayout(workflowName);
    WorkflowGraphModel model = new WorkflowGraphModel(layout);
    model.setPalette(palette);
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

      String title = panel.getTitle();
      detailFramePanel.setTitle(ResourceManager.getString("details") + (title != null ? (" - " + title) : ""));
      detailPanel.showCard(panel);
    }
    else
    {
      System.out.println("WARN: no detail panel for " + node.getClass());
    }
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
      manager.getCurrentWorkspace().saveWorkflow(workflowName, manager.getCurrentWorkspace().getWorkflow(workflowName), true);
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

  private void loadConfiguration()
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

  public static void main(String[] args)
  {
    System.getProperties().put("apple.laf.useScreenMenuBar", "true");
    String spec = System.getProperty("java.specification.version");
    if(spec.startsWith("1.3") || spec.startsWith("1.2") || spec.startsWith("1.1"))
    {
      System.out.println("Workflow Designer requires JDK 1.4.0 or higher");
      System.exit(1);
    }
    splash = new Splash(new Frame(), ResourceManager.getIcon("splash").getImage(), ResourceManager.getString("app.name"), true);
    splash.openSplash();
    splash.setProgress(10);

    if(LookUtils.class.getClassLoader() != null)
    {
      UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
    }
    Options.setGlobalFontSizeHints(com.jgoodies.plaf.FontSizeHints.MIXED);
    Options.setDefaultIconSize(new Dimension(18, 18));
    Options.setUseNarrowButtons(true);
    UIManager.put(com.jgoodies.plaf.Options.DEFAULT_ICON_SIZE_KEY, new Dimension(18, 18));
    if(LookUtils.IS_OS_WINDOWS_MODERN)
    {
      try
      {
        UIManager.setLookAndFeel((LookAndFeel)Class.forName("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel", true, com.jgoodies.plaf.windows.ExtWindowsLookAndFeel.class.getClassLoader()).newInstance());
      }
      catch(Exception e)
      {
	      e.printStackTrace();
      }
    }
    //all other platforms except for OSX get the plastic LAF
    else
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
      }
    }

    splash.setProgress(20);

    WorkflowDesigner d = new WorkflowDesigner();
    splash.setProgress(80);
    d.pack();
    splash.setProgress(90);
    d.show();
    splash.setProgress(100);
    splash.closeSplash();
    splash = null;
    d.checkWorkspaceExists();
  }
}
