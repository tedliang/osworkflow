package com.opensymphony.workflow.designer;

import java.util.*;

/**
 * @author acapitani
 */
public class DesignerService
{
  protected String verb;
  protected String service_addr;
  protected String workspace;
  protected Map workflows = new HashMap();

  private static final int MAX_WORKFLOWS = 64;

  public DesignerService(String[] args)
  {
    init();
    if(args != null)
    {
      // potrei caricare i parametri di servizio dagli argomenti
      // passati al main() del programma
      // TODO
    }
    else
    {
      // verifico le System Properties, magari passate via JNLP
      verb = System.getProperty("com.opensymphony.workflow.jws.verb");
      if(verb == null)
        verb = "none";
      else if(verb.equals("new"))
      {
        service_addr = System.getProperty("com.opensymphony.workflow.jws.service");
        if(service_addr == null)
          service_addr = "";
        workspace = System.getProperty("com.opensymphony.workflow.jws.workspace");
        if(workspace == null)
          workspace = "";
        if(service_addr.length() == 0)
        {
          // not a valida web service name!
          verb = "none";
        }
      }
      else if(verb.equals("modify"))
      {
        service_addr = System.getProperty("com.opensymphony.workflow.jws.service");
        if(service_addr == null)
          service_addr = "";
        workspace = System.getProperty("com.opensymphony.workflow.jws.workspace");
        if(workspace == null)
          workspace = "";
        if(service_addr.length() > 0)
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
          verb = "none";
        }
      }
    }
  }

  private void init()
  {
    verb = "none";
    service_addr = "";
    workspace = "";
  }

  public String getVerb()
  {
    return verb;
  }

  public String getServiceAddr()
  {
    return service_addr;
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
