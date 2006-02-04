/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.loader.ClassLoaderUtil;

import java.util.Map;

import javax.ejb.*;


/**
 * @ejb.bean
 *  type="Stateless"
 *  name="Workflow"
 *  view-type="remote"
 *  transaction-type="Container"
 *
 * @ejb.ejb-ref
 *  ejb-name="HistoryStepPrev"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="CurrentStepPrev"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="CurrentStep"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="HistoryStep"
 *  view-type="local"
 *
 * @ejb.permission unchecked="true"
 * @ejb.transaction type="Supports"
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @version $Revision: 1.7 $
 */
public abstract class WorkflowEJB extends AbstractWorkflow implements SessionBean {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void setSessionContext(SessionContext context) {
        WorkflowContext workflowContext = null;

        try {
            workflowContext = (WorkflowContext) ClassLoaderUtil.loadClass(getPersistenceProperties().getProperty("workflowContext", "com.opensymphony.workflow.ejb.EJBWorkflowContext"), getClass()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (workflowContext instanceof EJBWorkflowContext) {
            ((EJBWorkflowContext) workflowContext).setSessionContext(context);
        }

        super.context = workflowContext;
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public void changeEntryState(long id, int newState) throws WorkflowException {
        super.changeEntryState(id, newState);
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public void doAction(long id, int actionId, Map inputs) throws WorkflowException {
        super.doAction(id, actionId, inputs);
    }

    public void ejbCreate() {
    }

    public void ejbPostCreate() throws CreateException {
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
        super.executeTriggerFunction(id, triggerId);
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public long initialize(String workflowName, int initialAction, Map inputs) throws InvalidRoleException, InvalidInputException, StoreException, WorkflowException {
        return super.initialize(workflowName, initialAction, inputs);
    }
}
