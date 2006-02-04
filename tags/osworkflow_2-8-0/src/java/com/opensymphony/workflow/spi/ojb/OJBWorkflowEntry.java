/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ojb;

import com.opensymphony.workflow.spi.WorkflowEntry;

import java.util.List;


/**
 * @author picard
 * Created on 9 sept. 2003
 */
public class OJBWorkflowEntry implements WorkflowEntry {
    //~ Instance fields ////////////////////////////////////////////////////////

    List currentSteps;
    List historySteps;
    String workflowName;
    long id;
    private int state;

    //~ Constructors ///////////////////////////////////////////////////////////

    public OJBWorkflowEntry() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setCurrentSteps(List list) {
        currentSteps = list;
    }

    public List getCurrentSteps() {
        return currentSteps;
    }

    public void setHistorySteps(List list) {
        historySteps = list;
    }

    public List getHistorySteps() {
        return historySteps;
    }

    public void setId(long l) {
        id = l;
    }

    public long getId() {
        return id;
    }

    public boolean isInitialized() {
        return state > 0;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setWorkflowName(String string) {
        workflowName = string;
    }

    public String getWorkflowName() {
        return workflowName;
    }
}
