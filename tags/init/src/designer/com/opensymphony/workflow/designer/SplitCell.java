package com.opensymphony.workflow.designer;

import java.awt.*;

import com.opensymphony.workflow.loader.SplitDescriptor;
import com.jgraph.graph.GraphConstants;

public class SplitCell extends WorkflowCell implements Keyable
{
  private SplitDescriptor descriptor;

  // Construct Cell for Userobject
  public SplitCell(SplitDescriptor userObject)
  {
    super("Split id " + userObject.getId());
    descriptor = userObject;
    id = descriptor.getId();
    GraphConstants.setBackground(attributes, Color.gray);
  }

  public SplitCell(int id)
  {
    super("Split id " + id);
    this.id = id;
  }

  public String getKey()
  {
    String myClassName = SplitCell.class.toString();
    return (myClassName + id);
  }

  public SplitDescriptor getSplitDescriptor()
  {
    return descriptor;
  }

}


