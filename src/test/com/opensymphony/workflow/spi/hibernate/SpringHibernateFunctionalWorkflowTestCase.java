/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.spi.BaseFunctionalWorkflowTest;
import com.opensymphony.workflow.spi.DatabaseHelper;

import org.springframework.beans.factory.xml.XmlBeanFactory;

import org.springframework.core.io.ClassPathResource;


/**
 * @author        Quake Wang
 * @since        2004-5-2
 * @version $Revision: 1.3 $
 *
 **/
public class SpringHibernateFunctionalWorkflowTestCase extends BaseFunctionalWorkflowTest {
    //~ Constructors ///////////////////////////////////////////////////////////

    public SpringHibernateFunctionalWorkflowTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        try
        {
	        super.setUp();
	        DatabaseHelper.createDatabase("");
	
	        XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("osworkflow-spring.xml"));
	
	        //workflow = (AbstractWorkflow) beanFactory.getBean("workflow");
	        workflow.setConfiguration((Configuration) beanFactory.getBean("osworkflowConfiguration"));
        }
        catch(Exception e)
        {
            log.error(e);
        }
    }
    protected String getWorkflowName() {
        return "example";
    }
}
