/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.bsf;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import java.util.Map;


/**
 * @author Hani
 */
public class BSFCondition implements Condition {
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
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
        mgr.registerBean("jn", transientVars.get("jn"));

        try {
            BSFEngine engine = mgr.loadScriptingEngine(language);
            Object o = engine.eval(source, row, col, script);

            if (o == null) {
                return false;
            } else {
                return TextUtils.parseBoolean(o.toString());
            }
        } catch (BSFException e) {
            String message = "Could not execute BSF script";
            throw new WorkflowException(message, e);
        }
    }
}
