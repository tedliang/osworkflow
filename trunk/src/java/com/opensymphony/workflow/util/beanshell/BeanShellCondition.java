/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.beanshell;

import bsh.EvalError;
import bsh.Interpreter;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.1.1.1 $
 */
public class BeanShellCondition implements Condition {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(BeanShellCondition.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String script = (String) args.get(AbstractWorkflow.BSH_SCRIPT);

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

        Interpreter i = new Interpreter();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            if (loader != null) {
                i.setClassLoader(loader);
            }

            i.set("entry", entry);
            i.set("context", context);
            i.set("transientVars", transientVars);
            i.set("propertySet", ps);
            i.set("jn", transientVars.get("jn"));

            Object o = i.eval(script);

            if (o == null) {
                return false;
            } else {
                return TextUtils.parseBoolean(o.toString());
            }
        } catch (EvalError e) {
            String message = "Could not execute BeanShell script";
            log.error(message, e);
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) {
                i.setClassLoader(null);
            }
        }
    }
}
