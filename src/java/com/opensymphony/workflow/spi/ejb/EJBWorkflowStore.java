/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

import java.util.*;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 * EJB entity bean workflow store implementation.
 * <p>
 *
 * The following property is optional:
 * <ul>
 *  <li><b>property.store</b> - JNDI location for the OSCore EJBPropertySet PropertyStore (defaults to "ejb/propertyStore")</li>
 *  <li><b>workflow.store</b> - JNDI location for the ejb workflow store session bean (defaults to @link{#WorkflowStoreHome.JNDI_NAME)</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class EJBWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(EJBWorkflowStore.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private WorkflowStoreRemote session;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntryState(long entryId, int state) throws StoreException {
        try {
            session.setEntryState(entryId, state);
        } catch (RemoteException ex) {
            log.error("Error changing state of workflow instance #" + entryId + " to " + state, ex);
        }
    }

    public PropertySet getPropertySet(long entryId) throws StoreException {
        try {
            HashMap args = new HashMap(2);
            args.put("entityId", new Long(entryId));
            args.put("entityName", "WorkflowEntry");

            return PropertySetManager.getInstance("ejb", args);
        } catch (Exception e) {
            throw new StoreException("Could not retrieve PropertySet for workflow instance #" + entryId, e);
        }
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        try {
            return session.createCurrentStep(entryId, stepId, owner, startDate, dueDate, status, previousIds);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        try {
            return session.createEntry(workflowName);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public List findCurrentSteps(long entryId) throws StoreException {
        try {
            return session.findCurrentSteps(entryId);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public WorkflowEntry findEntry(long entryId) throws StoreException {
        try {
            return session.findEntry(entryId);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public List findHistorySteps(long entryId) throws StoreException {
        try {
            return session.findHistorySteps(entryId);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public void init(Map props) {
        String workflowSessionLocation = (String) props.get("workflow.store");

        if (workflowSessionLocation == null) {
            workflowSessionLocation = WorkflowStoreHomeFactory.JNDI_NAME;
        }

        try {
            InitialContext context = new InitialContext();
            session = ((WorkflowStoreHome) PortableRemoteObject.narrow(context.lookup(workflowSessionLocation), WorkflowStoreHome.class)).create();
        } catch (Exception ex) {
            log.error("Error looking up homes", ex);
            throw new IllegalArgumentException(ex.toString());
        }
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        try {
            return session.markFinished(step, actionId, finishDate, status, caller);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);

            return null;
        }
    }

    public void moveToHistory(Step step) throws StoreException {
        try {
            session.moveToHistory(step);
        } catch (RemoteException ex) {
            log.fatal("System error", ex);
        }
    }

    public List query(WorkflowQuery query) {
        // not implemented
        throw new UnsupportedOperationException("EJB store does not support queries");
    }

    public List query(WorkflowExpressionQuery query) throws StoreException {
        throw new UnsupportedOperationException("EJB store does not support queries");
    }
}
