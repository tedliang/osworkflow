/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.beanshell;

import bsh.EvalError;
import bsh.Interpreter;

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
public class BeanShellRegister implements Register {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(BeanShellRegister.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args) throws WorkflowException {
        String script = (String) args.get(AbstractWorkflow.BSH_SCRIPT);

        Interpreter i = new Interpreter();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            if (loader != null) {
                i.setClassLoader(loader);
            }

            i.set("entry", entry);
            i.set("context", context);

            Object o = i.eval(script);

            return o;
        } catch (EvalError e) {
            String message = "Could not get object registered in to variable map";
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) {
                i.setClassLoader(null);
            }
        }
    }
}
