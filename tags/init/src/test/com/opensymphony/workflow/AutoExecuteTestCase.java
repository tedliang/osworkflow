package com.opensymphony.workflow;

import java.util.*;

import junit.framework.TestCase;
import com.opensymphony.workflow.spi.Step;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 9, 2003
 * Time: 10:26:48 PM
 */
public class AutoExecuteTestCase extends TestCase {

    private AbstractWorkflow workflow;
    //string used by propertyset-create.xml
    public String myvar;

    public AutoExecuteTestCase(String s) {
        super(s);
    }

  protected void setUp() throws Exception
  {
    TestWorkflow.configFile = "osworkflow.xml";
    workflow = new TestWorkflow("testuser");
  }

    public void testSimpleAuto() throws Exception {
     long id = workflow.initialize("file:auto1.xml", 1, new HashMap());
     List currentSteps = workflow.getCurrentSteps(id);
      assertEquals("Unexpected number of current steps", 1, currentSteps.size());
      assertEquals("Unexpected current step", 2, ((Step)currentSteps.get(0)).getStepId());
    }

  public void testExecOnlyOne() throws Exception {
   long id = workflow.initialize("file:auto2.xml", 1, new HashMap());
   List currentSteps = workflow.getCurrentSteps(id);
    assertEquals("Unexpected number of current steps", 1, currentSteps.size());
    assertEquals("Unexpected current step", 4, ((Step)currentSteps.get(0)).getStepId());
    List history = workflow.getHistorySteps(id);
    assertEquals("Expected to have one history step", 1, history.size());
    assertEquals("Unexpected history step", 2, ((Step)history.get(0)).getStepId());
  }

  public void testExecTwoActions() throws Exception {
   long id = workflow.initialize("file:auto3.xml", 1, new HashMap());
   List currentSteps = workflow.getCurrentSteps(id);
    assertEquals("Unexpected number of current steps", 1, currentSteps.size());
    assertEquals("Unexpected current step", 3, ((Step)currentSteps.get(0)).getStepId());
    List history = workflow.getHistorySteps(id);
    assertEquals("Expected to have two history steps", 2, history.size());
    assertEquals("Unexpected first history step", 1, ((Step)history.get(0)).getStepId());
    assertEquals("Unexpected second history step", 2, ((Step)history.get(1)).getStepId());
  }

  public void testConditionCheck() throws Exception {
    long id = workflow.initialize("file:auto4.xml", 1, new HashMap());
    List currentSteps = workflow.getCurrentSteps(id);
     assertEquals("Unexpected number of current steps", 1, currentSteps.size());
     assertEquals("Unexpected current step", 3, ((Step)currentSteps.get(0)).getStepId());
  }

  public void testPropertySetCreated() throws Exception {
    Map inputs = new HashMap();
    List list = new ArrayList();
    inputs.put("list", list);
    long id = workflow.initialize("file:propertyset-create.xml", 1, inputs);
    String value = (String)list.get(0);
    assertEquals("Unexpected property set value for key myvar", "anything", value);
    List currentSteps = workflow.getCurrentSteps(id);
     assertEquals("Unexpected number of current steps", 1, currentSteps.size());
     assertEquals("Unexpected current step", 1, ((Step)currentSteps.get(0)).getStepId());
  }
}