/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.ejb.remote;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

import java.lang.reflect.Method;

import java.rmi.RemoteException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBHome;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 * Condition to check a remote EJB that implements {@link ConditionRemote}.
 * A required argument for this condition is <code>ejb.location</code>, which specifies
 * the JNDI location to look up the remote EJB at.
 * <p>
 * Note that by default, the EJB will be looked up from a default InitialContext. It is
 * however possible to specify an environment for the initial context by specifying the
 * standard JNDI keys as arguments.
 * <p>
 * For example, specifying an argument with a name of <code>java.naming.security.principal</code>
 * and a value of <code>testuser</code> will result in an InitialContext being created with
 * the user specified as 'testuser'.
 *
 * @author $Author: hani $
 * @version $Revision: 1.3 $
 */
public class RemoteEJBCondition implements Condition {
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);

        Hashtable env = null;
        Iterator iter = args.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            if (env == null) {
                env = new Hashtable();
            }

            if (((String) entry.getKey()).startsWith("java.naming.")) {
                env.put(entry.getKey(), entry.getValue());
            }
        }

        ConditionRemote sessionBean;

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
