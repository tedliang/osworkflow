/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class GlobalConditionsTestCase extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testGlobalConditions() throws Exception {
        Workflow workflow = new BasicWorkflow("test");
        long id = workflow.initialize(getClass().getResource("/samples/global-condition.xml").toString(), 1, null);
        Map input = new HashMap();
        input.put("global-test", Boolean.TRUE);

        int[] availableActions = workflow.getAvailableActions(id, input);
        assertEquals("Unexpected number of available actions", 0, availableActions.length);
        availableActions = workflow.getAvailableActions(id, null);
        assertEquals("Unexpected number of available actions", 1, availableActions.length);
    }
}
