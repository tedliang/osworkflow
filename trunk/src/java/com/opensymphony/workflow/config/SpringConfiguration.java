/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.config;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.loader.AbstractWorkflowFactory;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.WorkflowStore;

import java.net.URL;

import java.util.Map;


/**
 * @author        Quake Wang
 * @since        2004-5-2
 * @version $Revision: 1.2 $
 *
 **/
public class SpringConfiguration implements Configuration {
    //~ Instance fields ////////////////////////////////////////////////////////

    private AbstractWorkflowFactory factory;
    private WorkflowStore store;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setFactory(AbstractWorkflowFactory factory) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.config.Configuration#isInitialized()
     */
    public boolean isInitialized() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isModifiable(String name) {
        return factory.isModifiable(name);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.config.Configuration#getPersistence()
     */
    public String getPersistence() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.config.Configuration#getPersistenceArgs()
     */
    public Map getPersistenceArgs() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStore(WorkflowStore store) {
        this.store = store;
    }

    public WorkflowDescriptor getWorkflow(String name) throws FactoryException {
        WorkflowDescriptor workflow = factory.getWorkflow(name);

        if (workflow == null) {
            throw new FactoryException("Unknown workflow name");
        }

        return workflow;
    }

    public String[] getWorkflowNames() throws FactoryException {
        return factory.getWorkflowNames();
    }

    public WorkflowStore getWorkflowStore() throws StoreException {
        return store;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.config.Configuration#load(java.net.URL)
     */
    public void load(URL url) throws FactoryException {
        // TODO Auto-generated method stub
    }

    public boolean removeWorkflow(String workflow) throws FactoryException {
        return factory.removeWorkflow(workflow);
    }

    public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        return factory.saveWorkflow(name, descriptor, replace);
    }
}
