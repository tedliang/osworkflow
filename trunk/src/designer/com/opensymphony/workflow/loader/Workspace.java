package com.opensymphony.workflow.loader;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.Layout;
import com.opensymphony.workflow.designer.Prefs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 7:56:36 PM
 */
public class Workspace extends XMLWorkflowFactory
{
  private static final Log log = LogFactory.getLog(Workspace.class);

  private Map layouts = new HashMap();
  private File workflowsXML;
  private File configFile;

  public Workspace()
  {
    workflows = new HashMap();
  }

  public void initDone() throws FactoryException
  {
    String name = getProperties().getProperty("resource", null);
    if(name!=null) name = name.replace('\\', '/');
    if(name!=null && name.indexOf('/')==-1)
    {
      //it's a relative path to resource, lets convert it
      configFile = new File(Prefs.INSTANCE.get(Prefs.LAST_WORKSPACE, null));
      try
      {
        workflowsXML = new File(configFile.getParentFile(), name);
        getProperties().setProperty("resource", workflowsXML.toURL().toString());
      }
      catch(MalformedURLException e)
      {
        e.printStackTrace();
      }
    }
    super.initDone();
    log.debug("workspace location=" + workflowsXML);
    String dir = workflowsXML.getParent();
    Iterator iter = workflows.values().iterator();
    while(iter.hasNext())
    {
      WorkflowConfig config = (WorkflowConfig)iter.next();
      if(config.location.indexOf('/')==-1 && config.location.indexOf('\\')==-1)
      {
        try
        {
          File file = new File(dir, config.location);
          config.url = file.toURL();
          if(file.exists())
            config.lastModified = file.lastModified();
        }
        catch(MalformedURLException e)
        {
          e.printStackTrace();
        }
      }
    }
    //now read in all layouts
    iter = workflows.entrySet().iterator();
    while(iter.hasNext())
    {
      Map.Entry entry = (Map.Entry)iter.next();
      String workflowName = entry.getKey().toString();
      WorkflowConfig config = (WorkflowConfig)entry.getValue();
      String layoutUrl = getLayoutURL(config, workflowName);
      layouts.put(workflowName, layoutUrl);
    }
  }

  private String getLayoutURL(XMLWorkflowFactory.WorkflowConfig config, String workflowName)
  {
    try
    {
      if(config.url==null) config.url = new File(workflowsXML.getParentFile(), workflowName + ".xml").toURL();
    }
    catch(MalformedURLException e)
    {
      e.printStackTrace();
    }
    String layoutUrl = config.url.toString();
    layoutUrl = layoutUrl.substring(0, layoutUrl.lastIndexOf('/')+1);
    layoutUrl = layoutUrl + workflowName + ".lyt";
    return layoutUrl;
  }

  public String getName()
  {
    return configFile!=null ? configFile.getName().substring(0, configFile.getName().lastIndexOf('.')) : "<new>";
  }

  public boolean isNew()
  {
    return workflowsXML==null;
  }

  public void importDescriptor(String name, InputStream is)
  {
    WorkflowConfig config = new WorkflowConfig("file", name + ".xml");
    try
    {
      File file = new File(workflowsXML.getParentFile(), name+".xml");
      config.url = file.toURL();
      config.lastModified = file.lastModified();
      workflows.put(name, config);
    }
    catch(MalformedURLException e)
    {
      //can't really happy
      log.error(e.getMessage(), e);
    }
  }

  public Layout getLayout(String workflowName)
  {
    Object obj = layouts.get(workflowName);
    if(obj==null) return null;
    if(obj instanceof Layout)
      return (Layout)obj;
    String url = obj.toString();
    try
    {
      InputStream is = new URL(url).openStream();
      if(is!=null)
      {
        Layout layout = new Layout(is);
        layout.setUrl(url);
        layouts.put(workflowName, layout);
        return layout;
      }
    }
    catch(FileNotFoundException ex)
    {
      //that's ok, no saved layout
    }
    catch(java.io.IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public void save()
  {
    if(workflowsXML==null)
    {
      log.warn("cannot save workspace, no location set");
      return;
    }
    try
    {
      if(!configFile.exists())
      {
        //this is a brand new workspace, so lets create the main config file
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));
        out.println("<osworkflow>");
        out.println("  <persistence class=\"com.opensymphony.workflow.spi.memory.MemoryWorkflowStore\"/>");
        out.println("  <factory class=\"com.opensymphony.workflow.loader.Workspace\">");
        out.println("    <property key=\"resource\" value=\"workflows.xml\" />");
        out.println("  </factory>");
        out.println("</osworkflow>");
        out.flush();
        out.close();
      }
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(workflowsXML)));
      out.println("<workflows>");
      Iterator iter = workflows.entrySet().iterator();
      while(iter.hasNext())
      {
        Map.Entry entry = (Map.Entry)iter.next();
        WorkflowConfig config = (WorkflowConfig)entry.getValue();
        if(config.location==null) config.location = entry.getKey() + ".xml";
        out.println("  <workflow name=\"" + entry.getKey() + "\" type=\"file\" location=\"" + config.location + "\" />");
      }
      out.println("</workflows>");
      out.flush();
      out.close();
      log.debug("wrote " + workflowsXML);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public WorkflowDescriptor getWorkflow(String name) throws FactoryException
  {
    WorkflowConfig config = (WorkflowConfig)workflows.get(name);
    if(config==null) return null;
    if(config.url ==null)
    {
      return config.descriptor;
    }
    return super.getWorkflow(name);
  }

  public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException
  {
    Object obj = layouts.get(name);
    if(obj instanceof Layout)
    {
      Layout layout = (Layout)obj;
      try
      {
        WorkflowConfig config = (WorkflowConfig)workflows.get(name);
        URL url = new URL(getLayoutURL(config, name));
        File file = new File(url.getFile());
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        layout.writeXML(out, 0);
        out.flush();
        out.close();
        if(config.location==null)
        {
          config.location = name + ".xml";
          config.url = new File(workflowsXML.getParentFile(), config.location).toURL();
        }
      }
      catch(IOException e)
      {
        e.printStackTrace();
        return false;
      }
    }
    if(descriptor!=null)
    {
      WorkflowConfig config = (WorkflowConfig)workflows.get(name);
      if(config.url!=null)
      {
        return super.saveWorkflow(name, descriptor, replace);
      }
      else
      {
        log.warn("*** saveWorkflow called with config.location=" + config.location + " url is null");
      }
    }
    return false;
  }

  public void setLayout(String workflowName, Layout layout)
  {
    layouts.put(workflowName, layout);
  }

  public File getLocation()
  {
    return workflowsXML;
  }

  public void setLocation(File file)
  {
    this.workflowsXML = new File(file.getParentFile(), "workflows.xml");
    this.configFile = file;
  }

  public void createWorkflow(String name)
  {
    WorkflowConfig config = new WorkflowConfig("file", null);
    config.descriptor = new WorkflowDescriptor();
    ActionDescriptor initialAction = new ActionDescriptor();
    initialAction.setId(1);
    config.descriptor.getInitialActions().add(initialAction);
    workflows.put(name, config);
  }
}
