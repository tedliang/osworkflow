package com.opensymphony.workflow.designer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.workflow.loader.ResultDescriptor;

public class ResultCellCollection extends ArrayList
{
  public List getResultsToStep(int stepId)
  {
    Iterator results = iterator();
    List returnValue = new ArrayList();
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
    Iterator results = iterator();
    List returnValue = new ArrayList();
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
    Iterator results = iterator();
    List returnValue = new ArrayList();
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

  public ResultCell getResultCell(ResultDescriptor result)
  {
    ResultCell ret = null;

    Iterator iter = iterator();
    while(iter.hasNext())
    {
      ret = (ResultCell)iter.next();
      if(ret.getDescriptor() == result)
      {
        return ret;
      }
    }

    return null;
  }
}
