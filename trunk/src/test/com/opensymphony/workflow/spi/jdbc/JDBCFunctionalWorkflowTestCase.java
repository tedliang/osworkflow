/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.jdbc;

import com.javaranch.unittest.helper.sql.pool.JNDIUnitTestHelper;

import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.spi.BaseFunctionalWorkflow;
import com.opensymphony.workflow.spi.DatabaseHelper;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This leverages Hibernate as teh
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class JDBCFunctionalWorkflowTestCase extends BaseFunctionalWorkflow {
    //~ Constructors ///////////////////////////////////////////////////////////

    public JDBCFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void tearDown() {
        try {
            JNDIUnitTestHelper.shutdown();
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
    }

    protected void setUp() throws Exception {
        //ok so this code usually goes in the setUp but...
        if (JNDIUnitTestHelper.notInitialized()) {
            try {
                File file = new File("src/test/datasource.properties");
                assertTrue("could not find configuration file " + file.getAbsoluteFile(), file.exists());
                JNDIUnitTestHelper.init(file.getAbsolutePath());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                fail("IOException thrown : " + ioe.getMessage());
            } catch (NamingException ne) {
                ne.printStackTrace();
                fail("NamingException thrown on Init : " + ne.getMessage());
            }
        }

        DatabaseHelper.exportSchemaForJDBC();

        TestWorkflow.configFile = "/osworkflow-jdbc.xml";
        workflow = new TestWorkflow("test");
    }
}
