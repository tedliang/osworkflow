/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.config;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.spi.WorkflowStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.*;

import java.io.InputStream;

import java.net.URL;

import java.util.*;

import javax.xml.parsers.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class DefaultConfiguration implements Configuration {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static DefaultConfiguration INSTANCE = new DefaultConfiguration();
    private static final Log log = LogFactory.getLog(DefaultConfiguration.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private AbstractWorkflowFactory factory = new URLWorkflowFactory();
    private Map persistenceArgs = new HashMap();
    private String persistenceClass;
    private transient WorkflowStore store = null;
    private boolean initialized;

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean isInitialized() {
        return initialized;
    }

    public void setPersistence(String persistence) {
        persistenceClass = persistence;
    }

    public String getPersistence() {
        return persistenceClass;
    }

    public Map getPersistenceArgs() {
        return persistenceArgs;
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
        if (store == null) {
            String clazz = getPersistence();

            log.info("Initializing WorkflowStore: " + clazz);

            try {
                store = (WorkflowStore) Class.forName(clazz).newInstance();
            } catch (Exception ex) {
                throw new StoreException("Error creating store", ex);
            }

            store.init(getPersistenceArgs());
        }

        return store;
    }

    public void load(URL url) throws FactoryException {
        InputStream is = getInputStream(url);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = null;

            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                log.fatal("Could not load config file", e);
            }

            Document doc = db.parse(is);

            Element root = (Element) doc.getElementsByTagName("osworkflow").item(0);
            Element p = (Element) root.getElementsByTagName("persistence").item(0);
            Element factoryElement = (Element) root.getElementsByTagName("factory").item(0);

            persistenceClass = p.getAttribute("class");

            NodeList args = p.getElementsByTagName("property");

            //persistenceArgs = new HashMap();
            for (int i = 0; i < args.getLength(); i++) {
                Element e = (Element) args.item(i);
                persistenceArgs.put(e.getAttribute("key"), e.getAttribute("value"));
            }

            if (factoryElement != null) {
                String clazz = null;

                try {
                    clazz = factoryElement.getAttribute("class");

                    if (clazz == null) {
                        throw new FactoryException("factory does not specify a class attribute");
                    }

                    if (Thread.currentThread().getContextClassLoader() != null) {
                        try {
                            factory = (AbstractWorkflowFactory) Class.forName(clazz, true, Thread.currentThread().getContextClassLoader()).newInstance();
                        } catch (ClassNotFoundException e) {
                            //fall back to this class' classloader.
                            factory = (AbstractWorkflowFactory) Class.forName(clazz).newInstance();
                        }
                    } else {
                        //no context classloader, so use current one
                        factory = (AbstractWorkflowFactory) Class.forName(clazz).newInstance();
                    }

                    Properties properties = new Properties();
                    NodeList props = factoryElement.getElementsByTagName("property");

                    for (int i = 0; i < props.getLength(); i++) {
                        Element e = (Element) props.item(i);
                        properties.setProperty(e.getAttribute("key"), e.getAttribute("value"));
                    }

                    factory.init(properties);
                    factory.initDone();
                } catch (FactoryException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new FactoryException("Error creating workflow factory " + clazz, ex);
                }
            }

            initialized = true;
        } catch (FactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new FactoryException("Error in workflow config", e);
        }
    }

    public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        return factory.saveWorkflow(name, descriptor, replace);
    }

    /**
     * Load the default configuration from the current context classloader.
     * The search order is:
     * <li>Specified URL</li>
     * <li>osworkflow.xml</li>
     * <li>/osworkflow.xml</li>
     * <li>META-INF/osworkflow.xml</li>
     * <li>/META-INF/osworkflow.xml</li>
     */
    protected InputStream getInputStream(URL url) {
        InputStream is = null;

        if (url != null) {
            try {
                is = url.openStream();
            } catch (Exception ex) {
            }
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("META-INF/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/META-INF/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        return is;
    }

    /**
     * Get the workflow factory for this configuration.
     * This method should never ever be called from client code!
     */
    AbstractWorkflowFactory getFactory() {
        return factory;
    }
}
