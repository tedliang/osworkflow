/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ojb;

import com.opensymphony.workflow.spi.Step;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * @author picard
 * Created on 9 sept. 2003
 */
public abstract class OJBStep implements Step {
    //~ Instance fields ////////////////////////////////////////////////////////

    Date dueDate;
    Date finishDate;
    Date startDate;
    List previousSteps;
    OJBWorkflowEntry entry;
    String caller;
    String owner;
    String status;
    int actionId;
    int stepId;
    long entryId;
    long id;

    //~ Constructors ///////////////////////////////////////////////////////////

    public OJBStep() {
        super();
    }

    public OJBStep(OJBStep step) {
        this.actionId = step.getActionId();
        this.caller = step.getCaller();
        this.finishDate = step.getFinishDate();
        this.dueDate = step.getDueDate();
        this.startDate = step.getStartDate();
        this.id = step.getId();
        this.owner = step.getOwner();
        this.status = step.getStatus();
        this.stepId = step.getStepId();
        this.previousSteps = step.getPreviousSteps();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setActionId(int i) {
        actionId = i;
    }

    public int getActionId() {
        return actionId;
    }

    public void setCaller(String string) {
        caller = string;
    }

    public String getCaller() {
        return caller;
    }

    public void setDueDate(Date date) {
        dueDate = date;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setEntry(OJBWorkflowEntry entry) {
        this.entry = entry;
    }

    public OJBWorkflowEntry getEntry() {
        return entry;
    }

    public void setEntryId(long l) {
        entryId = l;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setFinishDate(Date date) {
        finishDate = date;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setId(long l) {
        id = l;
    }

    public long getId() {
        return id;
    }

    public void setOwner(String string) {
        owner = string;
    }

    public String getOwner() {
        return owner;
    }

    public long[] getPreviousStepIds() {
        if (previousSteps == null) {
            return new long[0];
        }

        long[] previousStepIds = new long[previousSteps.size()];
        int i = 0;

        for (Iterator iterator = previousSteps.iterator(); iterator.hasNext();) {
            OJBStep ojbStep = (OJBStep) iterator.next();
            previousStepIds[i] = ojbStep.getId();
            i++;
        }

        return previousStepIds;
    }

    public void setPreviousSteps(List list) {
        previousSteps = list;
    }

    public List getPreviousSteps() {
        return previousSteps;
    }

    public void setStartDate(Date date) {
        startDate = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStatus(String string) {
        status = string;
    }

    public String getStatus() {
        return status;
    }

    public void setStepId(int i) {
        stepId = i;
    }

    public int getStepId() {
        return stepId;
    }
}
