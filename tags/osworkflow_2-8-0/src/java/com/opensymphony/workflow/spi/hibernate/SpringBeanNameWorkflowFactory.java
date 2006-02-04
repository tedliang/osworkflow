/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.WorkflowFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Properties;


/**
 * Spring-compliant implementation of {@link com.opensymphony.workflow.loader.AbstractWorkflowFactory}. This
 * workflow factory retrieves {@link com.opensymphony.workflow.loader.WorkflowDescriptor}s from the Spring's
 * {@link ApplicationContext}. The name of the WorkflowDescriptors is the name
 * of the bean in the applicationContext. <br />
 * Motivation: reduce number of external configuration files leaving only
 * workflow definitions files. <br />
 * <ul>
 * <li>SpringConfiguration replaces osworkflow.xml</li>
 * <li>WorkflowFactoryImpl replaces workflows.xml</li>
 * </ul>
 * Usage:
 * <pre>
 * &lt;bean id=&quot;myworkflow&quot; class=&quot;com.opensymphony.workflow.spi.hibernate.WorkflowDescriptorFactoryBean&quot;&gt;
 *     &lt;property name=&quot;descriptorResource&quot;&gt;
 *         &lt;value&gt;classpath:/META-INF/workflow/myworkflow.xml&lt;/value&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id=&quot;workflowFactory&quot; class=&quot;com.opensymphony.workflow.spi.hibernate.BeanNameWorkflowFactory&quot;/&gt;
 * </pre>
 *
 * @author xd
 */
public class SpringBeanNameWorkflowFactory implements WorkflowFactory, ApplicationContextAware {
    //~ Instance fields ////////////////////////////////////////////////////////

    //////////////////////////////////////////////////~ Instance attributes. //

    /** The applicationContext. */
    private ApplicationContext applicationContext;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setApplicationContext(final ApplicationContext inApplicationContext) {
        this.applicationContext = inApplicationContext;
    }

    public void setLayout(String workflowName, Object layout) {
    }

    public Object getLayout(String workflowName) {
        return null;
    }

    public boolean isModifiable(String name) {
        return false;
    }

    public String getName() {
        return null;
    }

    public Properties getProperties() {
        return null;
    }

    //////////////////////////~ AbstractWorkflowFactory implemented methods. //
    public WorkflowDescriptor getWorkflow(final String inName) {
        return (WorkflowDescriptor) this.applicationContext.getBean(inName);
    }

    public WorkflowDescriptor getWorkflow(String name, boolean validate) throws FactoryException {
        return getWorkflow(name);
    }

    public String[] getWorkflowNames() {
        return this.applicationContext.getBeanDefinitionNames(WorkflowDescriptor.class);
    }

    public void createWorkflow(String name) {
    }

    public void init(Properties p) {
    }

    public void initDone() throws FactoryException {
    }

    public boolean removeWorkflow(final String inName) throws FactoryException {
        throw new FactoryException("Unsupported operation.");
    }

    public void renameWorkflow(String oldName, String newName) {
    }

    public void save() {
    }

    public boolean saveWorkflow(final String inName, final WorkflowDescriptor inDescriptor, final boolean inReplace) throws FactoryException {
        throw new FactoryException("Unsupported operation.");
    }
}
