package com.opensymphony.workflow.designer;

import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;

/**
 * @author hani Date: Dec 30, 2004 Time: 2:50:15 PM
 */
public class WorkflowAttributeMap extends AttributeMap
{
  public WorkflowAttributeMap(Map map)
  {
    super(map);
  }

  public AttributeMap applyMap(Map change)
  {
    if(!change.containsKey(GraphConstants.VALUE)) return super.applyMap(change);
    Object userObject = get("descriptor");
    Object newValue = change.get(GraphConstants.VALUE);
    if(userObject instanceof StepDescriptor)
    {
      StepDescriptor descriptor = (StepDescriptor)userObject;
      //StepDescriptor user = (StepDescriptor)((StepDescriptor)userObject).clone();
      descriptor.setName(newValue.toString());
    }
    else if(userObject instanceof ResultDescriptor)
    {
      ResultDescriptor result = (ResultDescriptor)userObject;
      String name = result.getDisplayName();
      boolean hasNickname = (name != null);
      if(hasNickname) hasNickname &= (name.length() > 0);
      if((!hasNickname) && (result.getParent() instanceof ActionDescriptor))
      {
        if(((ActionDescriptor)result.getParent()).getConditionalResults() != null)
        {
          if(((ActionDescriptor)result.getParent()).getConditionalResults().isEmpty())
          {
            ((ActionDescriptor)result.getParent()).setName(newValue.toString());
          }
        }
      }
      result.setDisplayName(newValue.toString());
    }
    else if(userObject instanceof ActionDescriptor)
    {
      ActionDescriptor descriptor = (ActionDescriptor)userObject;
      //StepDescriptor user = (StepDescriptor)((StepDescriptor)userObject).clone();
      descriptor.setName(newValue.toString());
    }

    return super.applyMap(change);
  }
}
