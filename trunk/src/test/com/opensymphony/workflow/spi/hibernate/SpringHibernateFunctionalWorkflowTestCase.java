/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.spi.AbstractFunctionalWorkflowTest;
import com.opensymphony.workflow.util.DatabaseHelper;

import org.springframework.beans.factory.xml.XmlBeanFactory;

import org.springframework.core.io.ClassPathResource;


/**
 * @author        Quake Wang
 * @since        2004-5-2
 *
 **/
public class SpringHibernateFunctionalWorkflowTestCase extends AbstractFunctionalWorkflowTest {
    //~ Constructors ///////////////////////////////////////////////////////////

    public SpringHibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        try {
            super.setUp();
            DatabaseHelper.runScript("", "jdbc/CreateDS");

            XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("osworkflow-spring.xml"));

            //workflow = (AbstractWorkflow) beanFactory.getBean("workflow");
            workflow.setConfiguration((Configuration) beanFactory.getBean("osworkflowConfiguration"));
        } catch (Exception e) {
            log.error(e);
        }
    }

    protected String getWorkflowName() {
        return "example";
    }

    protected void tearDown() throws Exception {
        DatabaseHelper.runScript(getClass().getResource("/scripts/jdbc/dropschema.sql"), "jdbc/DefaultDS");
    }
}
