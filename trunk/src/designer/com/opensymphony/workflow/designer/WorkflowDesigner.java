package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.config.WorkspaceManager;
import com.opensymphony.workflow.designer.dialogs.NewWorkspaceDialog;
import com.opensymphony.workflow.designer.editor.*;
import com.opensymphony.workflow.designer.swing.*;
import com.opensymphony.workflow.designer.swing.status.StatusBar;
import com.opensymphony.workflow.loader.*;
import org.jgraph.event.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Hani Suleiman (hani@formicary.net) Date: May 15, 2003 Time: 8:36:20 PM
 */
public class WorkflowDesigner extends JFrame implements GraphSelectionListener, GraphModelListener
{
  public static final String WORKSPACE_SUFFIX = ".wsf";

  private WorkspaceNavigator navigator;
  private WorkspaceManager manager = new WorkspaceManager();
  private GraphTabbedPane graphTabs = new GraphTabbedPane();
  private DesignerService service = null;
  private JSplitPane mainSplitPane;
  private EmptyBorderSplitPane leftSplitPane;
  private CardPanel detailPanel = new CardPanel();
  private FramePanel detailFramePanel;
  private Object currentDetailObject = null;
  public static WorkflowDesigner INSTANCE = null;
  private PaletteDescriptor palette = null;
  public StatusBar statusBar;

