/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;

import junit.framework.TestCase;

import java.net.URL;

import java.util.HashMap;


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
        //we expect an InternalWorkflowException (can't throw a checked exception in constructor, otherwise the ejb provider
        //will break spec by having a constructor
        try {
            Configuration config = new DefaultConfiguration();
            config.load(getClass().getResource("/osworkflow-badfactory.xml"));
        } catch (InternalWorkflowException ex) {
            assertTrue("Expected FactoryException, but instead got " + ex.getRootCause(), ex.getRootCause() instanceof FactoryException);
        } catch (FactoryException e) {
            return;
        }

        fail("bad factory did not throw an error");
    }

    public void testInitializeInvalidActionException() throws Exception {
        Workflow workflow = new BasicWorkflow("testuser");
        URL url = getClass().getResource("/samples/auto1.xml");
        assertNotNull("Unable to find resource /samples/auto1.xml", url);

        try {
            workflow.initialize(url.toString(), 2, new HashMap());
        } catch (InvalidActionException e) {
            return;
        }

        fail("Expected InvalidActionException but did not get one for a bad action in initialize");
    }

    public void testInvalidActionException() throws Exception {
        Workflow workflow = new BasicWorkflow("testuser");
        URL url = getClass().getResource("/samples/auto1.xml");
        assertNotNull("Unable to find resource /samples/auto1.xml", url);

        long id = workflow.initialize(url.toString(), 100, new HashMap());

        try {
            workflow.doAction(id, 10, null);
        } catch (InvalidActionException e) {
            return;
        }

        fail("Expected InvalidActionException but did not get one for a bad action");
    }

    public void testStoreException() throws Exception {
        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/samples/invalid/invalid-datasource.xml"));

        Workflow workflow = new BasicWorkflow("testuser");
        workflow.setConfiguration(config);

        //correct behaviour is to get a store exception since we can't look up the DS
        URL url = getClass().getResource("/samples/auto1.xml");
        assertNotNull("Unable to find resource /samples/auto1.xml", url);

        try {
            workflow.initialize(url.toString(), 100, new HashMap());
        } catch (StoreException e) {
            return;
        }

        fail("Expected StoreException but did not get one for a bad JDBC datasource");
    }
}
