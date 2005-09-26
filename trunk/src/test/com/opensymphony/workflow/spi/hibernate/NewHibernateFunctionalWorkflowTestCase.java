/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;
import com.opensymphony.workflow.util.DatabaseHelper;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This leverages Hibernate as the persistence mechanism.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class NewHibernateFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionFactory factory;
    Session session;
    //~ Constructors ///////////////////////////////////////////////////////////

    public NewHibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        super.setUp();
        DatabaseHelper.runScript("", "jdbc/CreateDS");

        factory = DatabaseHelper.createHibernateSessionFactory();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/new-osworkflow-hibernate.xml"));
        workflow.setConfiguration(config);
        session = factory.openSession();
        ((BasicWorkflow)workflow).getConfiguration().getPersistenceArgs().put("session", session);
    }

    protected void tearDown() throws Exception {
        session.flush();
        session.close();
        factory.close();
    }
}
