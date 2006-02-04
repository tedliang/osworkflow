/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

import junit.framework.TestCase;

import java.util.Map;


/**
 * @author hani Date: Apr 4, 2005 Time: 8:56:36 PM
 */
public class VerifyArg implements FunctionProvider {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        Object expected = args.get("expected");
        Object actual = args.get("actual");
        TestCase.assertEquals(expected, actual);
    }
}
