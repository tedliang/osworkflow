/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.ejb.ExceptionlessEntityAdapter;

import javax.ejb.CreateException;


/**
 * EJB implementation for workflow entries.
 *
 * @ejb.bean
 *  type="CMP"
 *  view-type="local"
 *  name="WorkflowEntry"
 *  reentrant="False"
 *  schema="WorkflowEntry"
 *  primkey-field="id"
 *
 * @ejb.pk class="java.lang.Long" extends="java.lang.Object"
 *
 * @ejb.persistence table-name="OS_WFENTRY"
 *
 * @ejb.home local-extends="javax.ejb.EJBLocalHome"
 *
 * @ejb.interface local-extends="javax.ejb.EJBLocalObject"
 *
 * @ejb.ejb-external-ref
 *  ref-name="ejb/SequenceGenerator"
 *  type="Session"
 *  view-type="remote"
 *  link="SequenceGenerator"
 *  home="com.opensymphony.module.sequence.SequenceGeneratorHome"
 *  business="com.opensymphony.module.sequence.SequenceGenerator"
 *
 * @ejb.env-entry
 *  name="sequenceName"
 *  type="java.lang.String"
 *  value="WorkflowEntryEJB"
 *
 * @ejb.permission unchecked="true"
 * @ejb.transaction type="Supports"
 *
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public abstract class WorkflowEntryEJB extends ExceptionlessEntityAdapter {
    //~ Methods ////////////////////////////////////////////////////////////////

    public abstract void setId(Long id);

    /**
     * @ejb.pk-field
     * @ejb.interface-method
     * @ejb.persistence column-name="ID"
     */
    public abstract Long getId();

    /**
     * @ejb.interface-method
     */
    public abstract void setState(int state);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="STATE"
     */
    public abstract int getState();

    public abstract void setWorkflowName(String workflowName);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="NAME"
     */
    public abstract String getWorkflowName();

    /**
     * @ejb.create-method
     */
    public Long ejbCreate(String workflowName) throws CreateException {
        try {
            Long id = new Long(nextLong());
            setId(id);
            setWorkflowName(workflowName);
            setState(0);

            return null;
        } catch (Exception e) {
            throw new CreateException(e.getMessage());
        }
    }

    public void ejbPostCreate(String workflowName) throws CreateException {
    }
}
