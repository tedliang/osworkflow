/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.config.ConfigLoader;
import com.opensymphony.workflow.spi.BaseFunctionalWorkflowTest;
import com.opensymphony.workflow.spi.DatabaseHelper;

import net.sf.hibernate.SessionFactory;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This leverages Hibernate as the persistence mechanism.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class HibernateFunctionalWorkflowTestCase extends BaseFunctionalWorkflowTest {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionFactory factory;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        DatabaseHelper.createDatabase("");

        factory = DatabaseHelper.createHibernateSessionFactory();

        TestWorkflow.configFile = "/osworkflow-hibernate.xml";

        ConfigLoader.persistenceArgs.put("sessionFactory", factory);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        factory.close();
    }
}
