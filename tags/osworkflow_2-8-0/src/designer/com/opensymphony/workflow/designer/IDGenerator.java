package com.opensymphony.workflow.designer;

public class IDGenerator
{
  private int nextId;

  public int generateId()
  {
    return nextId++;
  }

  public void checkId(int id)
  {
    if(id>=nextId) nextId = id+1;
  }
}
