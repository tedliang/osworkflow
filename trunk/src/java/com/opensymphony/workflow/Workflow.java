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
     * @deprecated use {@link #getAvailableActions(long, Map)}  with an empty Map instead.
     */
    public int[] getAvailableActions(long id) throws WorkflowException;

    /**
     * Returns a Collection of Step objects that are the current steps of the specified workflow instance.
     *
     * @param id The workflow instance id.
     * @return The steps that the workflow instance is currently in.
     */
    public List getCurrentSteps(long id) throws WorkflowException;

    /**
     * Return the state of the specified workflow instance id.
     * @param id The workflow instance id.
     * @return int The state id of the specified workflow
     */
    public int getEntryState(long id) throws WorkflowException;

    /**
     * Returns a list of all steps that are completed for the given workflow instance id.
     *
     * @param id The workflow instance id.
     * @return a List of Steps
     * @see com.opensymphony.workflow.spi.Step
     */
    public List getHistorySteps(long id) throws WorkflowException;

    /**
     * Get the PropertySet for the specified workflow instance id.
     * @param id The workflow instance id.
     */
    public PropertySet getPropertySet(long id) throws WorkflowException;

    /**
     * Get a collection (Strings) of currently defined permissions for the specified workflow instance.
     * @param id the workflow instance id.
     * @return A List of permissions specified currently (a permission is a string name).
     */
    public List getSecurityPermissions(long id) throws WorkflowException;

    /**
     * Get the workflow descriptor for the specified workflow name.
     * @param workflowName The workflow name.
     */
    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) throws WorkflowException;

    /**
     * Get the name of the specified workflow instance.
     * @param id the workflow instance id.
     */
    public String getWorkflowName(long id) throws WorkflowException;

    /**
     * Check if the calling user has enough permissions to initialise the specified workflow.
     * @param workflowName The name of the workflow to check.
     * @param initialStep The id of the initial state to check.
     * @return true if the user can successfully call initialize, false otherwise.
     */
    public boolean canInitialize(String workflowName, int initialStep) throws WorkflowException;

    /**
    * Check if the state of the specified workflow instance can be changed to the new specified one.
    * @param id The workflow instance id.
    * @param newState The new state id.
    * @return true if the state of the workflow can be modified, false otherwise.
    */
    public boolean canModifyEntryState(long id, int newState) throws WorkflowException;

    /**
     * Modify the state of the specified workflow instance.
     * @param id The workflow instance id.
     * @param newState the new state to change the workflow instance to.
     */
    public void changeEntryState(long id, int newState) throws WorkflowException;

    /**
     * Perform an action on the specified workflow instance.
     * @param id The workflow instance id.
     * @param actionId The action id to perform (action id's are listed in the workflow descriptor).
     * @param inputs The inputs to the workflow instance.
     * @throws InvalidInputException if a validator is specified and an input is invalid.
     */
    public void doAction(long id, int actionId, Map inputs) throws InvalidInputException, WorkflowException;

    /**
     * Executes a special trigger-function using the context of the given workflow instance id.
     *
     * @param id The workflow instance id
     * @param triggerId The id of the speciail trigger-function
     */
    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException;

    /**
    * Initializes a workflow so that it can begin processing. A workflow must be initialized before it can
    * begin any sort of activity. It can only be initialized once.
    *
    * @param workflowName The workflow name to create and initialize an instance for
    * @param initialAction The initial step to start the workflow
    * @param inputs The inputs entered by the end-user
    * @throws InvalidRoleException if the user can't start this function
     * @throws InvalidInputException if a validator is specified and an input is invalid.
    */
    public long initialize(String workflowName, int initialAction, Map inputs) throws InvalidRoleException, InvalidInputException, WorkflowException, InvalidEntryStateException;

    public List query(WorkflowQuery query) throws WorkflowException;

    /**
     * Get the available actions for the specified workflow instance.
     * @ejb.interface-method
     * @param id The workflow instance id.
     * @param inputs The inputs map to pass on to conditions
     * @return An array of action id's that can be performed on the specified entry
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     */
    int[] getAvailableActions(long id, Map inputs) throws WorkflowException;

    /**
     * Get all available workflow names.
     * @throws FactoryException If the underlying workflow factory is unable to determine the list of workflows it provides.
     */
    String[] getWorkflowNames() throws FactoryException;

    /**
     * Determine if a particular workflow can be initialized.
     * @param workflowName The workflow name to check.
     * @param initialAction The potential initial action.
     * @param inputs The inputs to check.
     * @return true if the workflow can be initialized, false otherwise.
     */
    boolean canInitialize(String workflowName, int initialAction, Map inputs) throws WorkflowException;
}
