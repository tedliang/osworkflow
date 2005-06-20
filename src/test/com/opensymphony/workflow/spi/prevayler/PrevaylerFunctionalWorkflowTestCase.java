/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Feb 12, 2004
 *
 *
 */
package com.opensymphony.workflow.spi.prevayler;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;


/**
 *
 *
 *
 * @author Christopher Farnham
 **/
public class PrevaylerFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * @param s
     */
    public PrevaylerFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/osworkflow-prevayler.xml"));
        workflow.setConfiguration(config);
    }
}
