/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;


/**
 * Interface for a workflow entry.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public interface WorkflowEntry {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the unique ID of the workflow entry.
     */
    public long getId();

    /**
     * Returns true if the workflow entry has been initialized.
     */
    public boolean isInitialized();

    /**
     * Returns the name of the workflow that this entry is an instance of.
     */
    public String getWorkflowName();
}
