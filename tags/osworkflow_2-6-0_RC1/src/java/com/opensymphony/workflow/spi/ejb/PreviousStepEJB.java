/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import javax.ejb.EntityBean;


/**
 * @ejb.bean generate="false"
 * @ejb.pk generate="false"
 * @ejb.interface generate="false"
 * @ejb.home generate="false"
 *
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @version $Revision: 1.2 $
 */
public abstract class PreviousStepEJB implements EntityBean {
    //~ Methods ////////////////////////////////////////////////////////////////

    public abstract void setId(Long id);

    /**
     * @ejb.pk-field
     * @ejb.interface-method
     * @ejb.persistence column-name="ID"
     */
    public abstract Long getId();

    public abstract void setPreviousId(Long previousId);

    /**
     * @ejb.pk-field
     * @ejb.interface-method
     * @ejb.persistence column-name="PREVIOUS_ID"
     */
    public abstract Long getPreviousId();
}
