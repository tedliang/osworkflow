/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.ejb.remote;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

import java.rmi.RemoteException;

import java.util.Map;

import javax.ejb.EJBHome;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.2 $
 */
public class RemoteEJBRegister implements Register {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(RemoteEJBRegister.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args) throws WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);
        RegisterRemote sessionBean = null;

        try {
            EJBHome home = (EJBHome) PortableRemoteObject.narrow(new InitialContext().lookup(ejbLocation), javax.ejb.EJBHome.class);
            Method create = home.getClass().getMethod("create", new Class[0]);
            sessionBean = (RegisterRemote) create.invoke(home, new Object[0]);
        } catch (Exception e) {
            String message = "Could not get handle to remote EJB register at: " + ejbLocation;
            throw new WorkflowException(message, e);
        }

        try {
            return sessionBean.registerVariable(context, entry, args);
        } catch (RemoteException e) {
            String message = "Remote exception while executing remote EJB register: " + ejbLocation;
            log.error(message, e);
            throw new WorkflowException(message, e);
        }
    }
}
