/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.jndi;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.2 $
 */
public class JNDIFunctionProvider implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(JNDIFunctionProvider.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String location = (String) args.get(AbstractWorkflow.JNDI_LOCATION);
        location = location.trim();

        FunctionProvider provider = null;

        try {
            provider = (FunctionProvider) new InitialContext().lookup(location);
        } catch (NamingException e) {
            String message = "Could not get handle to JNDI FunctionProvider at: " + location;
            log.error(message, e);
            throw new WorkflowException(message, e);
        }

        provider.execute(transientVars, args, ps);
    }
}
