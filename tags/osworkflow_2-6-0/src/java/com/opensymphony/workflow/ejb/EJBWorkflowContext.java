/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: Apr 29, 2002
 * Time: 10:55:22 PM
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.workflow.WorkflowContext;

import javax.ejb.SessionContext;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class EJBWorkflowContext implements WorkflowContext {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionContext context;

    //~ Constructors ///////////////////////////////////////////////////////////

    public EJBWorkflowContext(SessionContext context) {
        this.context = context;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getCaller() {
        return context.getCallerPrincipal().getName();
    }

    public void setRollbackOnly() {
        context.setRollbackOnly();
    }
}
