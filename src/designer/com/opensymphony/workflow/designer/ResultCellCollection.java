/**
 * Created on Feb 12, 2003
 * Copyright (C) 2002  Aditisoft Inc
 */
package com.opensymphony.workflow.designer;

import java.util.*;

public class ResultCellCollection extends HashMap
{

  public String getNextKey()
  {
    return Integer.toString(size());
  }

  public List getStepEndPointResults(int stepId)
  {
    Iterator results = values().iterator();
    Vector returnValue = new Vector();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      if(stepId == result.getStep())
      {
        returnValue.add(result);
      }
    }
    return returnValue;
  }

  public List getSplitEndPointResults(int splitId)
  {
    Iterator results = values().iterator();
    Vector returnValue = new Vector();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      if(splitId == result.getSplit())
      {
        returnValue.add(result);
      }
    }
    return returnValue;
  }

  public List getJoinEndPointResults(int joinId)
  {
    Iterator results = values().iterator();
    Vector returnValue = new Vector();
    while(results.hasNext())
    {
      ResultCell result = (ResultCell)results.next();
      if(joinId == result.getJoin())
      {
        returnValue.add(result);
      }
    }
    return returnValue;
  }

}
