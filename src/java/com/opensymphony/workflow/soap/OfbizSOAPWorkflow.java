/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 2:02:10 PM
 */
package com.opensymphony.workflow.soap;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.ofbiz.OfbizWorkflow;
import com.opensymphony.workflow.query.WorkflowQuery;

import electric.util.Context;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class OfbizSOAPWorkflow implements Workflow {
    //~ Methods ////////////////////////////////////////////////////////////////

    public int[] getAvailableActions(long id) throws WorkflowException {
        return new OfbizWorkflow(getRemoteUser()).getAvailableActions(id);
    }

    public List getCurrentSteps(long id) throws StoreException, FactoryException {
        return new OfbizWorkflow(getRemoteUser()).getCurrentSteps(id);
    }

    public List getHistorySteps(long id) throws StoreException, FactoryException {
        return new OfbizWorkflow(getRemoteUser()).getHistorySteps(id);
    }

    public PropertySet getPropertySet(long id) throws StoreException, FactoryException {
        return new OfbizWorkflow(getRemoteUser()).getPropertySet(id);
    }

    public List getSecurityPermissions(long id) throws WorkflowException {
        return new OfbizWorkflow(getRemoteUser()).getSecurityPermissions(id);
    }

    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) throws FactoryException {
        return new OfbizWorkflow(getRemoteUser()).getWorkflowDescriptor(workflowName);
    }

    public String getWorkflowName(long id) throws StoreException, FactoryException {
        return new OfbizWorkflow(getRemoteUser()).getWorkflowName(id);
    }

    public boolean canInitialize(String workflowName, int initialState) throws WorkflowException {
        return new OfbizWorkflow(getRemoteUser()).canInitialize(workflowName, initialState);
    }

    public boolean canInitialize(String workflowName, int initialAction, Map inputs) throws WorkflowException {
        return new OfbizWorkflow(getRemoteUser()).canInitialize(workflowName, initialAction, inputs);
    }

    public void doAction(long id, int actionId, Map inputs) throws WorkflowException, StoreException {
        new OfbizWorkflow(getRemoteUser()).doAction(id, actionId, inputs);
    }

    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
        new OfbizWorkflow(getRemoteUser()).executeTriggerFunction(id, triggerId);
    }

    public long initialize(String workflowName, int initialState, Map inputs) throws WorkflowException {
        return new OfbizWorkflow(getRemoteUser()).initialize(workflowName, initialState, inputs);
    }

    public List query(WorkflowQuery query) throws StoreException, FactoryException {
        return new OfbizWorkflow(getRemoteUser()).query(query);
    }

    private String getRemoteUser() {
        HttpServletRequest request = (HttpServletRequest) Context.thread().getProperty("httpRequest");

        return request.getRemoteUser();
    }
}
