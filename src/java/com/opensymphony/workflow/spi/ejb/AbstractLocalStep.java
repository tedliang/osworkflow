/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import java.sql.Timestamp;


/**
 * Remote interface for steps. Used to encapsulate common methods between history and current steps.
 *
 * @author Hani Suleiman
 * @version $Revision: 1.2 $
 */
public interface AbstractLocalStep {
    //~ Methods ////////////////////////////////////////////////////////////////

    public int getActionId();

    public String getCaller();

    public Timestamp getDueDate();

    public long getEntryId();

    public Timestamp getFinishDate();

    public Long getId();

    public String getOwner();

    public Timestamp getStartDate();

    public String getStatus();

    public int getStepId();
}
