package com.opensymphony.workflow;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class DoubleFunctionTestCase extends TestCase {

    private AbstractWorkflow workflow;
    public int counter = 0;

    public DoubleFunctionTestCase(String s) {
        super(s);
    }

  protected void setUp() throws Exception
  {
    TestWorkflow.configFile = "/osworkflow.xml";
    workflow = new TestWorkflow("testuser");
  }

    /**
     * Test if functions are executed once in an unconditional-result
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-130">Jira issue WF-118</a>
     * @throws Exception If error while executing testing
     */
    public void testDoubleFunctionExecution() throws Exception {
     long id = workflow.initialize(getClass().getClassLoader().getResource("/double.xml").toString(), 1, new HashMap());
      Map inputs = new HashMap();
      inputs.put("test", this);
      workflow.doAction(id, 3, inputs);
      assertEquals("function executed unexpected number of times", 2, counter);
    }
}
