package com.opensymphony.workflow.designer;

import java.util.Iterator;
import java.util.List;

import com.opensymphony.workflow.designer.proxy.StepProxy;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionalResultDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import org.jgraph.graph.GraphConstants;

public class StepCell extends WorkflowCell implements ResultAware
{
  private StepDescriptor descriptor;

  // Construct Cell for Userobject
  public StepCell(StepDescriptor userObject)
  {
    super(new StepProxy(userObject));
    GraphConstants.setEditable(attributes, true);
    descriptor = userObject;
    id = descriptor.getId();
    name = descriptor.getName();
  }

  public String toString()
  {
    return descriptor.getName();// + " " + descriptor.getId();
  }

  public String getName()
  {
    return descriptor.getName();
  }

  public StepDescriptor getDescriptor()
  {
    return descriptor;
  }

  public boolean removeResult(ResultDescriptor result)
  {
    Iterator iter = descriptor.getActions().iterator();
    while(iter.hasNext())
    {
      ActionDescriptor action = (ActionDescriptor)iter.next();
      if(result instanceof ConditionalResultDescriptor)
      {
        List list = action.getConditionalResults();
        if(list != null)
        {
          for(int i = 0; i < list.size(); i++)
          {
            if(list.get(i) == result)
            {
              list.remove(i);
              if(action.getUnconditionalResult()==null)
              {
                iter.remove();
              }
              return true;
            }
          }
        }
      }
      else
      {
        if(action.getUnconditionalResult() == result)
        {
          action.setUnconditionalResult(null);
          if(action.getConditionalResults().size()==0)
          {
            iter.remove();
          }
          return true;
        }
      }
    }
    return false;
  }
}
