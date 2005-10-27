/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;
import com.opensymphony.workflow.util.DatabaseHelper;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;


/**
 * White Box semi-functional test case that uses Hibernate as Store
 *
 * @author Luca Masini (l.masini@infogroup.it)
 */
public class NewHibernateFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Instance fields ////////////////////////////////////////////////////////

    Session session;
    private SessionFactory factory;

    //~ Constructors ///////////////////////////////////////////////////////////

    public NewHibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        super.setUp();
        DatabaseHelper.runScript("", "jdbc/CreateDS");

        factory = DatabaseHelper.createHibernateSessionFactory();
        session = factory.openSession();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/new-osworkflow-hibernate.xml"));
        config.getPersistenceArgs().put("session", session);

        DefaultHibernatePropertySetDelegate propertySetDelegate = new DefaultHibernatePropertySetDelegate();
        propertySetDelegate.setSessionFactory(factory);
        config.getPersistenceArgs().put("propertySetDelegate", propertySetDelegate);

        workflow.setConfiguration(config);
    }

    protected void tearDown() throws Exception {
        session.flush();
        session.close();
        factory.close();
        DatabaseHelper.runScript(getClass().getResource("/scripts/jdbc/dropschema.sql"), "jdbc/DefaultDS");
    }
}
