/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import java.io.Serializable;


/**
 * Simple implemenation.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class SimpleWorkflowEntry implements WorkflowEntry, Serializable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected String workflowName;
    protected boolean initialized;
    protected long id;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SimpleWorkflowEntry(long id, String workflowName, boolean initialized) {
        this.id = id;
        this.workflowName = workflowName;
        this.initialized = initialized;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }
}
