package com.opensymphony.workflow.designer.actions;

import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author Hani Suleiman
 *         Date: Mar 22, 2006
 *         Time: 8:59:05 PM
 */
public interface DescriptorAware
{
  public void setDescriptor(WorkflowDescriptor descriptor);
}
