/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ofbiz;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;
import com.opensymphony.workflow.util.DatabaseHelper;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Oct 4, 2003
 * Time: 1:57:25 AM
 */
public class OfbizFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Constructors ///////////////////////////////////////////////////////////

    public OfbizFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        //ofbiz creates its own tables
        DatabaseHelper.runScript("", "jdbc/CreateDS");
        super.setUp();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/osworkflow-ofbiz.xml"));
        workflow.setConfiguration(config);
    }
}
