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
 * @version $Revision: 1.2 $
 */
public class RemoteEJBCondition implements Condition {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(RemoteEJBCondition.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);

        ConditionRemote sessionBean = null;

        try {
            EJBHome home = (EJBHome) PortableRemoteObject.narrow(new InitialContext().lookup(ejbLocation), EJBHome.class);
            Method create = home.getClass().getMethod("create", new Class[0]);
            sessionBean = (ConditionRemote) create.invoke(home, new Object[0]);
        } catch (Exception e) {
            String message = "Could not get handle to Remote Condition: " + ejbLocation;
            throw new WorkflowException(message, e);
        }

        try {
            return sessionBean.passesCondition(transientVars, args, ps);
        } catch (RemoteException e) {
            String message = "Remote exception encountered while executing Remote Condition: " + ejbLocation;
            throw new WorkflowException(message, e);
        }
    }
}
