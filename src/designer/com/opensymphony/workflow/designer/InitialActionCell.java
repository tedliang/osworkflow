package com.opensymphony.workflow.designer;

import java.awt.Color;
import java.util.List;

import org.jgraph.graph.GraphConstants;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionalResultDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;

public class InitialActionCell extends WorkflowCell implements ResultAware
{
  private ActionDescriptor action;

  public void setActionDescriptor(ActionDescriptor action)
  {
    this.action = action;
  }

  public ActionDescriptor getActionDescriptor()
  {
    return action;
  }

  public InitialActionCell(String userObject)
  {
    super(userObject);
    GraphConstants.setBackground(attributes, Color.red.darker());
    // GraphConstants.setBorder(attributes, BorderFactory.createEmptyBorder());
  }

  public boolean removeResult(ResultDescriptor result)
  {
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
            return true;
          }
        }
      }
      return false;
    }
    else
    {
      if(action.getUnconditionalResult() == result)
      {
        action.setUnconditionalResult(null);
        return true;
      }
      return false;
    }
  }
}

