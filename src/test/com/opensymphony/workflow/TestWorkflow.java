/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.ConfigLoader;
import com.opensymphony.workflow.spi.StoreFactory;

import java.net.URL;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 1:36:21 PM
 */
public class TestWorkflow extends BasicWorkflow {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static String configFile = "/osworkflow.xml";

    //~ Constructors ///////////////////////////////////////////////////////////

    public TestWorkflow(String caller) {
        super(caller);

        //lets clear out the old store
        StoreFactory.clearCache();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void loadConfig(URL url) throws FactoryException {
        if (url == null) {
            ConfigLoader.load(getClass().getResourceAsStream(configFile));
        }
    }
}
