/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.workflow.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @version $Revision: 1.1.1.1 $
 */
public class WorkflowEJB extends AbstractWorkflow implements SessionBean {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(WorkflowEJB.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionContext sessionContext;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setSessionContext(SessionContext context) {
        this.sessionContext = context;
        super.context = new EJBWorkflowContext(context);
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public void doAction(long id, int actionId, Map inputs) throws WorkflowException {
        super.doAction(id, actionId, inputs);
    }

    public void ejbActivate() {
    }

    public void ejbCreate() throws CreateException {
        try {
            loadConfig();
        } catch (WorkflowException ex) {
            log.error("Error loading config");
            throw new CreateException(ex.getMessage());
        }
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate() throws CreateException {
    }

    public void ejbRemove() {
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
