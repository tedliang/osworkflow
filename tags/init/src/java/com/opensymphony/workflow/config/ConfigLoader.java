/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: Apr 29, 2002
 * Time: 12:16:00 PM
 */
package com.opensymphony.workflow.config;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.loader.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.*;

import java.io.InputStream;

import java.util.*;

import javax.xml.parsers.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1.1.1 $
 */
public class ConfigLoader {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static String persistence;
    public static Map persistenceArgs;
    private static final Log log = LogFactory.getLog(ConfigLoader.class);
    private static AbstractWorkflowFactory factory = new URLWorkflowFactory();

    //~ Methods ////////////////////////////////////////////////////////////////

    public static WorkflowDescriptor getWorkflow(String name) throws FactoryException {
        WorkflowDescriptor workflow = factory.getWorkflow(name);

        if (workflow == null) {
            throw new FactoryException("Unknown workflow name");
        }

        return workflow;
    }

    public static String[] getWorkflowNames() throws FactoryException {
        return factory.getWorkflowNames();
    }

    public static void load(InputStream is) throws FactoryException {
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

            persistence = p.getAttribute("class");

            NodeList args = p.getElementsByTagName("property");
            persistenceArgs = new HashMap();

            for (int i = 0; i < args.getLength(); i++) {
                Element e = (Element) args.item(i);
                persistenceArgs.put(e.getAttribute("key"), e.getAttribute("value"));
            }

            if (factoryElement != null) {
                String clazz = null;

                try {
                    clazz = factoryElement.getAttribute("class");

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
                } catch (Exception ex) {
                    throw new FactoryException("Error creating workflow factory " + clazz + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            throw new FactoryException("Error in workflow config", e);
        }
    }

    public static boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        return factory.saveWorkflow(name, descriptor, replace);
    }

    /**
     * This method should never ever be called from client code!
     * @return
     */
    static AbstractWorkflowFactory getFactory() {
        return factory;
    }
}
