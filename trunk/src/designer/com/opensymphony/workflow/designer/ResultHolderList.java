package com.opensymphony.workflow.designer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.workflow.loader.ResultDescriptor;

public class ResultHolderList extends ArrayList
{
  public List getResultsToStep(int stepId)
  {
    Iterator results = iterator();
    List returnValue = new ArrayList();
    while(results.hasNext())
    {
      ResultHolder result = (ResultHolder)results.next();
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
      ResultHolder result = (ResultHolder)results.next();
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
      ResultHolder result = (ResultHolder)results.next();
      if(joinId == result.getJoin())
      {
        returnValue.add(result);
      }
    }
    return returnValue;
  }

  public ResultHolder getResultCell(ResultDescriptor result)
  {
    ResultHolder ret = null;

    Iterator iter = iterator();
    while(iter.hasNext())
    {
      ret = (ResultHolder)iter.next();
      if(ret.getDescriptor() == result)
      {
        return ret;
      }
    }

    return null;
  }
}
