package com.opensymphony.workflow.loader;

import java.util.Map;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 27, 2003
 *         Time: 12:21:56 PM
 */
public interface ArgsAware
{
  public boolean isArgModifiable(String name);
  public Map getArgs();
}
