/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.ejb.local;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

import java.util.Map;

import javax.ejb.EJBHome;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 * A register helper that exposes a local session bean as a register.
 * This register takes in one argument, <code>ejb.location</code> that specifies
 * the JNDI location of the session bean.
 *
 * @author $Author: hani $
 * @version $Revision: 1.2 $
 */
public class LocalEJBRegister implements Register {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(LocalEJBRegister.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args) throws WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);
        Register sessionBean = null;

        try {
            EJBHome home = (EJBHome) PortableRemoteObject.narrow(new InitialContext().lookup(ejbLocation), javax.ejb.EJBHome.class);
            Method create = home.getClass().getMethod("create", new Class[0]);
            sessionBean = (Register) create.invoke(home, new Object[0]);
        } catch (Exception e) {
            String message = "Could not get handle to local EJB register at: " + ejbLocation;
            throw new WorkflowException(message, e);
        }

        return sessionBean.registerVariable(context, entry, args);
    }
}
