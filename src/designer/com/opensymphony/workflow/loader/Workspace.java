package com.opensymphony.workflow.loader;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.Layout;
import com.opensymphony.workflow.designer.Prefs;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraph;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 7:56:36 PM
 */
public class Workspace extends XMLWorkflowFactory
{
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
	    try
	    {
		    configFile = new File(new URL(Prefs.INSTANCE.get(Prefs.LAST_WORKSPACE, null)).getFile());
	    }
	    catch(MalformedURLException e)
	    {
		    throw new FactoryException(e);
	    }
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
          {
            config.lastModified = file.lastModified();
          }
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
    WorkflowConfig config = new WorkflowConfig(null, "file", name + ".xml");
    try
    {
      File file = new File(workflowsXML.getParentFile(), name+".xml");
      config.url = file.toURL();
      config.lastModified = file.lastModified();
      workflows.put(name, config);
    }
    catch(MalformedURLException e)
    {
      //can't really happen
      e.printStackTrace();
    }
  }

  public String getWorkflowFile(String workflowName)
  {
    WorkflowConfig config = (WorkflowConfig)workflows.get(workflowName);
    if(config != null && config.url != null)
    {
      return config.url.getFile();
    }
    return null;
  }
  
  public Object getLayout(String workflowName)
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
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public boolean removeWorkflow(String name) throws FactoryException
  {
    WorkflowConfig removed = (WorkflowConfig)workflows.remove(name);
    if(removed == null) return false;
    save();
    if(removed.url != null && removed.url.getProtocol().equals("file"))
    {
      return new File(removed.url.getFile()).delete();
    }
    return true;
  }

  public WorkflowDescriptor getWorkflow(String name, boolean validate) throws FactoryException
  {
    WorkflowConfig config = (WorkflowConfig)workflows.get(name);
    if(config==null) return null;
    if(config.url ==null)
    {
      return config.descriptor;
    }
    try
    {
      return super.getWorkflow(name, validate);
    }
    catch(FactoryException e)
    {
      //something went wrong, so lets delete the workflow from our list
      workflows.remove(name);
      throw e;
    }
  }

  public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, WorkflowGraph graph, boolean replace) throws FactoryException
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
        layout.writeXML(out, 0, graph);
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
	      descriptor.getMetaAttributes().put("generator", "OSWorkflow Designer");
	      descriptor.getMetaAttributes().put("lastModified", (new Date()).toString());
        return super.saveWorkflow(name, descriptor, replace);
      }
      else
      {
        System.out.println("WARN: *** saveWorkflow called with config.location=" + config.location + " url is null");
      }
    }
    return false;
  }

  public void setLayout(String workflowName, Object layout)
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
    WorkflowConfig config = new WorkflowConfig(null, "file", null);
    config.descriptor = DescriptorFactory.getFactory().createWorkflowDescriptor();
    config.descriptor.setName(name);
    ActionDescriptor initialAction = DescriptorFactory.getFactory().createActionDescriptor();
    initialAction.setName(ResourceManager.getString("action.initial.start"));
    config.descriptor.getInitialActions().add(initialAction);
	  config.descriptor.getMetaAttributes().put("created", (new Date()).toString());
    workflows.put(name, config);
  }

	public void renameWorkflow(String oldName, String newName)
	{
		//todo need to keep track of deleted workflows and delete their files on save
		WorkflowConfig config = (WorkflowConfig)workflows.get(oldName);
		config.location = newName + ".xml";
		try
		{
			config.url = new File(workflowsXML.getParentFile(), config.location).toURL();
		}
		catch(MalformedURLException e)
		{
			//this can't ever happen
			e.printStackTrace();
		}
		workflows.remove(oldName);
		workflows.put(newName, config);
	}
}
