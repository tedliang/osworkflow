/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;

import junit.framework.TestCase;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test that when an auto execute action happens, the correct actions occur.
 *
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 9, 2003
 * Time: 10:26:48 PM
 */
public class ActionsTestCase extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    //string used by propertyset-create.xml
    public String myvar;
    private AbstractWorkflow workflow;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ActionsTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testAutoWithSplit() throws Exception {
        URL url = getClass().getResource("/samples/auto-split.xml");
        long id = workflow.initialize(url.toString(), 1, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());
    }

    public void testConditionCheck() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/auto4.xml").toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 3, ((Step) currentSteps.get(0)).getStepId());
    }

    public void testExecOnlyOne() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/auto2.xml").toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 4, ((Step) currentSteps.get(0)).getStepId());

        List history = workflow.getHistorySteps(id);
        assertEquals("Expected to have one history step", 1, history.size());
        assertEquals("Unexpected history step", 2, ((Step) history.get(0)).getStepId());
    }

    public void testExecTwoActions() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/auto3.xml").toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 3, ((Step) currentSteps.get(0)).getStepId());

        List history = workflow.getHistorySteps(id);
        assertEquals("Expected to have two history steps", 2, history.size());
        assertEquals("Unexpected first history step", 2, ((Step) history.get(0)).getStepId());
        assertEquals("Unexpected second history step", 1, ((Step) history.get(1)).getStepId());
    }

    public void testPropertySetCreated() throws Exception {
        Map inputs = new HashMap();
        List list = new ArrayList();
        inputs.put("list", list);

        long id = workflow.initialize(getClass().getResource("/samples/propertyset-create.xml").toString(), 1, inputs);
        String value = (String) list.get(0);
        assertEquals("Unexpected property set value for key myvar", "anything", value);

        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, ((Step) currentSteps.get(0)).getStepId());
    }

    public void testSimpleAuto() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/auto1.xml").toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 2, ((Step) currentSteps.get(0)).getStepId());
    }

    public void testSimpleFinish() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/finish1.xml").toString(), 100, null);
        workflow.doAction(id, 1, null);
        assertTrue("Finished workflow should have no current steps", workflow.getCurrentSteps(id).size() == 0);
        assertEquals("Unexpected workflow entry state", WorkflowEntry.COMPLETED, workflow.getEntryState(id));

        List historySteps = workflow.getHistorySteps(id);

        //last history step should have status of LastFinished
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
    }

    public void testSimpleFinishWithoutRestriction() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/finish2.xml").toString(), 100, null);
        workflow.doAction(id, 1, null);
        assertTrue("Finished workflow should have no current steps", workflow.getCurrentSteps(id).size() == 0);
        assertEquals("Unexpected workflow entry state", WorkflowEntry.COMPLETED, workflow.getEntryState(id));

        List historySteps = workflow.getHistorySteps(id);

        //last history step should have status of LastFinished
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow("testuser");
    }
}
