package com.opensymphony.workflow.loader;

import java.io.*;
import java.util.*;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.Layout;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.WorkflowGraph;
import com.opensymphony.workflow.designer.DesignerService; 

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 7:56:36 PM
 */
public class RemoteWorkspace extends HTTPWorkflowFactory
{
  private Map layouts = new HashMap();
	private DesignerService service = null;
	  
  public RemoteWorkspace(DesignerService service)
  {
    this.service = service;
    workflows = new HashMap();
  }

  public boolean isModifiable(String name)
  {
    return true;
  }

  public void initDone() throws FactoryException
  {
    super.initDone();
    
		Map flows = service.getWorkflows(); 
		Iterator iter = flows.entrySet().iterator();   
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			HTTPWorkflowConfig config = new HTTPWorkflowConfig(service.getRemoteAddress(), entry.getValue().toString(), entry.getKey().toString());
			workflows.put(entry.getValue(), config);
		} 
		
		//	now read in all layouts
		iter = workflows.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			String workflowName = entry.getKey().toString();
			HTTPWorkflowConfig config = (HTTPWorkflowConfig)entry.getValue();
			String docId = config.docId;   
			layouts.put(workflowName, docId);
		}    
  }
  
  public String getName()
  {
    return service.getWorkspaceName();
  }

  public boolean isNew()
  {
    //return workflowsXML==null;
    return false;
  }

  public void importDescriptor(String name, InputStream is)
  {
    /*
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
      //can't really happen
      e.printStackTrace();
    }
    */
  }

  public Object getLayout(String workflowName)
  {
    Object obj = layouts.get(workflowName);
    if(obj==null) 
    	return null;
    if(obj instanceof Layout)
      return (Layout)obj;
    String docId = obj.toString();
    try
    {
      String layBuffer = readLayoutBuffer(service.getRemoteAddress(), docId);
      if(layBuffer!=null)
      {
        Layout layout = new Layout(layBuffer);
        layouts.put(workflowName, layout);
        return layout;
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public void save()
  {
  }

  public boolean removeWorkflow(String name) throws FactoryException
  {
    HTTPWorkflowConfig removed = (HTTPWorkflowConfig)workflows.remove(name);
    save();
    return removed != null;
	}

  public WorkflowDescriptor getWorkflow(String name, boolean validate) throws FactoryException
  {
    HTTPWorkflowConfig config = (HTTPWorkflowConfig)workflows.get(name);
    if(config==null) 
    	return null;
    
    if (config.descriptor==null)
    {
			String docId = config.docId; 
			try
			{
				String workflowBuffer = readWorkflowBuffer(service.getRemoteAddress(), docId);
				if (workflowBuffer!=null)
				{
					config.descriptor = WorkflowLoader.load(new ByteArrayInputStream(workflowBuffer.getBytes()), validate);
					if (config.descriptor!=null)
					{
						/*
						String metaName = (String)config.descriptor.getMetaAttributes().get("descr"); 
						if ((metaName!=null)&&(metaName.length()>0))
							name = metaName;
						*/
						config.descriptor.setName(name);
					}
					return config.descriptor;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
    }
    return config.descriptor;
  }

  public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, WorkflowGraph graph, boolean replace) throws Exception
  {
    String layoutBuffer = "";
    String workflowBuffer;
    
    Object obj = layouts.get(name);
    if(obj instanceof Layout)
    {
      Layout layout = (Layout)obj;
      try
      {
        HTTPWorkflowConfig config = (HTTPWorkflowConfig)workflows.get(name);
        
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(new BufferedWriter(sw));
				//PrintWriter out = new PrintWriter(new ByteArrayOutputStream());
        layout.writeXML(out, 0, graph);
        out.flush();
        out.close();
        layoutBuffer = sw.getBuffer().toString(); 
       	//layoutBuffer = out.toString(); 
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return false;
      }
    }
    if(descriptor!=null)
    {
      HTTPWorkflowConfig config = (HTTPWorkflowConfig)workflows.get(name);
      descriptor.getMetaAttributes().put("generator", "OSWorkflow Designer");
      descriptor.getMetaAttributes().put("lastModified", (new Date()).toString());
      StringWriter sw = new StringWriter();
      PrintWriter writer = new PrintWriter(new BufferedWriter(sw));
      writer.println(WorkflowDescriptor.XML_HEADER);
      writer.println(WorkflowDescriptor.DOCTYPE_DECL);
      descriptor.writeXML(writer, 0);
      writer.flush();
      writer.close();
      //workflowBuffer = writer.toString();
      workflowBuffer = sw.getBuffer().toString();
      String ret = writeWorkflowDescriptor(service.getRemoteAddress(), config.docId, name, workflowBuffer);
      System.out.println("workflow ret = " + ret);
      if (ret.length() == 0)
        return false;
      ret = writeWorkflowLayout(service.getRemoteAddress(), config.docId, name, layoutBuffer);
      System.out.println("layout ret = " + ret);
      if (ret.length() == 0)
        return false;
      config.docId = ret;		// set the new document ID
      return true;
		}
    return false;
  }

  public void setLayout(String workflowName, Object layout)
  {
    layouts.put(workflowName, layout);
  }

	/*
  public String getLocation()
  {
    //return workflowsXML;
    return null;
  }

  public void setLocation(String service)
  {
    this.workflowsXML = new File(file.getParentFile(), "workflows.xml");
    this.configFile = file; 
  }
	*/

  public void createWorkflow(String name)
  {
    HTTPWorkflowConfig config = new HTTPWorkflowConfig(service.getRemoteAddress(), name, "");
    config.descriptor = new WorkflowDescriptor();
    config.descriptor.setName(name);
    ActionDescriptor initialAction = new ActionDescriptor();
    initialAction.setName(ResourceManager.getString("action.initial.start"));
    config.descriptor.getInitialActions().add(initialAction);
	  config.descriptor.getMetaAttributes().put("created", (new Date()).toString());
    workflows.put(name, config);  
  }

	public void renameWorkflow(String oldName, String newName)
	{
		//todo need to keep track of deleted workflows and delete their files on save
		HTTPWorkflowConfig config = (HTTPWorkflowConfig)workflows.get(oldName);
		config.name = newName;
		workflows.remove(oldName);
		workflows.put(newName, config);		
	}
}
