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
 * @version $Revision: 1.3 $
 */
public class EJBWorkflowContext implements WorkflowContext {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionContext context;

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getCaller() {
        return context.getCallerPrincipal().getName();
    }

    public void setRollbackOnly() {
        context.setRollbackOnly();
    }

    public void setSessionContext(SessionContext context) {
        this.context = context;
    }
}
