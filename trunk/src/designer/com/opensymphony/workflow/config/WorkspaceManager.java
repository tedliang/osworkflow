package com.opensymphony.workflow.config;

import java.io.*;
import java.util.*;

import com.opensymphony.workflow.config.ConfigLoader;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.loader.Workspace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 15, 2003
 * Time: 7:57:28 PM
 */
public class WorkspaceManager
{
  private static final Log log = LogFactory.getLog(WorkspaceManager.class);

  private Workspace currentWorkspace = null;
  private Collection listeners;

  public void loadWorkspace(File file) throws FactoryException, FileNotFoundException
  {
    fireWorkspaceClosed();
    saveWorkspace();
    log.debug("loading " + file);
    ConfigLoader.load(new FileInputStream(file));
    currentWorkspace  = (Workspace)ConfigLoader.getFactory();
    fireWorkspaceOpened();
    log.debug("loaded workspace " + currentWorkspace);
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

  public void setCurrentWorkspace(Workspace current)
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

  public Workspace getCurrentWorkspace()
  {
    return currentWorkspace;
  }
}
