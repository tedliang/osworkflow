/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.FactoryException;

import java.io.*;

import java.util.Iterator;
import java.util.Map;


/**
 * @author Hani Suleiman
 *         Date: Dec 17, 2004
 *         Time: 12:00:36 AM
 */
public class HTTPWorkflowFactory extends AbstractWorkflowFactory {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Map workflows;
    protected boolean reload;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setLayout(String workflowName, Object layout) {
    }

    public Object getLayout(String workflowName) {
        return null;
    }

    public boolean isModifiable(String name) {
        return true;
    }

    public String getName() {
        return null;
    }

    public WorkflowDescriptor getWorkflow(String name) throws FactoryException {
        HTTPWorkflowConfig c = (HTTPWorkflowConfig) workflows.get(name);

        if (c == null) {
            throw new FactoryException("Unknown workflow name \"" + name + '\"');
        }

        if (c.descriptor != null) {
            loadWorkflow(c);
        }

        c.descriptor.setName(name);

        return c.descriptor;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.loader.AbstractWorkflowFactory#getWorkflowNames()
     */
    public String[] getWorkflowNames() throws FactoryException {
        int i = 0;
        String[] res = new String[workflows.keySet().size()];
        Iterator it = workflows.keySet().iterator();

        while (it.hasNext()) {
            res[i++] = (String) it.next();
        }

        return res;
    }

    public void createWorkflow(String name) {
    }

    public void deleteWorkflow(String name) {
    }

    public void initDone() throws FactoryException {
    }

    public boolean removeWorkflow(String name) throws FactoryException {
        throw new FactoryException("remove workflow not supported");
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.loader.AbstractWorkflowFactory#renameWorkflow(java.lang.String, java.lang.String)
     */
    public void renameWorkflow(String oldName, String newName) {
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.loader.AbstractWorkflowFactory#save()
     */
    public void save() {
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.loader.AbstractWorkflowFactory#saveWorkflow(java.lang.String, com.opensymphony.workflow.loader.WorkflowDescriptor, boolean)
     */
    public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        HTTPWorkflowConfig c = (HTTPWorkflowConfig) workflows.get(name);

        if ((c != null) && !replace) {
            return false;
        }

        if (c == null) {
            throw new UnsupportedOperationException("Saving of new workflow is not currently supported");
        }

        Writer out;

        // [KAP] comment this line to disable all the validation while saving a workflow
        //descriptor.validate();
        try {
            out = new OutputStreamWriter(null, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new FactoryException("utf-8 encoding not supported, contact your JVM vendor!");
        }

        writeXML(descriptor, out);

        //write it out to a new file, to ensure we don't end up with a messed up file if we're interrupted halfway for some reason
        //now lets rename
        return true;
    }

    protected String readLayoutBuffer(final String service_addr, final String docId) throws Exception {
        String sWorkflowBuffer;
        String sLayoutBuffer = null;

        //    OSWorkflowServiceServiceLocator service = new OSWorkflowServiceServiceLocator();
        //    service.setOSWorkflowServiceEndpointAddress(service_addr);
        //    OSWorkflowService srv = service.getOSWorkflowService();
        //    String ret = srv.downloadWorkflow(docId);
        //    if (ret.equals("ERROR"))
        //    {
        //      System.out.println("File not downloaded!");
        //      return null;
        //    }
        //    System.out.println("File downloaded OK!");
        //    int junction = ret.indexOf(x2lSeparator);
        //    sWorkflowBuffer = ret.substring(0,junction);
        //    sLayoutBuffer = ret.substring(junction + x2lSeparator.length());
        return sLayoutBuffer;
    }

    protected String readWorkflowBuffer(final String service_addr, final String docId) throws Exception {
        String sWorkflowBuffer = null;

        //    String sLayoutBuffer;
        //
        //    OSWorkflowServiceServiceLocator service = new OSWorkflowServiceServiceLocator();
        //    service.setOSWorkflowServiceEndpointAddress(service_addr);
        //    OSWorkflowService srv = service.getOSWorkflowService();
        //    String ret = srv.downloadWorkflow(docId);
        //    if (ret.equals("ERROR"))
        //    {
        //      System.out.println("File not downloaded!");
        //      return null;
        //    }
        //    System.out.println("File downloaded OK!");
        //    int junction = ret.indexOf(x2lSeparator);
        //    sWorkflowBuffer = ret.substring(0,junction);
        //    sLayoutBuffer = ret.substring(junction + x2lSeparator.length());
        return sWorkflowBuffer;
    }

    protected String writeWorkflowBuffer(final String service_addr, final String docId, final String name, final String workflowBuffer, final String layoutBuffer) throws Exception {
        String ret = null;

        //    OSWorkflowServiceServiceLocator service = new OSWorkflowServiceServiceLocator();
        //    service.setOSWorkflowServiceEndpointAddress(service_addr);
        //    OSWorkflowService srv = service.getOSWorkflowService();
        //    if ((docId==null)||(docId.length()==0))
        //      ret = srv.uploadWorkflow(name, workflowBuffer + x2lSeparator + layoutBuffer, true);
        //    else
        //      ret = srv.uploadWorkflow(docId, workflowBuffer + x2lSeparator + layoutBuffer, false);
        //    if (ret.equals(""))
        //    {
        //      System.out.println("File not uploaded!");
        //      return "";
        //    }
        //    System.out.println("File uploaded OK!");
        return ret;
    }

    protected void writeXML(WorkflowDescriptor descriptor, Writer out) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(out));
        writer.println(WorkflowDescriptor.XML_HEADER);
        writer.println(WorkflowDescriptor.DOCTYPE_DECL);
        descriptor.writeXML(writer, 0);
        writer.flush();
        writer.close();
    }

    private void loadWorkflow(HTTPWorkflowConfig c) throws FactoryException {
        /*
        try
        {
          c.descriptor = WorkflowLoader.load(c.url);
        }
        catch (Exception e)
        {
          throw new FactoryException("Error in workflow descriptor: " + c.url, e);
        }
        */
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    static class HTTPWorkflowConfig {
        String docId;
        String name;
        String service_addr;
        WorkflowDescriptor descriptor;

        //long lastModified;
        public HTTPWorkflowConfig(String service_addr, String name, String docId) {
            this.service_addr = service_addr;
            this.name = name;
            this.docId = docId;
        }
    }
}
