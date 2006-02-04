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

import java.util.Arrays;
import java.util.List;


/**
 * @author hani Date: Apr 24, 2004 Time: 12:35:01 PM
 */
public class CommonAndGlobalActionsTestCase extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Workflow workflow;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testBasicCommonAction() throws Exception {
        URL url = getClass().getResource("/samples/common-actions.xml");
        long id = workflow.initialize(url.toString(), 50, null);
        assertEquals("Unexpected workflow state", WorkflowEntry.ACTIVATED, workflow.getEntryState(id));

        //verify that a common action can be called
        workflow.doAction(id, 100, null);

        List historySteps = workflow.getHistorySteps(id);
        Step historyStep = (Step) historySteps.get(0);
        assertEquals("Unexpected old status set", "Restarted", historyStep.getStatus());

        //now let's move to step 2
        workflow.doAction(id, 1, null);

        //now let's check if we can call a non-specified common action
        try {
            workflow.doAction(id, 100, null);
            fail("Should not be able to call non-explicitly specified common-action");
        } catch (InvalidActionException e) {
        }

        //now test -1 stepId stuff.
        workflow.doAction(id, 101, null);
        historySteps = workflow.getHistorySteps(id);
        historyStep = (Step) historySteps.get(0);
        assertEquals("Unexpected old status set", "Finished", historyStep.getStatus());
    }

    public void testBasicGlobalAction() throws Exception {
        URL url = getClass().getResource("/samples/global-actions.xml");
        long id = workflow.initialize(url.toString(), 50, null);

        int[] availableActions = workflow.getAvailableActions(id, null);
        Object[] list = new Object[availableActions.length];

        for (int i = 0; i < availableActions.length; i++) {
            list[i] = new Integer(availableActions[i]);
        }

        assertTrue("Unexpected available actions " + Arrays.asList(list), Arrays.equals(new int[] {
                    100, 101, 1
                }, availableActions));

        //verify that a global action can be called
        workflow.doAction(id, 100, null);

        List historySteps = workflow.getHistorySteps(id);
        Step historyStep = (Step) historySteps.get(0);
        assertEquals("Unexpected old status set", "Restarted", historyStep.getStatus());

        //now let's move to step 2
        workflow.doAction(id, 1, null);

        //now test -1 stepId stuff.
        workflow.doAction(id, 101, null);
        historySteps = workflow.getHistorySteps(id);
        historyStep = (Step) historySteps.get(0);
        assertEquals("Unexpected old status set", "Finished", historyStep.getStatus());
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow("test");
    }
}
