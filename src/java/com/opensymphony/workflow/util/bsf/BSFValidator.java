/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.bsf;

import com.ibm.bsf.BSFEngine;
import com.ibm.bsf.BSFManager;

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
public class BSFValidator implements Validator {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String language = (String) args.get(AbstractWorkflow.BSF_LANGUAGE);
        String source = (String) args.get(AbstractWorkflow.BSF_SOURCE);
        int row = TextUtils.parseInt((String) args.get(AbstractWorkflow.BSF_ROW));
        int col = TextUtils.parseInt((String) args.get(AbstractWorkflow.BSF_COL));
        String script = (String) args.get(AbstractWorkflow.BSF_SCRIPT);

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

        BSFManager mgr = new BSFManager();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader != null) {
            mgr.setClassLoader(loader);
        }

        mgr.registerBean("entry", entry);
        mgr.registerBean("context", context);
        mgr.registerBean("transientVars", transientVars);
        mgr.registerBean("propertySet", ps);

        try {
            BSFEngine engine = mgr.loadScriptingEngine(language);
            Object o = engine.eval(source, row, col, script);

            if (o != null) {
                throw new InvalidInputException(o);
            }
        } catch (Exception e) {
            String message = "Could not execute BSF validator";
            throw new WorkflowException(message, e);
        }
    }
}
