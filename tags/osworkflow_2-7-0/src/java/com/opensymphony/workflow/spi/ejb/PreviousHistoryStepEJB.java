/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.workflow.spi.ejb.PreviousStepPK;

import javax.ejb.CreateException;


/**
 * @ejb.bean
 *  type="CMP"
 *  view-type="local"
 *  name="HistoryStepPrev"
 *  reentrant="False"
 *  schema="HistoryStepPrev"
 *
 * @ejb.pk extends="java.lang.Object" class="com.opensymphony.workflow.spi.ejb.PreviousStepPK"
 *
 * @ejb.persistence table-name="OS_HISTORYSTEP_PREV"
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
 * @ejb.finder
 *  signature="java.util.Collection findByStepId(long entryId)"
 *  query="SELECT DISTINCT OBJECT(o) from HistoryStepPrev o where o.id = ?1"
 *
 * @ejb.permission unchecked="true"
 * @ejb.transaction type="Supports"
 *
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Apr 7, 2003
 * Time: 8:17:09 PM
 */
public abstract class PreviousHistoryStepEJB extends PreviousStepEJB {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @ejb.create-method
     */
    public PreviousStepPK ejbCreate(long id, long previousId) throws CreateException {
        try {
            Long pkA = new Long(id);
            Long pkB = new Long(previousId);
            setId(pkA);
            setPreviousId(pkB);

            return new PreviousStepPK(pkA, pkB);
        } catch (Exception e) {
            throw new CreateException(e.getMessage());
        }
    }

    public void ejbPostCreate(long id, long previousId) throws CreateException {
    }
}
