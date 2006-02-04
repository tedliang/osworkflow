/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.ejb.local;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

import java.util.Map;

import javax.ejb.EJBLocalHome;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.3 $
 */
public class LocalEJBCondition implements Condition {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(LocalEJBCondition.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String ejbLocation = (String) args.get(AbstractWorkflow.EJB_LOCATION);

        Condition sessionBean = null;

        try {
            EJBLocalHome home = (EJBLocalHome) PortableRemoteObject.narrow(new InitialContext().lookup(ejbLocation), EJBLocalHome.class);
            Method create = home.getClass().getMethod("create", new Class[0]);
            sessionBean = (Condition) create.invoke(home, new Object[0]);
        } catch (Exception e) {
            String message = "Could not get handle to Local Condition: " + ejbLocation;
            log.error(message, e);
            throw new WorkflowException(message, e);
        }

        return sessionBean.passesCondition(transientVars, args, ps);
    }
}
