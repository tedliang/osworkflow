/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.bsf;

import com.ibm.bsf.BSFEngine;
import com.ibm.bsf.BSFException;
import com.ibm.bsf.BSFManager;

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
 * @version $Revision: 1.2 $
 */
public class BSFRegister implements Register {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(BSFRegister.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args) throws WorkflowException {
        String language = (String) args.get(AbstractWorkflow.BSF_LANGUAGE);
        String source = (String) args.get(AbstractWorkflow.BSF_SOURCE);
        int row = TextUtils.parseInt((String) args.get(AbstractWorkflow.BSF_ROW));
        int col = TextUtils.parseInt((String) args.get(AbstractWorkflow.BSF_COL));
        String script = (String) args.get(AbstractWorkflow.BSF_SCRIPT);

        BSFManager mgr = new BSFManager();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader != null) {
            mgr.setClassLoader(loader);
        }

        mgr.registerBean("entry", entry);
        mgr.registerBean("context", context);

        try {
            BSFEngine engine = mgr.loadScriptingEngine(language);
            Object o = engine.eval(source, row, col, script);

            return o;
        } catch (BSFException e) {
            String message = "Could not get object registered in to variable map";
            throw new WorkflowException(message, e);
        }
    }
}
