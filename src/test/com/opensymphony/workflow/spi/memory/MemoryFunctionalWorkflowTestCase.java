package com.opensymphony.workflow.spi.memory;

import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.spi.BaseFunctionalWorkflow;

/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.
 * 
 * @author Eric Pugh (epugh@upstate.com)
 */
public class MemoryFunctionalWorkflowTestCase extends BaseFunctionalWorkflow
{
    
    public MemoryFunctionalWorkflowTestCase(String s)
    {
        super(s);
    }
    protected void setUp() throws Exception
    {
        TestWorkflow.configFile = "/osworkflow.xml";
        workflow = new TestWorkflow("test");
    }
  
}
