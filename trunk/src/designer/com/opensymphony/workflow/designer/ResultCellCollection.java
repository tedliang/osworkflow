package com.opensymphony.workflow.designer;

import java.util.*;

public class ResultCellCollection extends HashMap
{

  public String getNextKey()
  {
    return Integer.toString(size());
  }

  public List getResultsToStep(int stepId)
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

  public List getResultsToSplit(int splitId)
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

  public List getResultsToJoin(int joinId)
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
