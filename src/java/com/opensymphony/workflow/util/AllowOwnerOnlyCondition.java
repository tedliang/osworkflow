/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import java.util.*;


/**
 * Simple utility condition that returns true if the owner is the caller. Looks at
 * ALL current steps unless a stepId is given in the optional argument "stepId".
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class AllowOwnerOnlyCondition implements Condition {
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws StoreException {
        int stepId = 0;
        String stepIdVal = (String) args.get("stepId");

        if (stepIdVal != null) {
            try {
                stepId = Integer.parseInt(stepIdVal);
            } catch (Exception ex) {
            }
        }

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
        WorkflowStore store = (WorkflowStore) transientVars.get("store");
        List currentSteps = store.findCurrentSteps(entry.getId());

        if (stepId == 0) {
            for (Iterator iterator = currentSteps.iterator();
                    iterator.hasNext();) {
                Step step = (Step) iterator.next();

                if ((step.getOwner() != null) && context.getCaller().equals(step.getOwner())) {
                    return true;
                }
            }
        } else {
            for (Iterator iterator = currentSteps.iterator();
                    iterator.hasNext();) {
                Step step = (Step) iterator.next();

                if (stepId == step.getStepId()) {
                    if ((step.getOwner() != null) && context.getCaller().equals(step.getOwner())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
