/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.workflow.WorkflowContext;

import javax.ejb.SessionContext;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class EJBWorkflowContext implements WorkflowContext {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionContext sessionContext;

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getCaller() {
        return sessionContext.getCallerPrincipal().getName();
    }

    public void setRollbackOnly() {
        sessionContext.setRollbackOnly();
    }

    public void setSessionContext(SessionContext context) {
        this.sessionContext = context;
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }
}
