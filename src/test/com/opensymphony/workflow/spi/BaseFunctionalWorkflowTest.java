/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import com.opensymphony.module.user.EntityNotFoundException;
import com.opensymphony.module.user.Group;
import com.opensymphony.module.user.User;
import com.opensymphony.module.user.UserManager;

import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.Expression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;

import junit.framework.TestCase;

import java.util.*;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public abstract class BaseFunctionalWorkflowTest extends TestCase {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String USER_TEST = "test";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected AbstractWorkflow workflow;
    protected WorkflowDescriptor workflowDescriptor;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BaseFunctionalWorkflowTest(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testExampleWorkflow() throws Exception {
        WorkflowQuery query;

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
        assertEquals(USER_TEST, historyStep.getCaller());
        assertNull(historyStep.getDueDate());

        // check system date, add in a 1 second fudgefactor.
        assertTrue("history step finish date " + historyStep.getFinishDate() + " is in the future!", (historyStep.getFinishDate().getTime() - 1000) < System.currentTimeMillis());
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

        query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);

        List workflows = workflow.query(query);
        assertEquals("Unexpected number of workflow query results", 1, workflows.size());
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(0, actions.length);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 7, historySteps.size());

        WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
        WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
        query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        workflows = workflow.query(query);
        assertEquals("Unexpected number of workflow query results", 1, workflows.size());
    }

    public void testWorkflowExpressionQuery() throws Exception {
        WorkflowExpressionQuery query = new WorkflowExpressionQuery(new Expression(Expression.OWNER, Expression.CURRENT_STEPS, Expression.EQUALS, USER_TEST));
        List workflows;

        String workflowName = getClass().getResource("/samples/example.xml").toString();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 1));

        workflows = workflow.query(query);
        assertEquals(0, workflows.size());

        long workflowId = workflow.initialize(workflowName, 1, new HashMap());
        workflows = workflow.query(query);
        assertEquals(1, workflows.size());

        Expression queryLeft = new Expression(Expression.OWNER, Expression.CURRENT_STEPS, Expression.EQUALS, USER_TEST);
        Expression queryRight = new Expression(Expression.STATUS, Expression.CURRENT_STEPS, Expression.EQUALS, "Finished");
        query = new WorkflowExpressionQuery(new Expression[] {
                    queryLeft, queryRight
                }, WorkflowExpressionQuery.AND);
        workflows = workflow.query(query);
        assertEquals(0, workflows.size());

        queryRight = new Expression(Expression.STATUS, Expression.CURRENT_STEPS, Expression.EQUALS, "Underway");
        query = new WorkflowExpressionQuery(new Expression[] {
                    queryLeft, queryRight
                }, WorkflowExpressionQuery.AND);
        workflows = workflow.query(query);
        assertEquals(1, workflows.size());

        //there should be one step that has been started
        workflows = workflow.query(new WorkflowExpressionQuery(new Expression(Expression.START_DATE, Expression.CURRENT_STEPS, Expression.LT, new Date())));
        assertEquals("Expected to find one workflow step that was started", 1, workflows.size());

        //there should be no steps that have been completed
        workflows = workflow.query(new WorkflowExpressionQuery(new Expression(Expression.FINISH_DATE, Expression.HISTORY_STEPS, Expression.LT, new Date())));
        assertEquals("Expected to find no history steps that were completed", 0, workflows.size());

        workflow.doAction(workflowId, 1, Collections.EMPTY_MAP);

        //there should be two step that have been started
        workflows = workflow.query(new WorkflowExpressionQuery(new Expression(Expression.START_DATE, Expression.HISTORY_STEPS, Expression.LT, new Date())));
        assertEquals("Expected to find 2 workflow steps that were started", 1, workflows.size());

        //there should be 1 steps that has been completed
        workflows = workflow.query(new WorkflowExpressionQuery(new Expression(Expression.FINISH_DATE, Expression.HISTORY_STEPS, Expression.LT, new Date())));
        assertEquals("Expected to find 1 history steps that was completed", 1, workflows.size());
    }

    public void testWorkflowQuery() throws Exception {
        WorkflowQuery query;
        List workflows;

        String workflowName = getClass().getResource("/samples/example.xml").toString();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 1));

        query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
        workflows = workflow.query(query);
        assertEquals(0, workflows.size());

        long workflowId = workflow.initialize(workflowName, 1, new HashMap());
        workflows = workflow.query(query);
        assertEquals(1, workflows.size());

        WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
        WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
        query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        workflows = workflow.query(query);
        assertEquals(0, workflows.size());

        queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
        query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        workflows = workflow.query(query);
        assertEquals(1, workflows.size());
    }

    protected void setUp() throws Exception {
        assertNotNull(TestWorkflow.configFile);
        workflow = new TestWorkflow(USER_TEST);

        UserManager um = UserManager.getInstance();

        try {
            um.getUser(USER_TEST);
        } catch (EntityNotFoundException enfe) {
            User test = um.createUser(USER_TEST);
            test.setPassword("test");

            Group foos = um.createGroup("foos");
            Group bars = um.createGroup("bars");
            Group bazs = um.createGroup("bazs");
            test.addToGroup(foos);
            test.addToGroup(bars);
            test.addToGroup(bazs);
        }
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
