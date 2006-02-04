/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 12:04:32 PM
 */
package com.opensymphony.workflow.soap;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;

import electric.util.Context;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Soap enabled Wrapper around a BasicWorkflow
 */
public class BasicSOAPWorkflow implements Workflow {
    //~ Methods ////////////////////////////////////////////////////////////////

    public int[] getAvailableActions(long id) {
        return new BasicWorkflow(getRemoteUser()).getAvailableActions(id);
    }

    public int[] getAvailableActions(long id, Map inputs) {
        return new BasicWorkflow(getRemoteUser()).getAvailableActions(id, inputs);
    }

    public void setConfiguration(Configuration configuration) {
        new BasicWorkflow(getRemoteUser()).setConfiguration(configuration);
    }

    public List getCurrentSteps(long id) {
        return new BasicWorkflow(getRemoteUser()).getCurrentSteps(id);
    }

    public int getEntryState(long id) {
        return new BasicWorkflow(getRemoteUser()).getEntryState(id);
    }

    public List getHistorySteps(long id) {
        return new BasicWorkflow(getRemoteUser()).getHistorySteps(id);
    }

    public PropertySet getPropertySet(long id) {
        return new BasicWorkflow(getRemoteUser()).getPropertySet(id);
    }

    public List getSecurityPermissions(long id) {
        return new BasicWorkflow(getRemoteUser()).getSecurityPermissions(id, null);
    }

    public List getSecurityPermissions(long id, Map inputs) {
        return new BasicWorkflow(getRemoteUser()).getSecurityPermissions(id, inputs);
    }

    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) {
        return new BasicWorkflow(getRemoteUser()).getWorkflowDescriptor(workflowName);
    }

    public String getWorkflowName(long id) {
        return new BasicWorkflow(getRemoteUser()).getWorkflowName(id);
    }

    public String[] getWorkflowNames() {
        return new BasicWorkflow(getRemoteUser()).getWorkflowNames();
    }

    public boolean canInitialize(String workflowName, int initialState) {
        return new BasicWorkflow(getRemoteUser()).canInitialize(workflowName, initialState);
    }

    public boolean canInitialize(String workflowName, int initialAction, Map inputs) {
        return new BasicWorkflow(getRemoteUser()).canInitialize(workflowName, initialAction, inputs);
    }

    public boolean canModifyEntryState(long id, int newState) {
        return new BasicWorkflow(getRemoteUser()).canModifyEntryState(id, newState);
    }

    public void changeEntryState(long id, int newState) throws WorkflowException {
        new BasicWorkflow(getRemoteUser()).changeEntryState(id, newState);
    }

    public void doAction(long id, int actionId, Map inputs) throws WorkflowException {
        new BasicWorkflow(getRemoteUser()).doAction(id, actionId, inputs);
    }

    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
        new BasicWorkflow(getRemoteUser()).executeTriggerFunction(id, triggerId);
    }

    public long initialize(String workflowName, int initialState, Map inputs) throws WorkflowException {
        return new BasicWorkflow(getRemoteUser()).initialize(workflowName, initialState, inputs);
    }

    public List query(WorkflowQuery query) throws StoreException {
        return new BasicWorkflow(getRemoteUser()).query(query);
    }

    public List query(WorkflowExpressionQuery query) throws WorkflowException {
        return new BasicWorkflow(getRemoteUser()).query(query);
    }

    public boolean removeWorkflowDescriptor(String workflowName) throws FactoryException {
        return new BasicWorkflow(getRemoteUser()).removeWorkflowDescriptor(workflowName);
    }

    public boolean saveWorkflowDescriptor(String workflowName, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        return new BasicWorkflow(getRemoteUser()).saveWorkflowDescriptor(workflowName, descriptor, replace);
    }

    protected String getRemoteUser() {
        HttpServletRequest request = (HttpServletRequest) Context.thread().getProperty("httpRequest");

        return request.getRemoteUser();
    }
}
