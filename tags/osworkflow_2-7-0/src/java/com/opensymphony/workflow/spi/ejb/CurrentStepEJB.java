/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.ejb.ExceptionlessEntityAdapter;

import java.sql.Timestamp;

import javax.ejb.CreateException;


/**
 * EJB implementation for current steps.
 *
 * @ejb.bean
 *  type="CMP"
 *  view-type="local"
 *  name="CurrentStep"
 *  reentrant="False"
 *  schema="CurrentStep"
 *  primkey-field="id"
 *
 * @ejb.pk class="java.lang.Long" extends="java.lang.Object"
 *
 * @ejb.persistence table-name="OS_CURRENTSTEP"
 *
 * @ejb.home local-extends="javax.ejb.EJBLocalHome"
 *
 * @ejb.interface local-extends="javax.ejb.EJBLocalObject,com.opensymphony.workflow.spi.ejb.AbstractLocalStep"
 *
 * @ejb.env-entry
 *  name="sequenceName"
 *  type="java.lang.String"
 *  value="WorkflowEntryEJB"
 *
 * @ejb.ejb-external-ref
 *  ref-name="ejb/SequenceGenerator"
 *  type="Session"
 *  view-type="remote"
 *  link="SequenceGenerator"
 *  home="com.opensymphony.module.sequence.SequenceGeneratorHome"
 *  business="com.opensymphony.module.sequence.SequenceGenerator"
 *
 * @ejb.finder
 *  signature="java.util.Collection findByEntryId(long entryId)"
 *  query="SELECT DISTINCT OBJECT(o) from CurrentStep o where o.entryId = ?1"
 *
 * @ejb.permission unchecked="true"
 * @ejb.transaction type="Supports"
 *
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public abstract class CurrentStepEJB extends ExceptionlessEntityAdapter {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @ejb.interface-method
     */
    public abstract void setActionId(int actionId);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="ACTION_ID"
     */
    public abstract int getActionId();

    /**
     * @ejb.interface-method
     */
    public abstract void setCaller(String caller);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="CALLER"
     */
    public abstract String getCaller();

    /**
     * @ejb.interface-method
     */
    public abstract void setDueDate(Timestamp dueDate);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="DUE_DATE"
     */
    public abstract Timestamp getDueDate();

    public abstract void setEntryId(long entryId);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="ENTRY_ID"
     */
    public abstract long getEntryId();

    /**
     * @ejb.interface-method
     */
    public abstract void setFinishDate(Timestamp finishDate);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="FINISH_DATE"
     */
    public abstract Timestamp getFinishDate();

    public abstract void setId(Long id);

    /**
     * @ejb.pk-field
     * @ejb.interface-method
     * @ejb.persistence column-name="ID"
     */
    public abstract Long getId();

    public abstract void setOwner(String owner);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="OWNER"
     */
    public abstract String getOwner();

    public abstract void setStartDate(Timestamp startDate);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="START_DATE"
     */
    public abstract Timestamp getStartDate();

    /**
     * @ejb.interface-method
     */
    public abstract void setStatus(String status);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="STATUS"
     */
    public abstract String getStatus();

    public abstract void setStepId(int stepId);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="STEP_ID"
     */
    public abstract int getStepId();

    /**
     * @ejb.create-method
     */
    public Long ejbCreate(long entryId, int stepId, String owner, Timestamp startDate, Timestamp dueDate, String status) throws CreateException {
        try {
            Long id = new Long(nextLong());
            setId(id);
            setEntryId(entryId);
            setStepId(stepId);
            setOwner(owner);
            setStartDate(startDate);
            setDueDate(dueDate);
            setStatus(status);

            return id;
        } catch (Exception e) {
            throw new CreateException(e.getMessage());
        }
    }

    public void ejbPostCreate(long entryId, int stepId, String owner, Timestamp startDate, Timestamp dueDate, String status) throws CreateException {
    }
}
