package com.opensymphony.workflow.designer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author acapitani
 */
public class DesignerService
{
  protected String verb;
  protected String address;
  protected String workspace;
  protected Map workflows = new HashMap();

  private static final int MAX_WORKFLOWS = 30;

  public DesignerService()
  {
    verb = System.getProperty("com.opensymphony.workflow.jws.verb");
    if("new".equals(verb))
    {
      address = System.getProperty("com.opensymphony.workflow.jws.service");
      workspace = System.getProperty("com.opensymphony.workflow.jws.workspace");
      if(address == null)
      {
        // not a valid web service name!
        verb = null;
      }
    }
    else if("modify".equals(verb))
    {
      address = System.getProperty("com.opensymphony.workflow.jws.service");
      workspace = System.getProperty("com.opensymphony.workflow.jws.workspace");
      if(address != null)
      {
        for(int i = 1; i <= MAX_WORKFLOWS; i++)
        {
          String sId = System.getProperty("com.opensymphony.workflow.jws.id_" + i);
          if((sId == null) || (sId.length() == 0))
            break;
          String sName = System.getProperty("com.opensymphony.workflow.jws.name_" + i);
          if(sName == null)
            break;
          workflows.put(sId, sName);
        }
      }
      else
      {
        // not a valid web service
        verb = null;
      }
    }
  }

  public String getVerb()
  {
    return verb;
  }

  public String getRemoteAddress()
  {
    return address;
  }

  public String getWorkspaceName()
  {
    return workspace;
  }

  public Map getWorkflows()
  {
    return workflows;
  }

}
