package com.opensymphony.workflow.designer;

import java.awt.Color;
import java.util.List;

import org.jgraph.graph.GraphConstants;

import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.SplitDescriptor;

public class SplitCell extends WorkflowCell implements ResultAware
{
  private SplitDescriptor descriptor;

  public SplitCell(SplitDescriptor userObject)
  {
    super(userObject);
    descriptor = userObject;
    id = descriptor.getId();
    GraphConstants.setBackground(attributes, Color.gray);
  }

  public SplitDescriptor getSplitDescriptor()
  {
    return descriptor;
  }

  public boolean removeResult(ResultDescriptor result)
  {
    List list = descriptor.getResults();
    if(list != null)
    {
      for(int i = 0; i < list.size(); i++)
      {
        if(list.get(i) == result)
        {
          list.remove(i);
          return true;
        }
      }
    }
    return false;
  }

  public String toString()
  {
    return "Split id " + descriptor.getId();
  }
}


