/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.workflow.WorkflowContext;

import javax.ejb.SessionContext;


/**
 * EJB specific workflow context.
 * The default implementation is to get the caller principal from the
 * container's SessionContext. If different behaviour is desired, this
 * class can be subclassed with whatever custom logic in place to look
 * up the caller. This can be done by specifying a <code>workflowContext</code>
 * property in the ejb persistence store's properties in osworkflow.xml.
 * The value of this properly should be the classname of the WorkflowContext
 * to use.
 *
 * @author Hani Suleiman
 * @version $Revision: 1.6 $
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
