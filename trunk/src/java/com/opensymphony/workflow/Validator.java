/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.module.propertyset.PropertySet;

import java.util.Map;


/**
 * Interface that must be implemented to define a java-based validator in your workflow definition.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 * @version $Revision: 1.4 $
 */
public interface Validator {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Validates the user input.
     *
     * @param transientVars Variables that will not be persisted. These include inputs
     * given in the {@link Workflow#initialize} and {@link Workflow#doAction} method calls.
     * There are a number of special variable names:
     * <li><b>entry</b>: (object type: {@link com.opensymphony.workflow.spi.WorkflowEntry})
     *  The workflow instance
     * <li><b>context</b>:
     * (object type: {@link com.opensymphony.workflow.WorkflowContext}). The workflow context.
     * <li><b>actionId</b>: The Integer ID of the current action that was taken.
     * <li><b>currentSteps</b>: A Collection of the current steps in the workflow instance.<p>
     * Also, any variable set as a {@link com.opensymphony.workflow.Register}), will also be
     * available in the transient map, no matter what. These transient variables only last through
     * the method call that they were invoked in, such as {@link Workflow#initialize}
     * and {@link Workflow#doAction}.
     * @param args The properties for this function invocation. Properties are created
     * from arg nested elements within the xml, an arg element takes in a name attribute
     * which is the properties key, and the CDATA text contents of the element map to
     * the property value.
     * @param ps The persistent variables that are associated with the current
     * instance of the workflow. Any change made to the propertyset are persisted to
     * the propertyset implementation's persistent store.
     * @throws InvalidInputException if the input is deemed to be invalid
     */
    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException, WorkflowException;
}