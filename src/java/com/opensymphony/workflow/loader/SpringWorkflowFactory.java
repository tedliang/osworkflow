/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.FactoryException;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class SpringWorkflowFactory extends XMLWorkflowFactory {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String resource;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SpringWorkflowFactory() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setReload(String reload) {
        this.reload = Boolean.valueOf(reload).booleanValue();
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void init() {
        try {
            Properties props = new Properties();
            props.setProperty("reload", getReload());
            props.setProperty("resource", getResource());

            super.init(props);
            initDone();
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private String getReload() {
        return String.valueOf(reload);
    }

    private String getResource() {
        return resource;
    }
}
