package com.opensymphony.workflow.designer;

import com.opensymphony.workflow.loader.ResultDescriptor;

/**
 * @author hani
 * Date: Nov 28, 2003
 * Time: 5:14:27 PM
 */
public interface ResultAware
{
  public boolean removeResult(ResultDescriptor result);
}
