package com.opensymphony.workflow;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 1:58:48 PM
 */
public class ExceptionTestCase extends TestCase
{
  public ExceptionTestCase(String s)
  {
    super(s);
  }

  public void testFactoryException()
  {
    TestWorkflow.configFile = "osworkflow-badfactory.xml";
    //we expect an InternalWorkflowException (can't throw a checked exception in constructor, otherwise the ejb provider
    //will break spec by having a constructor
    try
    {
      TestWorkflow workflow = new TestWorkflow("testuser");
      fail("bad factory did not throw an error");
    }
    catch(InternalWorkflowException ex)
    {
      assertTrue("Expected FactoryException, but instead got " + ex.getRootCause(), ex.getRootCause() instanceof FactoryException);
    }
  }

  public void testStoreException() throws Exception
  {
    TestWorkflow.configFile = "osworkflow-jdbc.xml";
    TestWorkflow workflow = new TestWorkflow("testuser");
    //correct behaviour is to get a store exception since we can't look up the DS
    try
    {
      workflow.initialize("file:auto1.xml", 1, new HashMap());
    }
    catch(StoreException e)
    {
    }
  }
}
