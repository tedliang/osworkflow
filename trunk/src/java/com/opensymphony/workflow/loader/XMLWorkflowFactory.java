/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidWorkflowDescriptorException;

import org.w3c.dom.*;

import java.io.*;

import java.net.URL;

import java.util.*;

import javax.xml.parsers.*;


/**
 * @author Hani Suleiman
 * Date: May 10, 2002
 * Time: 11:30:41 AM
 */
public class XMLWorkflowFactory extends AbstractWorkflowFactory {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Map workflows;
    protected boolean reload;

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean isModifiable(String name) {
        return true;
    }

    public WorkflowDescriptor getWorkflow(String name) throws FactoryException {
        WorkflowConfig c = (WorkflowConfig) workflows.get(name);

        if (c == null) {
            throw new FactoryException("Unknown workflow name \"" + name + "\"");
        }

        if (c.descriptor != null) {
            if (reload) {
                File file = new File(c.url.getFile());

                if (file.exists() && (file.lastModified() > c.lastModified)) {
                    c.lastModified = file.lastModified();
                    loadWorkflow(c);
                }
            }
        } else {
            loadWorkflow(c);
        }

        return c.descriptor;
    }

    public String[] getWorkflowNames() {
        int i = 0;
        String[] res = new String[workflows.keySet().size()];
        Iterator it = workflows.keySet().iterator();

        while (it.hasNext()) {
            res[i++] = (String) it.next();
        }

        return res;
    }

    public void initDone() throws FactoryException {
        reload = getProperties().getProperty("reload", "false").equals("true");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = null;
        String name = getProperties().getProperty("resource", "workflows.xml");

        if ((name != null) && (name.indexOf(":/") > -1)) {
            try {
                is = new URL(name).openStream();
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream(name);
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/" + name);
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("META-INF/" + name);
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/META-INF/" + name);
            } catch (Exception e) {
            }
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db;

            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new FactoryException("Error creating document builder", e);
            }

            Document doc = db.parse(is);

            Element root = (Element) doc.getElementsByTagName("workflows").item(0);
            workflows = new HashMap();

            List list = XMLUtil.getChildElements(root, "workflow");

            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                WorkflowConfig config = new WorkflowConfig(e.getAttribute("type"), e.getAttribute("location"));
                workflows.put(e.getAttribute("name"), config);
            }
        } catch (Exception e) {
            throw new InvalidWorkflowDescriptorException("Error in workflow config", e);
        }
    }

    public boolean removeWorkflow(String name) throws FactoryException {
        throw new FactoryException("remove workflow not supported");

        //WorkflowConfig workflow = (WorkflowConfig)workflows.remove(name);
        //if(workflow == null) return false;
        //if(workflow.descriptor != null)
        //{
        //
        //}
        //return true;
    }

    public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        WorkflowConfig c = (WorkflowConfig) workflows.get(name);

        if ((c != null) && !replace) {
            return false;
        }

        if (c == null) {
            throw new UnsupportedOperationException("Saving of new workflow is not currently supported");
        }

        Writer out;
        descriptor.validate();

        try {
            out = new OutputStreamWriter(new FileOutputStream(c.url.getFile() + ".new"), "utf-8");
        } catch (FileNotFoundException ex) {
            throw new FactoryException("Could not create new file to save workflow " + c.url.getFile());
        } catch (UnsupportedEncodingException ex) {
            throw new FactoryException("utf-8 encoding not supported, contact your JVM vendor!");
        }

        writeXML(descriptor, out);

        //write it out to a new file, to ensure we don't end up with a messed up file if we're interrupted halfway for some reason
        //now lets rename
        File original = new File(c.url.getFile());
        File backup = new File(c.url.getFile() + ".bak");
        File updated = new File(c.url.getFile() + ".new");
        boolean isOK = !original.exists() || original.renameTo(backup);

        if (!isOK) {
            throw new FactoryException("Unable to backup original workflow file " + original + " to " + backup + ", aborting save");
        }

        isOK = updated.renameTo(original);

        if (!isOK) {
            throw new FactoryException("Unable to rename new  workflow file " + updated + " to " + original + ", aborting save");
        }

        backup.delete();

        return true;
    }

    protected void save() {
    }

    protected void writeXML(WorkflowDescriptor descriptor, Writer out) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(out));
        writer.println(WorkflowDescriptor.XML_HEADER);
        writer.println(WorkflowDescriptor.DOCTYPE_DECL);
        descriptor.writeXML(writer, 0);
        writer.flush();
        writer.close();
    }

    private void loadWorkflow(WorkflowConfig c) throws FactoryException {
        try {
            c.descriptor = WorkflowLoader.load(c.url);
        } catch (Exception e) {
            throw new FactoryException("Error in workflow descriptor: " + c.url, e);
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    static class WorkflowConfig {
        String location;
        String type;
        URL url;
        WorkflowDescriptor descriptor;
        long lastModified;

        public WorkflowConfig(String type, String location) {
            if ("URL".equals(type)) {
                try {
                    url = new URL(location);

                    File file = new File(url.getFile());

                    if (file.exists()) {
                        lastModified = file.lastModified();
                    }
                } catch (Exception ex) {
                }
            } else if ("file".equals(type)) {
                try {
                    File file = new File(location);
                    url = file.toURL();
                    lastModified = file.lastModified();
                } catch (Exception ex) {
                }
            } else {
                url = Thread.currentThread().getContextClassLoader().getResource(location);
            }

            this.type = type;
            this.location = location;
        }
    }
}
