/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
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
        super.setUp();
        DatabaseHelper.createDatabase("");

        factory = DatabaseHelper.createHibernateSessionFactory();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/osworkflow-hibernate.xml"));
        workflow.setConfiguration(config);
        workflow.getConfiguration().getPersistenceArgs().put("sessionFactory", factory);
    }

    protected void tearDown() throws Exception {
        factory.close();
    }
}
