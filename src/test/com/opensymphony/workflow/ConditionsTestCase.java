/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import junit.framework.TestCase;


/**
 * @author hani Date: May 5, 2004 Time: 8:47:45 PM
 */
public class ConditionsTestCase extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testInvalidNestedCondition() throws Exception {
        WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getResource("/samples/invalid/invalid-nested-condition.xml").toString());

        try {
            descriptor.validate();
            fail("descriptor loaded successfully, even though nested condition is invalid exists");
        } catch (InvalidWorkflowDescriptorException e) {
            //the descriptor is invalid, which is correct
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
        }
    }

    public void testNestedCondition() throws Exception {
        Workflow workflow = new BasicWorkflow("test");
        long id = workflow.initialize(getClass().getResource("/samples/nested-condition.xml").toString(), 1, null);
        int[] availableActions = workflow.getAvailableActions(id, null);
        assertEquals("Unexpected number of available actions", 1, availableActions.length);
        assertEquals("Unexpected available action", 2, availableActions[0]);
    }
}
