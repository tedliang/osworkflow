package com.opensymphony.workflow.config;

import java.io.*;
import java.util.*;
import java.net.URL;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.loader.RemoteWorkspace;
import com.opensymphony.workflow.loader.WorkflowFactory;
import com.opensymphony.workflow.designer.DesignerService;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 7:57:28 PM
 */
public class WorkspaceManager
{
  //private Workspace currentWorkspace = null;
  private WorkflowFactory currentWorkspace = null;
  private Collection listeners;

  public void loadWorkspace(URL url) throws FactoryException, IOException
  {
    fireWorkspaceClosed();
    saveWorkspace();
    DefaultConfiguration config = new DefaultConfiguration();
    config.load(url);
    currentWorkspace  = config.getFactory();
    fireWorkspaceOpened();
  }
	
	public void loadServiceWorkspace(DesignerService service) throws FactoryException, IOException
	{
		fireWorkspaceClosed();
		saveWorkspace();
		currentWorkspace  = new RemoteWorkspace(service);
		currentWorkspace.initDone(); 
		
    fireWorkspaceOpened();
  }

  public void addWorkspaceListener(WorkspaceListener listener)
  {
    if(listeners==null)
    {
      listeners = new HashSet();
    }
    listeners.add(listener);
  }

  public void removeWorkspaceListener(WorkspaceListener listener)
  {
    if(listeners==null) return;
    listeners.remove(listener);
  }

  protected void fireWorkspaceOpened()
  {
    if(listeners==null) return;
    WorkspaceEvent event = new WorkspaceEvent(this, currentWorkspace, WorkspaceEvent.WORKSPACE_OPENED);
    notifyListeners(event);
  }

  protected void fireWorkspaceClosed()
  {
    if(listeners==null) return;
    WorkspaceEvent event = new WorkspaceEvent(this, currentWorkspace, WorkspaceEvent.WORKSPACE_CLOSED);
    notifyListeners(event);
  }

  private void notifyListeners(WorkspaceEvent event)
  {
    Iterator iter = listeners.iterator();
    while(iter.hasNext())
    {
      WorkspaceListener listener = (WorkspaceListener)iter.next();
      listener.workspaceChanged(event);
      if(event.isConsumed()) return;
    }
  }

  public void setCurrentWorkspace(WorkflowFactory current)
  {
    saveWorkspace();
    if(this.currentWorkspace!=null)
      fireWorkspaceClosed();
    currentWorkspace = current;
    if(currentWorkspace!=null)
      fireWorkspaceOpened();
  }

  public void saveWorkspace()
  {
    if(currentWorkspace!=null)
    {
      currentWorkspace.save();
    }
  }

  public WorkflowFactory getCurrentWorkspace()
  {
    return currentWorkspace;
  }
}
