/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.memory;

import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class MemoryFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Constructors ///////////////////////////////////////////////////////////

    public MemoryFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        MemoryWorkflowStore.reset();
        super.setUp();
    }
}
