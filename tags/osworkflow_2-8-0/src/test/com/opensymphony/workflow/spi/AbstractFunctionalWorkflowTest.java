/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;

import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.QueryNotSupportedException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.Expression;
import com.opensymphony.workflow.query.FieldExpression;
import com.opensymphony.workflow.query.NestedExpression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public abstract class AbstractFunctionalWorkflowTest extends TestCase {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String USER_TEST = "test";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected Log log;
    protected Workflow workflow;
    protected WorkflowDescriptor workflowDescriptor;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AbstractFunctionalWorkflowTest(String s) {
        super(s);
        log = LogFactory.getLog(getClass());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testExampleWorkflow() throws Exception {
        WorkflowQuery query;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 100));

        long workflowId = workflow.initialize(workflowName, 100, new HashMap());
        String workorderName = workflow.getWorkflowName(workflowId);
        workflowDescriptor = workflow.getWorkflowDescriptor(workorderName);

        if (log.isDebugEnabled()) {
            log.debug("Name of workorder:" + workorderName);
        }

        assertTrue("Expected external-permission permA in step 1 not found", workflow.getSecurityPermissions(workflowId, null).contains("permA"));

        List currentSteps = workflow.getCurrentSteps(workflowId);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, ((Step) currentSteps.get(0)).getStepId());

        List historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 0, historySteps.size());

        if (log.isDebugEnabled()) {
            log.debug("Perform Finish First Draft");
        }

        workflow.doAction(workflowId, 1, Collections.EMPTY_MAP);

        int[] actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(3, actions.length);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        Step historyStep = (Step) historySteps.get(0);
        assertEquals(USER_TEST, historyStep.getCaller());
        assertNull(historyStep.getDueDate());

        // check system date, add in a 1 second fudgefactor.
        assertTrue("history step finish date " + historyStep.getFinishDate() + " is in the future!", (historyStep.getFinishDate().getTime() - 1000) < System.currentTimeMillis());
        logActions(actions);

        if (log.isDebugEnabled()) {
            log.debug("Perform Finish Foo");
        }

        workflow.doAction(workflowId, 12, Collections.EMPTY_MAP);

        //Step lastHistoryStep = historyStep;
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 2, historySteps.size());

        if (log.isDebugEnabled()) {
            log.debug("Perform Stay in Bar");
        }

        workflow.doAction(workflowId, 113, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(2, actions.length);
        assertTrue((actions[0] == 13) && (actions[1] == 113));
        logActions(actions);

        //historyStep = (Step) historySteps.get(0);
        //assertEquals(lastHistoryStep.getId(), historyStep.getId());
        if (log.isDebugEnabled()) {
            log.debug("Perform Finish Bar");
        }

        workflow.doAction(workflowId, 13, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(1, actions.length);
        logActions(actions);

        if (log.isDebugEnabled()) {
            log.debug("Perform Finish Baz");
        }

        workflow.doAction(workflowId, 14, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        logActions(actions);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 5, historySteps.size());

        if (log.isDebugEnabled()) {
            log.debug("Perform Finish Editing");
        }

        workflow.doAction(workflowId, 3, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(3, actions.length);
        logActions(actions);

        if (log.isDebugEnabled()) {
            log.debug("Perform Publish Doc");
        }

        workflow.doAction(workflowId, 7, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(1, actions.length);
        logActions(actions);

        if (log.isDebugEnabled()) {
            log.debug("Perform Publish Document");
        }

        workflow.doAction(workflowId, 11, Collections.EMPTY_MAP);

        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(0, actions.length);
        historySteps = workflow.getHistorySteps(workflowId);
        assertEquals("Unexpected number of history steps", 8, historySteps.size());

        query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);

        try {
            List workflows = workflow.query(query);
            assertEquals("Unexpected number of workflow query results", 1, workflows.size());

            WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
            WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
            query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
            workflows = workflow.query(query);
            assertEquals("Unexpected number of workflow query results", 1, workflows.size());
        } catch (QueryNotSupportedException ex) {
            System.out.println("query not supported");
        }
    }

    public void testExceptionOnIllegalStayInCurrentStep() throws Exception {
        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 100));

        try {
            long workflowId = workflow.initialize(workflowName, 200, new HashMap());
            fail("initial action result specified target step of current step. Succeeded but should not have.");
        } catch (WorkflowException e) {
            // expected, no such thing as current step for initial action
        }
    }

    public void testMetadataAccess() throws Exception {
        String workflowName = getWorkflowName();
        long workflowId = workflow.initialize(workflowName, 100, new HashMap());
        WorkflowDescriptor wfDesc = workflow.getWorkflowDescriptor(workflowName);

        Map meta = wfDesc.getMetaAttributes();
        assertTrue("missing metadata", (meta.get("workflow-meta1")).equals("workflow-meta1-value"));
        assertTrue("missing metadata", (meta.get("workflow-meta2")).equals("workflow-meta2-value"));

        meta = wfDesc.getStep(1).getMetaAttributes();
        assertTrue("missing metadata", (meta.get("step-meta1")).equals("step-meta1-value"));
        assertTrue("missing metadata", (meta.get("step-meta2")).equals("step-meta2-value"));

        meta = wfDesc.getAction(1).getMetaAttributes();
        assertTrue("missing metadata", (meta.get("action-meta1")).equals("action-meta1-value"));
        assertTrue("missing metadata", (meta.get("action-meta2")).equals("action-meta2-value"));
    }

    public void testWorkflowExpressionQuery() throws Exception {
        List workflows;
        WorkflowExpressionQuery query;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 100));

        //-------------------   FieldExpression.OWNER  +  FieldExpression.CURRENT_STEPS ----------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.OWNER, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, USER_TEST));

        try {
            workflows = workflow.query(query);
            assertEquals("empty OWNER+CURRENT_STEPS", 0, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.error("Store does not support query");

            return;
        }

        long workflowId = workflow.initialize(workflowName, 100, new HashMap());
        workflows = workflow.query(query);
        assertEquals("OWNER+CURRENT_STEPS", 1, workflows.size());

        //-------------------  FieldExpression.NAME + FieldExpression.ENTRY ----------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.NAME, FieldExpression.ENTRY, FieldExpression.EQUALS, "notexistingname"));
        workflows = workflow.query(query);
        assertEquals("empty NAME+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.NAME, FieldExpression.ENTRY, FieldExpression.EQUALS, workflowName));
        workflows = workflow.query(query);
        assertEquals("NAME+ENTRY", 1, workflows.size());

        //-------------------  FieldExpression.STATE + FieldExpression.ENTRY ----------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.STATE, FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.COMPLETED)));
        workflows = workflow.query(query);
        assertEquals("empty STATE+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.STATE, FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.ACTIVATED)));
        workflows = workflow.query(query);
        assertEquals("STATE+ENTRY", 1, workflows.size());

        // ---------------------------  empty nested query : AND ---------------------------------
        Expression queryLeft = new FieldExpression(FieldExpression.OWNER, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, USER_TEST);
        Expression queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Finished");
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.AND));
        workflows = workflow.query(query);
        assertEquals("empty nested query AND", 0, workflows.size());

        // -------------------------- negated nested query: AND ----------------------------------
        queryLeft = new FieldExpression(FieldExpression.OWNER, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, USER_TEST);
        queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Finished", true);
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.AND));
        workflows = workflow.query(query);
        assertEquals("negated nested query AND", 1, workflows.size());

        // -------------------------- nested query: AND + same context ------------------------------------------
        queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Underway");
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.AND));
        workflows = workflow.query(query);
        assertEquals("nested query AND", 1, workflows.size());

        // ------------------------- empty nested query: OR + mixed context -------------------------------------
        queryLeft = new FieldExpression(FieldExpression.FINISH_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date());
        queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Finished");
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.OR));

        try {
            workflows = workflow.query(query);
            assertEquals("empty nested query OR + mixed context", 0, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.warn("Query not supported: " + e);
        }

        // ------------------------- negated nested query: OR -------------------------------------
        queryLeft = new FieldExpression(FieldExpression.FINISH_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date());
        queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Finished", true);
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.OR));

        try {
            workflows = workflow.query(query);
            assertEquals("negated nested query OR", 1, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.warn("Query not supported: " + e);
        }

        // ------------------------- nested query: OR + mixed context -------------------------------------
        queryLeft = new FieldExpression(FieldExpression.FINISH_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date());
        queryRight = new FieldExpression(FieldExpression.NAME, FieldExpression.ENTRY, FieldExpression.EQUALS, workflowName);
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.OR));

        try {
            workflows = workflow.query(query);
            assertEquals("nested query OR + mixed context", 1, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.warn("Query not supported: " + e);
        }

        // --------------------- START_DATE+CURRENT_STEPS -------------------------------------------------
        //there should be one step that has been started
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.START_DATE, FieldExpression.CURRENT_STEPS, FieldExpression.LT, new Date(System.currentTimeMillis() + 1000)));
        workflows = workflow.query(query);
        assertEquals("Expected to find one workflow step that was started", 1, workflows.size());

        // --------------------- empty FINISH_DATE+HISTORY_STEPS -------------------------------------------
        //there should be no steps that have been completed
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.FINISH_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date()));
        workflows = workflow.query(query);
        assertEquals("Expected to find no history steps that were completed", 0, workflows.size());

        // =================================================================================================
        workflow.doAction(workflowId, 1, Collections.EMPTY_MAP);

        // --------------------- START_DATE+HISTORY_STEPS -------------------------------------------------
        //there should be two step that have been started
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.START_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date(System.currentTimeMillis() + 1000)));
        workflows = workflow.query(query);
        assertEquals("Expected to find 1 workflow step in the history for entry #" + workflowId, 1, workflows.size());

        // --------------------- FINISH_DATE+HISTORY_STEPS -------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.FINISH_DATE, FieldExpression.HISTORY_STEPS, FieldExpression.LT, new Date(System.currentTimeMillis() + 1000)));
        workflows = workflow.query(query);
        assertEquals("Expected to find 1 history steps that was completed", 1, workflows.size());

        // --------------------- ACTION + HISTORY_STEPS ----------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.ACTION, FieldExpression.HISTORY_STEPS, FieldExpression.EQUALS, new Integer(1)));
        workflows = workflow.query(query);
        assertEquals("ACTION + HISTORY_STEPS", 1, workflows.size());

        // --------------------- STEP + HISTORY_STEPS ----------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.STEP, FieldExpression.HISTORY_STEPS, FieldExpression.EQUALS, new Integer(1)));
        workflows = workflow.query(query);
        assertEquals("STEP + HISTORY_STEPS", 1, workflows.size());

        // --------------------- CALLER + HISTORY_STEPS --------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.CALLER, FieldExpression.HISTORY_STEPS, FieldExpression.EQUALS, USER_TEST));
        workflows = workflow.query(query);
        assertEquals("CALLER + HISTORY_STEPS", 1, workflows.size());

        //----------------------------------------------------------------------------
        // ----- some more tests using nested expressions
        long workflowId2 = workflow.initialize(workflowName, 100, Collections.EMPTY_MAP);
        workflow.changeEntryState(workflowId, WorkflowEntry.SUSPENDED);
        queryRight = new FieldExpression(FieldExpression.STATE, FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.ACTIVATED));
        queryLeft = new FieldExpression(FieldExpression.STATE, FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.SUSPENDED));
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.OR));
        workflows = workflow.query(query);
        assertEquals(2, workflows.size());

        queryLeft = new FieldExpression(FieldExpression.OWNER, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, USER_TEST);
        queryRight = new FieldExpression(FieldExpression.STATUS, FieldExpression.CURRENT_STEPS, FieldExpression.EQUALS, "Finished", true);
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, NestedExpression.AND));
        workflows = workflow.query(query);
        assertEquals("Expected to find 2 workflows in current steps", 2, workflows.size());
    }

    public void testWorkflowQuery() throws Exception {
        WorkflowQuery query = null;
        List workflows;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", workflow.canInitialize(workflowName, 100));

        try {
            query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
            workflows = workflow.query(query);
            assertEquals(0, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.error("Store does not support query");
        }

        try {
            long workflowId = workflow.initialize(workflowName, 100, new HashMap());
            workflows = workflow.query(query);
            assertEquals(1, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.error("Store does not support query");
        }

        try {
            WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
            WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
            query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
            workflows = workflow.query(query);
            assertEquals(0, workflows.size());

            queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
            query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
            workflows = workflow.query(query);
            assertEquals(1, workflows.size());
        } catch (QueryNotSupportedException e) {
            log.error("Store does not support query");
        }
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow(USER_TEST);

        UserManager um = UserManager.getInstance();
        assertNotNull("Could not get UserManager", um);

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

    protected String getWorkflowName() {
        return getClass().getResource("/samples/example.xml").toString();
    }

    protected void logActions(int[] actions) {
        for (int i = 0; i < actions.length; i++) {
            String name = workflowDescriptor.getAction(actions[i]).getName();
            int actionId = workflowDescriptor.getAction(actions[i]).getId();

            if (log.isDebugEnabled()) {
                log.debug("Actions Available: " + name + " id:" + actionId);
            }
        }
    }
}
