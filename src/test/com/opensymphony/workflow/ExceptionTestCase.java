/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import junit.framework.TestCase;

import java.util.HashMap;
import java.net.URL;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 1:58:48 PM
 */
public class ExceptionTestCase extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    public ExceptionTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testFactoryException() {
        TestWorkflow.configFile = "/osworkflow-badfactory.xml";

        //we expect an InternalWorkflowException (can't throw a checked exception in constructor, otherwise the ejb provider
        //will break spec by having a constructor
        try {
            TestWorkflow workflow = new TestWorkflow("testuser");
            fail("bad factory did not throw an error");
        } catch (InternalWorkflowException ex) {
            assertTrue("Expected FactoryException, but instead got " + ex.getRootCause(), ex.getRootCause() instanceof FactoryException);
        }
    }

    public void testStoreException() throws Exception {
        TestWorkflow.configFile = "/osworkflow-jdbc.xml";

        TestWorkflow workflow = new TestWorkflow("testuser");

        //correct behaviour is to get a store exception since we can't look up the DS
        URL url = getClass().getResource("/samples/auto1.xml");
        assertNotNull("Unable to find resource /samples/auto1.xml", url);
        try {
          workflow.initialize(url.toString(), 1, new HashMap());
        } catch (StoreException e) {
            return;
        }

        fail("Expected StoreException but did not get one for a bad JDBC datasource");
    }
}
