/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.ejb.remote;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

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
 * @version $Revision: 1.1.1.1 $
 */
public class RemoteEJBValidator implements Validator {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(RemoteEJBValidator.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException, WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);

        ValidatorRemote sessionBean = null;

        try {
            EJBHome home = (EJBHome) PortableRemoteObject.narrow(new InitialContext().lookup(ejbLocation), EJBHome.class);
            Method create = home.getClass().getMethod("create", new Class[0]);
            sessionBean = (ValidatorRemote) create.invoke(home, new Object[0]);
        } catch (Exception e) {
            String message = "Could not get handle to remote EJB validator: " + ejbLocation;
            log.error(message, e);
            throw new WorkflowException(message, e);
        }

        try {
            sessionBean.validate(transientVars, args, ps);
        } catch (RemoteException e) {
            String message = "Remote exception while executing remote EJB validator: " + ejbLocation;
            throw new WorkflowException(message, e);
        }
    }
}
