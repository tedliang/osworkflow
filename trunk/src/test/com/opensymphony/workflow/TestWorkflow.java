package com.opensymphony.workflow;

import java.net.URL;
import java.io.*;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.ConfigLoader;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 1:36:21 PM
 */
public class TestWorkflow extends BasicWorkflow
{
  public static String configFile = "/osworkflow.xml";

  public TestWorkflow(String caller)
  {
    super(caller);
  }

  protected void loadConfig(URL url) throws FactoryException {
      if (url == null) {
            File file = new File(configFile);
              ConfigLoader.load(getClass().getResourceAsStream(configFile));
      }
  }

}
