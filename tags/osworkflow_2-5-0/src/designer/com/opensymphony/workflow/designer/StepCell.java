/**
 * Created on Feb 3, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import com.opensymphony.workflow.loader.StepDescriptor;

/**
 * @author apatel
 */
public class StepCell extends WorkflowCell implements Keyable
{
  private StepDescriptor descriptor;

  // Construct Cell for Userobject
  public StepCell(StepDescriptor userObject)
  {
    super(userObject.getName());
    descriptor = userObject;
    id = descriptor.getId();
    name = descriptor.getName();
  }

  public StepCell(int id, String name)
  {
    super(name);
    this.id = id;
    this.name = name;
  }

  public String getKey()
  {
    String myClassName = StepCell.class.toString();
    return (myClassName + id);
  }

  public String toString()
  {
    return descriptor.getName() + " " + descriptor.getId();

  }

  public String getName()
  {
    return name;
  }

  public StepDescriptor getDescriptor()
  {
    return descriptor;
  }

  /**
   * If Key values are equal the objects are equal
   */
  public boolean equals(Object obj)
  {
    boolean returnVal = false;
    if(obj instanceof StepCell)
    {
      if(obj != null)
      {
        StepCell recObj = (StepCell)obj;
        if(getKey().equals(recObj.getKey()))
        {
          returnVal = true;
        }
      }
    }
    return returnVal;
  }

}
