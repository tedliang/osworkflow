/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowQuery;

import java.util.List;
import java.util.Map;


/**
 * The core workflow interface.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 */
public interface Workflow {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get an array of possible actions for the specified workflow in the current state that the caller can perform.
     *
     * @param id the workflow to get actions for
     * @return an array of action id's. The collection of action is those that can be performed on the current step
     */
    public int[] getAvailableActions(long id) throws WorkflowException;

    /**
     * Returns a Collection of Step objects that are the current steps of this workflow.
     *
     * @param id The workflow ID
     * @return The steps that the workflow is currently in
     */
    public List getCurrentSteps(long id) throws WorkflowException;

    /**
     * Returns a list of all steps that are finished for the given workflow instance ID.
     *
     * @param id the ID of the workflow instance
     * @return a List of Steps
     * @see com.opensymphony.workflow.spi.Step
     */
    public List getHistorySteps(long id) throws WorkflowException;

    /**
     * Get the PropertySet for the specified workflow ID
     * @param id The workflow ID
     */
    public PropertySet getPropertySet(long id) throws WorkflowException;

    /**
     * Get a collection (Strings) of currently defined permissions for the specified workflow/
     *
     * @param id the workflow ID
     * @return A List of permissions specified currently (a permission is a string name)
     */
    public List getSecurityPermissions(long id) throws WorkflowException;

    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) throws WorkflowException;

    public String getWorkflowName(long id) throws WorkflowException;

    /**
     * Check if the calling user has enough permissions to initialise the specified workflow
     *
     * @param workflowName The name of the workflow to check
     * @param initialStep The id of the initial state to check
     * @return true if the user can successfully call initialize, false otherwise
     */
    public boolean canInitialize(String workflowName, int initialStep) throws WorkflowException;

    /**
     * Perform an action on the specified workflow
     *
     * @param id The workflow id to perform the action on
     * @param actionId The action id to perform (action id's are listed in the workflow descriptor)
     * @param inputs
     * @throws InvalidInputException
     */
    public void doAction(long id, int actionId, Map inputs) throws InvalidInputException, WorkflowException;

    /**
     * Executes a special trigger-function using the context of the given workflow instance id.
     *
     * @param id the workflow instance id
     * @param triggerId the id of the speciail trigger-function
     */
    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException;

    /**
    * Initializes a workflow so that it can begin processing. A workflow must be initialized before it can
    * begin any sort of activity. It can only be initialized once.
    *
    * @param workflowName the workflow instance id
    * @param initialAction the initial step to start the workflow
    * @param inputs the inputs entered by the end-user
    * @throws InvalidRoleException if the user can't start this function
    * @throws InvalidInputException if the input the user gave is invalid
    */
    public long initialize(String workflowName, int initialAction, Map inputs) throws InvalidRoleException, InvalidInputException, WorkflowException;

    public List query(WorkflowQuery query) throws WorkflowException;

    String[] getWorkflowNames() throws FactoryException;

    boolean canInitialize(String workflowName, int initialAction, Map inputs) throws WorkflowException;
}
