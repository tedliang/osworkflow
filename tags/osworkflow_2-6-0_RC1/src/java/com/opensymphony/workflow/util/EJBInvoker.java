/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 * Generic EJB Invoker function.
 * This function is used to invoke an EJB listener in a step. The EJB
 * must implement WorkflowListener if it's a remote session bean, or
 * WorkflowLocalListener if it's a local session bean.<br>
 * It accepts a number of arguments, these are:
 *
 * <ul>
 *  <li>ejb-home - The fully qualified class name of the EJB remote home interface</li>
 *  <li>ejb-local-home - The fully qualified class name of the local home interface</li>
 *  <li>ejb-jndi-location - The JNDI location of the ejb to invoke</li>
 * </ul>
 * <p>
 * Note that only one of ejb-home or ejb-local-home can be specified.
 *
 * Also, please note that the entire set of properties will be passed through to the
 * constructor for InitialContext, meaning that if you need to use an
 * InintialContextFactory other than the default one, you are free to include arguments
 * that will do so.
 *
 * @author Hani Suleiman
 * @version $Revision: 1.2 $
 * Date: Apr 6, 2002
 * Time: 11:48:14 PM
 */
public class EJBInvoker implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(EJBInvoker.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) {
        Class homeClass = null;
        Object home = null;
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
        Hashtable env = new Hashtable(args);

        try {
            InitialContext ic = new InitialContext(env);

            if (log.isDebugEnabled()) {
                log.debug("executing with properties=" + args);
            }

            if (args.containsKey("ejb-home")) {
                homeClass = Class.forName((String) args.get("ejb-home"));
            } else if (args.containsKey("ejb-local-home")) {
                homeClass = Class.forName((String) args.get("ejb-local-home"));
            }

            home = PortableRemoteObject.narrow(ic.lookup((String) args.get("ejb-jndi-location")), homeClass);

            Method method = homeClass.getMethod("create", new Class[0]);

            if (java.rmi.Remote.class.isAssignableFrom(homeClass)) {
                WorkflowListener listener = (WorkflowListener) method.invoke(home, new Object[0]);
                listener.stateChanged(entry);
            } else {
                WorkflowLocalListener listener = (WorkflowLocalListener) method.invoke(home, new Object[0]);
                listener.stateChanged(entry);
            }
        } catch (Exception e) {
            log.error("Error invoking EJB homeClass=" + homeClass, e);
        }
    }
}
