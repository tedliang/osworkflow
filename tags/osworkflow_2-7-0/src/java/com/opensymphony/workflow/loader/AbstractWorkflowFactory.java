/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.FactoryException;

import java.util.Properties;


/**
 * Abstract base class for all workflow factories.
 * A workflow factory is a factory class that is able
 * to provide workflow descriptors given a workflow name,
 * as well as save descriptors.
 *
 * @author Hani Suleiman
 * Date: May 10, 2002
 * Time: 11:17:06 AM
 */
public abstract class AbstractWorkflowFactory {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Properties properties = new Properties();

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get the configuration properties of this factory
     */
    public Properties getProperties() {
        return properties;
    }

    public final void init(Properties p) {
        this.properties = p;
    }

    /**
     * Get a workflow descriptor given a workflow name.
     * @param name The name of the workflow to get.
     * @return The descriptor for the specified workflow.
     * @throws FactoryException if the specified workflow name does not exist or cannot be located.
     */
    public abstract WorkflowDescriptor getWorkflow(String name) throws FactoryException;

    /**
     * Get all workflow names in the current factory
     * @return An array of all workflow names
     * @throws FactoryException if the factory cannot determine the names of the workflows it has.
     */
    public abstract String[] getWorkflowNames() throws FactoryException;

    public abstract boolean removeWorkflow(String name) throws FactoryException;

    /**
     * Save the workflow.
     * @param name The name of the workflow to same.
     * @param descriptor The descriptor for the workflow.
     * @param replace true if an existing workflow with this name should be replaced.
     * @return true if the workflow was saved.
     * @throws FactoryException if there was an error saving the workflow
     * @throws com.opensymphony.workflow.InvalidWorkflowDescriptorException if the descriptor specified is invalid
     */
    public abstract boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException;

    /**
     * Invoked after the properties of the factory have been set.
     * Subclasses should override this method and add any specific
     * setup code required. For example, connecting to an external resource
     * or database.
     * @throws FactoryException if there was an error during initialization.
     */
    public void initDone() throws FactoryException {
    }
}
