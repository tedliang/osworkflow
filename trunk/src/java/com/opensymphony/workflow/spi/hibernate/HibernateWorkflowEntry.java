/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.spi.WorkflowEntry;

import java.util.List;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.1.1.1 $
 */
public class HibernateWorkflowEntry implements WorkflowEntry {
    //~ Instance fields ////////////////////////////////////////////////////////

    List currentSteps;
    List historySteps;
    String workflowName;
    boolean initialized;
    long id;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setCurrentSteps(List currentSteps) {
        this.currentSteps = currentSteps;
    }

    public List getCurrentSteps() {
        return currentSteps;
    }

    public void setHistorySteps(List historySteps) {
        this.historySteps = historySteps;
    }

    public List getHistorySteps() {
        return historySteps;
    }

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