  public WorkflowDesigner(Splash splash)
  {
    super(ResourceManager.getString("app.name"));
    INSTANCE = this;

    service = new DesignerService();

    setJMenuBar(BarFactory.createMenubar(manager, service.getVerb()));
    splash.setProgress(30);
    navigator = new WorkspaceNavigator(this);
    JScrollPane sp = new JScrollPane(detailPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    detailFramePanel = new FramePanel(ResourceManager.getString("details"), false);
    detailFramePanel.setContent(sp);

    splash.setProgress(40);
    loadPalette();
    splash.setProgress(50);

    // create workspace view
    FramePanel flowsPanel = new FramePanel(ResourceManager.getString("workspace"), false);
    flowsPanel.setContent(new JScrollPane(navigator));

    // layout
    leftSplitPane = new EmptyBorderSplitPane(JSplitPane.VERTICAL_SPLIT, flowsPanel, detailFramePanel);
    mainSplitPane = new EmptyBorderSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, new JScrollPane(graphTabs));
    graphTabs.setVisible(false);
    mainSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.MAIN_DIVIDER_LOCATION, 150));
    leftSplitPane.setDividerLocation(Prefs.INSTANCE.getInt(Prefs.DETAIL_DIVIDER_LOCATION, 50));

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
    if("new".equals(service.getVerb()))
    {
      newRemoteWorkspace();
    }
    else if("modify".equals(service.getVerb()))
    {
      newRemoteWorkspace();
      openRemoteWorkspace();
    }
    else
    {
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
  }

  public void graphChanged(GraphModelEvent e)
  {
    if(detailPanel.getVisibleCard() == null)
    {
      return;
    }

    AbstractDescriptor desc = ((DetailPanel)detailPanel.getVisibleCard()).getDescriptor();
    showDetails(desc);
    //DefaultGraphCell  relationsCell = relationshipsNavigator.getCell();

    Object[] cells = e.getChange().getChanged();
    //System.out.println("changed = " + java.util.Arrays.asList(cells));
    //for(int i = 0; i < cells.length; i++)
    //{
    //  if(cells[i] instanceof WorkflowCell)
    //  {
    //    if(cells[i] == detailCell)
    //    {
    //      showDetails(desc);
    //    }
    //  }
    //  else if(cells[i] instanceof WorkflowEdge)
    //  {
    //    if(cells[i] == detailEdge)
    //    {
    //      showDetails(desc);
    //    }
    //  }
    //  if(cells[i] instanceof DefaultGraphCell)
    //  {
    //    //showRelationships(relationsCell);
    //  }
    //}
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

  public void validateCurrentWorkflow()
  {
    WorkflowGraph graph = graphTabs.getCurrentGraph();
    if(graph != null)
    {
      validateWorkflow(graph.getName());
    }
  }

  public void validateSaveCurrentWorkflow()
  {
    WorkflowGraph graph = graphTabs.getCurrentGraph();
    if(graph != null)
    {
      save(graph, true);
    }
  }

  public void validateWorkflow(String workflowName)
  {
    WorkflowGraph graph = graphTabs.getGraph(workflowName);
    if(graph != null)
    {
      workflowName = graph.getName();
      WorkflowDescriptor d = graph.getDescriptor();
      if(d != null)
      {
        try
        {
          d.validate();
        }
        catch(InvalidWorkflowDescriptorException e)
        {
          System.out.println("Error validating workflow: " + e);
          JOptionPane.showMessageDialog(this,
                                        ResourceManager.getString("error.validate.workflow", new Object[]{e.getMessage()}),
                                        ResourceManager.getString("title.validate.workflow", new Object[]{workflowName}),
                                        JOptionPane.ERROR_MESSAGE);
          return;
        }

        JOptionPane.showMessageDialog(this,
                                      ResourceManager.getString("success.validate.workflow"),
                                      ResourceManager.getString("title.validate.workflow", new Object[]{workflowName}),
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void closeCurrentWorkflow()
  {
    WorkflowGraph graph = graphTabs.getCurrentGraph();
    if(graph != null)
    {
      deleteWorkflow(graph.getDescriptor().getName());
    }
    //graphTabs.removeGraph(graphTabs.getCurrentGraph());
  }

  public void createGraph(String workflowName)
  {
    //Workspace currentWorkspace = manager.getCurrentWorkspace();
    AbstractWorkflowFactory currentWorkspace = manager.getCurrentWorkspace();
    Layout layout = (Layout)currentWorkspace.getLayout(workflowName);
    WorkflowGraphModel model = new WorkflowGraphModel(layout);
    model.setPalette(palette);
    model.addGraphModelListener(this);
    boolean hasLayout = layout != null;
    if(layout == null) layout = new Layout();
    WorkflowDescriptor descriptor;
    try
    {
      descriptor = currentWorkspace.getWorkflow(workflowName, false);
    }
    catch(FactoryException e)
    {
      e.printStackTrace();
      return;
    }
    WorkflowGraph graph = new WorkflowGraph(model, descriptor, layout, !hasLayout);
    graph.addGraphSelectionListener(this);
    //graph.setName(workflowName);
    if(descriptor != null)
      graph.setName(descriptor.getName());
    else
      graph.setName(workflowName);
    graphTabs.addGraph(graph);
    graphTabs.setVisible(true);
  }

  public void quit()
  {
    Point location = getLocation();
    Prefs.INSTANCE.putInt(Prefs.MAIN_DIVIDER_LOCATION, mainSplitPane.getDividerLocation());
    Prefs.INSTANCE.putInt(Prefs.DETAIL_DIVIDER_LOCATION, leftSplitPane.getDividerLocation());
    Prefs.INSTANCE.put(Prefs.DESIGNER_BOUNDS, location.x + "," + location.y + ',' + mainSplitPane.getWidth() + ',' + mainSplitPane.getHeight());
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
    for(int i = e.getCells().length - 1; i >= 0; i--)
    {
      if(e.isAddedCell(i))
      {
        lastAdded = e.getCells()[i];
        break;
      }
    }
    if(lastAdded instanceof WorkflowCell || lastAdded instanceof WorkflowEdge)
    {
      AbstractDescriptor desc = getCellDescriptor(lastAdded);
      showDetails(desc);
      navigator.selectTreeNode(graphTabs.getCurrentGraph().getDescriptor(), desc);
    }
  }

  public void showSelectedCellDetails()
  {
    WorkflowGraph graph = graphTabs.getCurrentGraph();
    if(graph != null)
    {
      Object cell = graph.getSelectionModel().getSelectionCell();
      if(cell != null)
      {
        if(cell instanceof WorkflowCell || cell instanceof WorkflowEdge)
        {
          AbstractDescriptor desc = getCellDescriptor(cell);
          showDetails(desc);
          navigator.selectTreeNode(graphTabs.getCurrentGraph().getDescriptor(), desc);
        }
      }
    }
  }

  public void refreshUI()
  {
    if(currentDetailObject != null)
    {
      showDetails(currentDetailObject);
    }
  }

  public void showDetails(Object node)
  {
    if(node == null) return;
    String title = getDescriptorTitle(node);
    AbstractDescriptor descriptor = null;
    currentDetailObject = node;
    String panelName = node.getClass().getName();
    DetailPanel current = (DetailPanel)detailPanel.getVisibleCard();
    if(current != null)
      current.closeView();
    DetailPanel panel = (DetailPanel)detailPanel.showCard(panelName);
    if(panel == null)
    {
      if(node instanceof StepDescriptor)
      {
        panel = new StepEditor();
      }
      else if(node instanceof SplitDescriptor)
      {
        panel = new SplitEditor();
      }
      else if(node instanceof JoinDescriptor)
      {
        panel = new JoinEditor();
      }
      else if(node instanceof ResultDescriptor)
      {
        panel = new ResultEditor();
      }
      else if(node instanceof ActionDescriptor)
      {
        panel = new ActionEditor();
      }
      else if(node instanceof WorkflowDescriptor)
      {
        panel = new WorkflowEditor();
      }
      else if(node instanceof String)
      {
        panel = new GenericEditor();
      }
    }

    if(panel != null)
    {
      if(node instanceof String)
      {
        ((GenericEditor)panel).setLabel((String)node);
        panel.setName(panelName);
        detailFramePanel.setTitle(ResourceManager.getString("details") + (title != null ? (" - " + title) : ""));
        detailPanel.showCard(panel);
        return;
      }
      descriptor = (AbstractDescriptor)node;
    }

    if(panel != null)
    {
      WorkflowGraph currentGraph = graphTabs.getCurrentGraph();
      panel.setModel(currentGraph.getWorkflowGraphModel());
      panel.setGraph(currentGraph);
      panel.setDescriptor(descriptor);

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
      String oldWorkspace = Prefs.INSTANCE.get(Prefs.LAST_WORKSPACE, null);
      try
      {
        graphTabs.removeAll();
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, file.toString());
        manager.loadWorkspace(file);
        AbstractWorkflowFactory workspace = manager.getCurrentWorkspace();
        navigator.setWorkspace(workspace);
        String[] workflows = workspace.getWorkflowNames();
        for(int i = 0; i < workflows.length; i++)
        {
          createGraph(workflows[i]);
        }
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, file.toString());
      }
      catch(Exception t)
      {
        Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, oldWorkspace);
        t.printStackTrace();
      }
    }
  }

  public void openRemoteWorkspace()
  {
    try
    {
      manager.loadServiceWorkspace(service);
      RemoteWorkspace workspace = (RemoteWorkspace)manager.getCurrentWorkspace();
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

  public void checkWorkspaceExists()
  {
    if(manager.getCurrentWorkspace() == null)
    {
      NewWorkspaceDialog newSpace = new NewWorkspaceDialog(this, ResourceManager.getString("workspace.new"), true);
      newSpace.pack();
      newSpace.getBanner().setTitle("");
      newSpace.getBanner().setSubtitle(ResourceManager.getString("workspace.new.long"));
      Utils.centerComponent(this, newSpace);
      Dimension size = newSpace.getSize();
      newSpace.setSize(size.width + 10, size.height + 15);
      newSpace.show();
    }
  }

  private boolean save(WorkflowGraph graph, boolean validate)
  {
    boolean saved = false;

    Layout layout = graph.getGraphLayout();
    WorkflowGraphModel model = (WorkflowGraphModel)graph.getModel();
    layout.setAllEntries(model.getActivitiesList());
    String workflowName = graph.getName();
    manager.getCurrentWorkspace().setLayout(workflowName, layout);
    WorkflowDescriptor descriptor = null;
    try
    {
      descriptor = manager.getCurrentWorkspace().getWorkflow(workflowName);
      if(validate)
      {
        descriptor.validate();
      }
      if(manager.getCurrentWorkspace() instanceof Workspace)
        saved = ((Workspace)manager.getCurrentWorkspace()).saveWorkflow(workflowName, descriptor, graph, true);
      else if(manager.getCurrentWorkspace() instanceof RemoteWorkspace)
        saved = ((RemoteWorkspace)manager.getCurrentWorkspace()).saveWorkflow(workflowName, descriptor, graph, true);
      if(!saved)
      {
        JOptionPane.showMessageDialog(this, "Error", ResourceManager.getString("error.save.workflow.long", new Object[]{workflowName}), JOptionPane.ERROR_MESSAGE);
      }
    }
    catch(InvalidWorkflowDescriptorException e)
    {
      System.out.println("Error saving workflow: " + e);
      PrintWriter out = new PrintWriter(System.out);
      descriptor.writeXML(out, 0);
      out.flush();
      JOptionPane.showMessageDialog(this,
                                    ResourceManager.getString("error.validate.workflow", new Object[]{e.getMessage()}),
                                    ResourceManager.getString("title.validate.workflow", new Object[]{workflowName}),
                                    JOptionPane.ERROR_MESSAGE);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, e.getMessage(), ResourceManager.getString("error.save.workflow.long", new Object[]{workflowName}), JOptionPane.ERROR_MESSAGE);
    }
    return saved;
  }

  public void saveOpenGraphs()
  {
    WorkflowGraph[] graphs = graphTabs.getGraphs();
    for(int i = 0; i < graphs.length; i++)
    {
      save(graphs[i], false);
    }
  }

  public void saveWorkspace()
  {
    manager.saveWorkspace();
  }

  public Workspace newLocalWorkspace()
  {
    closeWorkspace();
    Workspace workspace = new Workspace();
    manager.setCurrentWorkspace(workspace);
    navigator.setWorkspace(workspace);
    return workspace;
  }

  public RemoteWorkspace newRemoteWorkspace()
  {
    closeWorkspace();
    RemoteWorkspace workspace = new RemoteWorkspace(service);
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

  public WorkspaceNavigator navigator()
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

  public void selectCell(AbstractDescriptor descriptor)
  {
    WorkflowGraph graph = getCurrentGraph();
    graph.getSelectionModel().clearSelection();
    WorkflowGraphModel model = (WorkflowGraphModel)graph.getModel();
    if(descriptor instanceof StepDescriptor)
    {
      StepCell cell = model.getStepCell(descriptor.getId());
      if(cell != null)
      {
        graph.getSelectionModel().setSelectionCell(cell);
      }
    }
    else if(descriptor instanceof SplitDescriptor)
    {
      SplitCell cell = model.getSplitCell(descriptor.getId());
      if(cell != null)
      {
        graph.getSelectionModel().setSelectionCell(cell);
      }
    }
    else if(descriptor instanceof JoinDescriptor)
    {
      JoinCell cell = model.getJoinCell(descriptor.getId());
      if(cell != null)
      {
        graph.getSelectionModel().setSelectionCell(cell);
      }
    }
    else if(descriptor instanceof ResultDescriptor)
    {
      ResultEdge edge = model.getResultCell((ResultDescriptor)descriptor);
      if(edge != null)
      {
        graph.getSelectionModel().setSelectionCell(edge);
      }
    }
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

  private AbstractDescriptor getCellDescriptor(Object cell)
  {
    if(cell instanceof StepCell)
    {
      return ((StepCell)cell).getDescriptor();
    }
    else if(cell instanceof JoinCell)
    {
      return ((JoinCell)cell).getJoinDescriptor();
    }
    else if(cell instanceof SplitCell)
    {
      return ((SplitCell)cell).getSplitDescriptor();
    }
    else if(cell instanceof InitialActionCell)
    {
      return ((InitialActionCell)cell).getActionDescriptor();
    }
    else if(cell instanceof ResultEdge)
    {
      return ((ResultEdge)cell).getDescriptor();
    }
    return null;
  }

  private String getDescriptorTitle(Object desc)
  {
    String title = "";
    if(desc instanceof StepDescriptor)
    {
      title = ((StepDescriptor)desc).getName();
    }
    else if(desc instanceof SplitDescriptor)
    {
      title = "Split #" + ((SplitDescriptor)desc).getId();
    }
    else if(desc instanceof JoinDescriptor)
    {
      title = "Join #" + ((JoinDescriptor)desc).getId();
    }
    else if(desc instanceof ResultDescriptor)
    {
      title = ((ResultDescriptor)desc).getDisplayName();
    }
    else if(desc instanceof ActionDescriptor)
    {
      title = ((ActionDescriptor)desc).getName();
    }
    else if(desc instanceof WorkflowDescriptor)
    {
      title = ((WorkflowDescriptor)desc).getName();
    }
    return title;
  }
}
