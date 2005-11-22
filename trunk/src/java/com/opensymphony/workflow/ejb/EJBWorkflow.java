/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.ejb;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.EJBUtils;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;

import javax.naming.NamingException;


/**
 * EJB based workflow class.
 * This class acts as a wrapper around a workflow session bean.
 *
 * @author plightbo
 * @version $Revision: 1.13 $
 */
public class EJBWorkflow implements Workflow {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(EJBWorkflow.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private WorkflowRemote wf;

    //~ Constructors ///////////////////////////////////////////////////////////

    public EJBWorkflow(String location) throws CreateException, RemoteException, WorkflowException {
        WorkflowHome home = null;

        try {
            home = (WorkflowHome) EJBUtils.lookup(location, WorkflowHome.class);
        } catch (NamingException e) {
            try {
                home = (WorkflowHome) EJBUtils.lookup(WorkflowHomeFactory.COMP_NAME, WorkflowHome.class);
            } catch (NamingException e1) {
                try {
                    home = (WorkflowHome) EJBUtils.lookup(WorkflowHomeFactory.JNDI_NAME, WorkflowHome.class);
                } catch (NamingException e2) {
                    throw new WorkflowException("Could not get a handle on the workflow Home EJB", e);
                }
            }
        }

        wf = home.create();
    }

    public EJBWorkflow() throws CreateException, RemoteException, WorkflowException {
        this(WorkflowHomeFactory.JNDI_NAME);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public int[] getAvailableActions(long id) {
        try {
            return wf.getAvailableActions(id);
        } catch (RemoteException e) {
            log.error("Error getting available actions", e);

            return new int[0];
        }
    }

    public int[] getAvailableActions(long id, Map inputs) {
        try {
            return wf.getAvailableActions(id, inputs);
        } catch (RemoteException e) {
            log.error("Error getting available actions", e);

            return new int[0];
        }
    }

    public void setConfiguration(Configuration configuration) {
        try {
            wf.setConfiguration(configuration);
        } catch (RemoteException e) {
            log.fatal("Error setting configuration", e);
        }
    }

    public List getCurrentSteps(long id) {
        try {
            return wf.getCurrentSteps(id);
        } catch (RemoteException e) {
            log.error("Error getting current steps", e);

            return Collections.EMPTY_LIST;
        }
    }

    public int getEntryState(long id) {
        try {
            return wf.getEntryState(id);
        } catch (RemoteException e) {
            log.error("Error getting entry state", e);

            return WorkflowEntry.UNKNOWN;
        }
    }

    public List getHistorySteps(long id) {
        try {
            return wf.getHistorySteps(id);
        } catch (RemoteException e) {
            log.error("Error getting history steps", e);

            return Collections.EMPTY_LIST;
        }
    }

    public PropertySet getPropertySet(long id) {
        try {
            return wf.getPropertySet(id);
        } catch (RemoteException e) {
            log.error("Error getting PropertySet", e);

            return null;
        }
    }

    public List getSecurityPermissions(long id) {
        try {
            return wf.getSecurityPermissions(id);
        } catch (RemoteException e) {
            log.error("Error getting security permissions", e);

            return Collections.EMPTY_LIST;
        }
    }

    public List getSecurityPermissions(long id, Map inputs) {
        try {
            return wf.getSecurityPermissions(id, inputs);
        } catch (RemoteException e) {
            log.error("Error getting security permissions", e);

            return Collections.EMPTY_LIST;
        }
    }

    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) {
        try {
            return wf.getWorkflowDescriptor(workflowName);
        } catch (RemoteException e) {
            log.error("Error getting descriptor", e);

            return null;
        }
    }

    public String getWorkflowName(long id) {
        try {
            return wf.getWorkflowName(id);
        } catch (RemoteException e) {
            log.error("Error getting workflow name", e);

            return null;
        }
    }

    public String[] getWorkflowNames() {
        try {
            return wf.getWorkflowNames();
        } catch (RemoteException e) {
            log.error("Error calling getWorkflowNames", e);

            return new String[0];
        }
    }

    public boolean canInitialize(String workflowName, int initialState) {
        try {
            return wf.canInitialize(workflowName, initialState);
        } catch (RemoteException e) {
            log.error("Error checking canInitialize", e);

            return false;
        }
    }

    public boolean canInitialize(String workflowName, int initialAction, Map inputs) {
        try {
            return wf.canInitialize(workflowName, initialAction, inputs);
        } catch (RemoteException e) {
            log.error("Error checking canInitialize", e);

            return false;
        }
    }

    public boolean canModifyEntryState(long id, int newState) {
        try {
            return wf.canModifyEntryState(id, newState);
        } catch (RemoteException e) {
            log.error("Error checking modifying entry state", e);

            return false;
        }
    }

    public void changeEntryState(long id, int newState) throws WorkflowException {
        try {
            wf.changeEntryState(id, newState);
        } catch (RemoteException e) {
            log.error("Error modifying entry state", e);
            throw new WorkflowException(e);
        }
    }

    public void doAction(long id, int actionId, Map inputs) throws InvalidInputException, WorkflowException {
        try {
            wf.doAction(id, actionId, inputs);
        } catch (RemoteException e) {
            log.error("Error performing action", e);
            throw new WorkflowException(e);
        }
    }

    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
        try {
            wf.executeTriggerFunction(id, triggerId);
        } catch (RemoteException e) {
            log.error("Error executing trigger", e);
            throw new WorkflowException(e);
        }
    }

    public long initialize(String workflowName, int initialState, Map inputs) throws InvalidRoleException, InvalidInputException, WorkflowException {
        try {
            return wf.initialize(workflowName, initialState, inputs);
        } catch (RemoteException e) {
            log.error("Error initializing", e);
            throw new WorkflowException(e);
        }
    }

    public List query(WorkflowExpressionQuery query) throws WorkflowException {
        throw new QueryNotSupportedException("EJB Store does not support queries");
    }

    public List query(WorkflowQuery query) throws WorkflowException {
        throw new QueryNotSupportedException("EJB Store does not support queries");
    }

    public boolean removeWorkflowDescriptor(String workflowName) throws FactoryException {
        try {
            return wf.removeWorkflowDescriptor(workflowName);
        } catch (RemoteException e) {
            log.error("Error removing workflow " + workflowName, e);

            return false;
        }
    }

    public boolean saveWorkflowDescriptor(String workflowName, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        try {
            return wf.saveWorkflowDescriptor(workflowName, descriptor, replace);
        } catch (RemoteException e) {
            log.error("Error saving workflow", e);
            throw new FactoryException(e);
        }
    }
}
