/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.Step;

import java.util.Collection;
import java.util.Map;


/**
 * @author hani Date: Feb 17, 2005 Time: 3:56:57 PM
 */
public class VerifyCurrentSteps implements FunctionProvider {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        Collection currentSteps = (Collection) transientVars.get("currentSteps");

        if (currentSteps.size() != 1) {
            throw new WorkflowException("Expected 1 step to be in currentSteps, instead found " + currentSteps.size());
        }

        int expectedId = Integer.parseInt((String) args.get("step.id"));
        Step step = (Step) currentSteps.iterator().next();

        if (step.getStepId() != expectedId) {
            throw new WorkflowException("Expected step id " + expectedId + " in prefunction, instead found id " + step.getStepId());
        }
    }
}
