package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.config.WorkspaceManager;
import com.opensymphony.workflow.designer.editor.*;
import com.opensymphony.workflow.designer.swing.BarFactory;
import com.opensymphony.workflow.designer.swing.SplashWindow;
import com.opensymphony.workflow.designer.swing.CardPanel;
import com.opensymphony.workflow.designer.swing.EmptyBorderSplitPane;
import com.opensymphony.workflow.designer.swing.FramePanel;
import com.opensymphony.workflow.loader.WorkflowConfigDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.Workspace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
  private static final Log log = LogFactory.getLog(WorkflowDesigner.class);

  public static final String WORKSPACE_SUFFIX = ".wsf";

  private Navigator navigator;
  private WorkspaceManager manager = new WorkspaceManager();
  private JTabbedPane graphTabs = new JTabbedPane();
  // Current WorkSpace File
  private List graphs = new ArrayList();
  private List mlayout = new ArrayList();
  private JSplitPane mainSplitPane;
  private EmptyBorderSplitPane leftSplitPane;
  private CardPanel detailPanel = new CardPanel();
  private FramePanel detailFramePanel;
  public static WorkflowDesigner INSTANCE = null;
  public static WorkflowConfigDescriptor config = null;

  public WorkflowDesigner()
  {
    super("OSWorkflow Designer");
    INSTANCE = this;
    navigator = new Navigator(this);
    setJMenuBar(BarFactory.createMenubar(manager));
    detailFramePanel = new FramePanel("Details", false);
    detailFramePanel.setContent(detailPanel);
    detailFramePanel = new FramePanel("Details", false);
    //    detailFramePanel.setContent(new JScrollPane(detailPanel));
    detailFramePanel.setContent(detailPanel);

    loadConfiguration();
    // create workspace view
    FramePanel flowsPanel = new FramePanel("Workspace", false);
    flowsPanel.setContent(new JScrollPane(navigator));

    // layout
    leftSplitPane = new EmptyBorderSplitPane(JSplitPane.VERTICAL_SPLIT, flowsPanel, detailFramePanel);
    mainSplitPane = new EmptyBorderSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, new JScrollPane(graphTabs));
    graphTabs.setVisible(false);
    //    graphTabs.setFont(FontHelper.TAB_ARIAL);
    mainSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.MAIN_DIVIDER_LOCATION, 150));
    leftSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.DETAIL_DIVIDER_LOCATION, 150));

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
    getContentPane().add(mainSplitPane, BorderLayout.CENTER);

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
      openWorkspace(new File(lastOpened));
      String workflow = Prefs.INSTANCE.get(Prefs.WORKFLOW_CURRENT, null);
      if(workflow != null)
      {
        navigator.selectWorkflow(workflow);
      }
    }
  }

  public WorkflowGraph getCurrentGraph()
  {
    int index = graphTabs.getSelectedIndex();
    if(index == -1 || index >= graphs.size()) return null;
    return (WorkflowGraph)graphs.get(index);
  }

  public void createGraph(String workflowName)
  {
    GraphModel model = new WorkflowGraphModel();
    Layout layout = manager.getCurrentWorkspace().getLayout(workflowName);
    boolean hasLayout = layout != null;
    if(layout == null) layout = new Layout();
    WorkflowDescriptor descriptor;
    try
    {
      descriptor = manager.getCurrentWorkspace().getWorkflow(workflowName);
    }
    catch(FactoryException e)
    {
      log.error("Error creating graph:" + e.getMessage());
      return;
    }
    mlayout.add(layout);
    WorkflowGraph graph = new WorkflowGraph(model, descriptor, layout, !hasLayout);
    graph.addGraphSelectionListener(this);
    graphs.add(graph);
    graphTabs.add(workflowName, new JScrollPane(graph));
    graphTabs.setSelectedIndex(graphTabs.getComponentCount() - 1);
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
      log.error("Error saving prefs", e);
    }
    System.exit(0);
  }

  public void valueChanged(GraphSelectionEvent e)
  {
    int index = e.getCells().length;
    Object lastModified = e.getCells()[index - 1];
    if(lastModified instanceof WorkflowCell || lastModified instanceof WorkflowEdge)
    {
      if(e.isAddedCell(index - 1))
      {
        showDetails(lastModified);
      }
    }
    else if(lastModified != null)
    {
      log.debug("unhandled selection:" + lastModified.getClass());
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
      if(node instanceof WorkflowCell)
      {
        panel.setCell((WorkflowCell)node);
      }
      else if(node instanceof WorkflowEdge)
      {
        panel.setEdge((WorkflowEdge)node);
      }
      String title = panel.getTitle();
      detailFramePanel.setTitle("Details" + (title != null ? (" - " + title) : ""));
      detailPanel.showCard(panel);
    }
    else
    {
      log.warn("no detail panel for " + node.getClass());
    }
  }

  public void openWorkspace(File f)
  {
    if(f != null && f.exists())
    {
      try
      {
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, f.toString());
        manager.loadWorkspace(f);
        navigator.setWorkspace(manager.getCurrentWorkspace());
      }
      catch(Exception t)
      {
        log.error("Error opening workspace", t);
      }
    }
  }

  public void save(int index)
  {
    Layout layout = (Layout)mlayout.get(index);
    WorkflowGraphModel obj = (WorkflowGraphModel)((WorkflowGraph)graphs.get(index)).getModel();
    layout.setActivity(obj.getActivitiesList());
    String workflowName = graphTabs.getTitleAt(index);
    manager.getCurrentWorkspace().setLayout(workflowName, layout);
    try
    {
      manager.getCurrentWorkspace().saveWorkflow(workflowName, manager.getCurrentWorkspace().getWorkflow(workflowName), true);
    }
    catch(FactoryException e)
    {
      log.error("Error saving " + workflowName + ": " + e);
    }
  }

  public void saveOpenGraphs()
  {
    for(int i = 0; i < graphTabs.getTabCount(); i++)
    {
      save(i);
    }
  }

  public void saveWorkspace()
  {
    manager.saveWorkspace();
  }

  public void newWorkspace()
  {
    closeWorkspace();
    Workspace workspace = new Workspace();
    manager.setCurrentWorkspace(workspace);
    navigator.setWorkspace(workspace);
  }

  public void closeWorkspace()
  {
    int count = graphTabs.getComponentCount();
    for(int i = count - 1; i >= 0; i--)
    {
      graphTabs.remove(i);
    }
    manager.setCurrentWorkspace(null);
    navigator.setWorkspace(null);
    Prefs.INSTANCE.remove(Prefs.LAST_WORKSPACE);
    mlayout.clear();
    graphs.clear();
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
    for(int i = 0; i < graphTabs.getTabCount(); i++)
    {
      String name = graphTabs.getTitleAt(i);
      if(name.equals(workflowName))
      {
        graphTabs.setSelectedIndex(i);
        Prefs.INSTANCE.put(Prefs.WORKFLOW_CURRENT, workflowName);
        return;
      }
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
        log.fatal("Could not load workflow plugin file", e);
      }

      InputStream is = WorkflowDesigner.class.getResourceAsStream("/META-INF/palette.xml");
      Document doc = db.parse(is);

      Element root = (Element)doc.getElementsByTagName("plugin").item(0);

      config = new WorkflowConfigDescriptor(root);
    }
    catch(SAXException e)
    {
      log.fatal("Syntax error in plugin file", e);
    }
    catch(IOException e)
    {
      log.fatal("Plugin file does not exist", e);
    }
  }

  public static void main(String[] args)
  {
    String spec = System.getProperty("java.specification.version");
    if(spec.startsWith("1.3") || spec.startsWith("1.2") || spec.startsWith("1.1"))
    {
      System.out.println("Workflow Designer requires JDK 1.4.0 or higher");
      System.exit(1);
    }
    if(System.getProperty("os.name").startsWith("Windows"))
    {
      try
      {
        UIManager.setLookAndFeel((LookAndFeel)Class.forName("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel", true, WorkflowDesigner.class.getClassLoader()).newInstance());
      }
      catch(Exception e)
      {
      }
    }
    //all other platforms except for OSX get the plastic LAF
    else if(System.getProperty("mrj.version") == null)
    {
      try
      {
        UIManager.setLookAndFeel((LookAndFeel)Class.forName("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel", true, WorkflowDesigner.class.getClassLoader()).newInstance());
      }
      catch(Exception e)
      {
      }
    }

    SplashWindow frame = new SplashWindow();
    frame.init();
    frame.show();
    WorkflowDesigner d = new WorkflowDesigner();
    d.pack();
    d.show();
  }
}
