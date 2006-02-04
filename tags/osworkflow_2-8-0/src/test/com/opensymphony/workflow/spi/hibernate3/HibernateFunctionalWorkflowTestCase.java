/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate3;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;
import com.opensymphony.workflow.util.DatabaseHelper;
import com.opensymphony.workflow.util.PropertySetDelegateImpl;

import net.sf.hibernate.SessionFactory;


/**
 * White Box semi-functional test case that uses Hibernate as Store
 *
 * @author Luca Masini (l.masini@infogroup.it)
 */
public class HibernateFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Instance fields ////////////////////////////////////////////////////////

    org.hibernate.Session session;
    private SessionFactory factory;
    private org.hibernate.SessionFactory factory3;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        super.setUp();
        DatabaseHelper.runScript("", "jdbc/CreateDS");

        factory = DatabaseHelper.createPropertySetSessionFactory();
        factory3 = DatabaseHelper.createHibernate3SessionFactory();
        session = factory3.openSession();

        Configuration config = new DefaultConfiguration();
        config.load(getClass().getResource("/osworkflow-hibernate3.xml"));
        config.getPersistenceArgs().put("session", session);
        config.getPersistenceArgs().put("propertySetDelegate", new PropertySetDelegateImpl());

        workflow.setConfiguration(config);
    }

    protected void tearDown() throws Exception {
        session.flush();
        session.close();

        factory.close();
        factory3.close();
        DatabaseHelper.runScript(getClass().getResource("/scripts/jdbc/dropschema.sql"), "jdbc/DefaultDS");
    }
}
