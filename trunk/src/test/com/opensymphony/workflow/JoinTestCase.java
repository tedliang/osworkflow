/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;

import junit.framework.TestCase;


/**
 * @author hani Date: Feb 17, 2005 Time: 4:24:20 PM
 */
public class JoinTestCase extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Workflow workflow;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testWithReject() throws Exception {
        checkRoute(new int[] {2, 3, 2, 4, 6, 7});
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow("testuser");
    }

    private void checkRoute(int[] actions) throws Exception {
        long workflowId = workflow.initialize(getClass().getResource("/samples/join.xml").toString(), 1, null);

        for (int i = 0; i < actions.length; i++) {
            workflow.doAction(workflowId, actions[i], null);
        }
    }
}
