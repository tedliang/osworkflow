/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import com.opensymphony.module.user.Group;
import com.opensymphony.module.user.User;
import com.opensymphony.module.user.UserManager;

import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public abstract class BaseFunctionalWorkflow extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    //string used by propertyset-create.xml
    public String myvar;
    protected AbstractWorkflow workflow;
    protected WorkflowDescriptor workflowDescriptor;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BaseFunctionalWorkflow(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void OFFtestInvalidRole() throws Exception {
        try {
            workflow.initialize(getClass().getResource("/samples/example.xml").toString(), 1, new HashMap());
            fail("Should have thrown an exception 'You are restricted from initializing this workflow'");
        } catch (InvalidRoleException ire) {
            //good
        }
    }

    public void testExampleWorkflow() throws Exception {
        UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");

        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);

        String workflowName = getClass().getResource("/samples/example.xml").toString();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 1));

        long workflowId = workflow.initialize(workflowName, 1, new HashMap());
        String workorderName = workflow.getWorkflowName(workflowId);
        workflowDescriptor = workflow.getWorkflowDescriptor(workorderName);
        log("Name of workorder:" + workorderName);
        assertTrue("Expected external-permission permA in step 1 not found", workflow.getSecurityPermissions(workflowId).contains("permA"));

        List currentSteps = workflow.getCurrentSteps(workflowId);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, ((Step) currentSteps.get(0)).getStepId());

        List historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 0, historySteps.size());
        log("Perform Finish First Draft");
        workflow.doAction(workflowId, 1, Collections.EMPTY_MAP);

        int[] actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(2, actions.length);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        Step historyStep = (Step) historySteps.get(0);
        assertEquals("test", historyStep.getCaller());
        assertNull(historyStep.getDueDate());
        assertTrue("history step finish date is in the future!", historyStep.getFinishDate().getTime() < System.currentTimeMillis());
        logActions(actions);
        log("Perform Finish Foo");
        workflow.doAction(workflowId, 12, Collections.EMPTY_MAP);

        Step lastHistoryStep = historyStep;
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
        historyStep = (Step) historySteps.get(0);
        assertEquals(lastHistoryStep.getId(), historyStep.getId());
        log("Perform Finish Bar");
        workflow.doAction(workflowId, 13, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(1, actions.length);
        logActions(actions);
        log("Perform Finish Baz");
        workflow.doAction(workflowId, 14, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        logActions(actions);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 4, historySteps.size());
        log("Perform Finish Editing");
        workflow.doAction(workflowId, 3, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(3, actions.length);
        logActions(actions);
        log("Perform Publish Doc");
        workflow.doAction(workflowId, 7, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(1, actions.length);
        logActions(actions);
        log("Perform Publish Document");
        workflow.doAction(workflowId, 11, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(0, actions.length);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 7, historySteps.size());
    }

    protected void setUp() throws Exception {
        TestWorkflow.configFile = "/osworkflow.xml";
        workflow = new TestWorkflow("test");
    }

    /**
     * Set this to log to System.out.println to see more details about what is happening in the
     * system.
     * @param s  the string to be logged.
     */
    protected void log(String s) {
        //System.out.println(s);
    }

    protected void logActions(int[] actions) {
        for (int i = 0; i < actions.length; i++) {
            String name = workflowDescriptor.getAction(actions[i]).getName();
            int actionId = workflowDescriptor.getAction(actions[i]).getId();
            log("Actions Available: " + name + " id:" + actionId);
        }
    }
}
