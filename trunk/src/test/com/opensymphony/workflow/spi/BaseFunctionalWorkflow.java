package com.opensymphony.workflow.spi;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.opensymphony.module.user.Group;
import com.opensymphony.module.user.User;
import com.opensymphony.module.user.UserManager;
import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public abstract class BaseFunctionalWorkflow extends TestCase
{
    protected AbstractWorkflow workflow;
    protected WorkflowDescriptor workflowDescriptor;
    //string used by propertyset-create.xml
    public String myvar;

    public BaseFunctionalWorkflow(String s)
    {
        super(s);
    }
    protected void setUp() throws Exception
    {
        TestWorkflow.configFile = "/osworkflow.xml";
        workflow = new TestWorkflow("test");
    }
    public void OFFtestInvalidRole() throws Exception
    {
        try
        {
            workflow.initialize(getClass().getResource("/example.xml").toString(), 1, new HashMap());
            fail("Should have thrown an exception 'You are restricted from initializing this workflow'");
        }
        catch (InvalidRoleException ire)
        {
            //good
        }
    }
    public void testExampleWorkflow() throws Exception
    {
        UserManager um = UserManager.getInstance();
        User test = um.createUser("test");
        test.setPassword("test");
        Group foos = um.createGroup("foos");
        Group bars = um.createGroup("bars");
        Group bazs = um.createGroup("bazs");
        test.addToGroup(foos);
        test.addToGroup(bars);
        test.addToGroup(bazs);
        long workflowId = workflow.initialize(getClass().getResource("/example.xml").toString(), 1, new HashMap());
        String workorderName = workflow.getWorkflowName(workflowId);
        workflowDescriptor = workflow.getWorkflowDescriptor(workorderName);
        log("Name of workorder:" + workorderName);
        List currentSteps = workflow.getCurrentSteps(workflowId);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, ((Step) currentSteps.get(0)).getStepId());
        log("Perform Finish First Draft");
        workflow.doAction(workflowId, 1, Collections.EMPTY_MAP);
        int[] actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(2,actions.length);
        logActions(actions);
        log("Perform Finish Foo");
        workflow.doAction(workflowId, 12, Collections.EMPTY_MAP);
        log("Perform Finish Bar");
        workflow.doAction(workflowId, 13, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(1,actions.length);
        logActions(actions);
        log("Perform Finish Baz");
        workflow.doAction(workflowId, 14, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        logActions(actions);
        log("Perform Finish Editing");
        workflow.doAction(workflowId, 3, Collections.EMPTY_MAP);
        actions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
        assertEquals(3,actions.length);
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
    }

    protected void log(String s)
    {
        //System.out.println(s);
    }

    protected void logActions(int[] actions)
    {
        for (int i = 0; i < actions.length; i++)
        {
            String name = workflowDescriptor.getAction(actions[i]).getName();
            int actionId = workflowDescriptor.getAction(actions[i]).getId();
            log("Actions Available: " + name + " id:" + actionId);
        }
    }
}
