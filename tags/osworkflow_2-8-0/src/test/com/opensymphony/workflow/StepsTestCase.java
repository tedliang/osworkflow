/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.spi.Step;

import junit.framework.TestCase;

import java.net.URL;

import java.util.*;


/**
 * User: Hani Suleiman
 * Date: Oct 14, 2003
 * Time: 3:02:08 PM
 */
public class StepsTestCase extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private AbstractWorkflow workflow;

    //~ Constructors ///////////////////////////////////////////////////////////

    public StepsTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testEarlyJoin() throws Exception {
        URL url = getClass().getResource("/samples/earlyjoin.xml");
        long id = workflow.initialize(url.toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());
        workflow.doAction(id, 1, Collections.EMPTY_MAP);

        //we end up in step 3, with everything finished
        List historySteps = workflow.getHistorySteps(id);
        assertEquals("Unexpected number of history steps", 3, historySteps.size());

        Step step = (Step) historySteps.get(0);
        assertEquals("Unexpected last history step", 3, step.getStepId());
    }

    public void testJoinNodesOrder() throws Exception {
        URL url = getClass().getResource("/samples/joinorder.xml");
        long id = workflow.initialize(url.toString(), 1, null);
        workflow.doAction(id, 2, null);
        workflow.doAction(id, 3, null);
        workflow.doAction(id, 2, null);
        workflow.doAction(id, 4, null);
        workflow.doAction(id, 6, null);
        workflow.doAction(id, 7, null);
    }

    public void testSplitCompletedHistorySteps() throws Exception {
        URL url = getClass().getResource("/samples/joinsplit.xml");
        long id = workflow.initialize(url.toString(), 100, null);
        List currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        workflow.doAction(id, 1, Collections.EMPTY_MAP);
        currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());

        List historySteps = workflow.getHistorySteps(id);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
        workflow.doAction(id, 2, Collections.EMPTY_MAP);

        //check that the same action is no longer available
        int[] actions = workflow.getAvailableActions(id, Collections.EMPTY_MAP);
        assertEquals("Unexpected number of actions available", 1, actions.length);
        historySteps = workflow.getHistorySteps(id);
        currentSteps = workflow.getCurrentSteps(id);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected number of history steps", 2, historySteps.size());
    }

    public void testStepPostFunction() throws Exception {
        URL url = getClass().getResource("/samples/step-post.xml");
        long id = workflow.initialize(url.toString(), 1, null);
        workflow.doAction(id, 2, null);
        assertTrue("post-function was not called as expected", "postvalue".equals(workflow.getPropertySet(id).getString("postkey")));
    }

    public void testStepPreFunction() throws Exception {
        URL url = getClass().getResource("/samples/step-pre.xml");
        long id = workflow.initialize(url.toString(), 1, null);
        assertTrue("pre-function was not called as expected", "prevalue".equals(workflow.getPropertySet(id).getString("prekey")));
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow("testuser");
    }
}
