/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.InvalidWorkflowDescriptorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import java.io.*;

import java.sql.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


/**
 * Workflow Factory that stores workflows in a database.
 * The database requires a property called 'datasource' which is the JNDI
 * name of the datasource for this factory.
 * <p>
 * Also required is a database table called OS_WORKFLOWDEFS with two columns,
 * WF_NAME which contains the workflow name, and WF_DEFINITION which will contain the xml
 * workflow descriptor, the latter can be either a TEXT or BINARY type.
 * <p>
 * Note that this class is provided as an example, and users are encouraged to use
 * their own implementations that are more suited to their particular needs.
 * 
 * @author Hubert Felber, Philipp Hug
 * Date: May 01, 2003
 * Time: 11:17:06 AM
 */
public class JDBCWorkflowFactory extends XMLWorkflowFactory implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(JDBCWorkflowFactory.class);
    final static String wfTable = "OS_WORKFLOWDEFS";
    final static String wfName = "WF_NAME";
    final static String wfDefinition = "WF_DEFINITION";

    //~ Instance fields ////////////////////////////////////////////////////////

    private DataSource ds;
    private Map workflows;
    private boolean reload;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JDBCWorkflowFactory() {
        try {
            init();
        } catch (Exception e) {
            log.fatal("Could not initialize db connection for workflow factory.");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *
     * @param name
     * @return
     */
    public WorkflowDescriptor getWorkflow(String name) {
        WfConfig c = (WfConfig) workflows.get(name);

        if (c == null) {
            throw new RuntimeException("Unknown workflow name \"" + name + "\"");
        }

        if (log.isDebugEnabled()) {
            log.debug("getWorkflow " + name + " descriptor=" + c.descriptor);
        }

        if (c.descriptor != null) {
            if (reload) {
                //@todo check timestamp
                log.debug("Reloading workflow " + name);
                loadWorkflow(c);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Loading workflow " + name);
                loadWorkflow(c);
            }
        }

        return c.descriptor;
    }

    /**
     *
     * @return
     */
    public String[] getWorkflowNames() {
        int i = 0;
        String[] res = new String[workflows.keySet().size()];
        Iterator it = workflows.keySet().iterator();

        while (it.hasNext()) {
            res[i++] = (String) it.next();
        }

        return res;
    }

    public void execute(Map transientVars, Map args, PropertySet ps) {
        String name = (String) args.get("name");
        WorkflowDescriptor wfds = (WorkflowDescriptor) transientVars.get("descriptor");

        try {
            saveWorkflow(name, wfds, false);
        } catch (Exception e) {
        }
    }

    /**
     *
     * @throws FactoryException
     */
    public void initDone() throws FactoryException {
        try {
            reload = getProperties().getProperty("reload", "false").equals("true");

            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT " + wfName + "," + wfDefinition + " FROM " + wfTable);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString(1);
                WfConfig config = new WfConfig(name);
                workflows.put(rs.getString(1), config);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            log.fatal("Could not read workflow names from db.");
            throw new FactoryException(e);
        } catch (Exception e) {
            log.fatal("Could not read workflow names from db.");
            throw new FactoryException(e);
        }
    }

    /**
     *
     * @param workflowname
     * @return
     */
    public byte[] read(String workflowname) {
        byte[] wf = {};

        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT " + wfDefinition + " FROM " + wfTable + " WHERE " + wfName + " = ?");
            ps.setString(1, workflowname);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                wf = rs.getBytes(1);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            log.fatal("Could not read workflow [" + workflowname + "]", e);
        }

        return wf;
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean removeWorkflow(String name) {
        boolean removed = false;

        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM " + wfTable + " WHERE " + wfName + " = ?");
            ps.setString(1, name);

            int rows = ps.executeUpdate();

            if (rows == 1) {
                removed = true;
            }

            ps.close();
            conn.close();
        } catch (SQLException e) {
            log.fatal("Unable to remove workflow [" + name + "]", e);
        }

        return removed;
    }

    /**
     *
     * @param name
     * @param descriptor
     * @param replace
     * @return
     * @throws com.opensymphony.workflow.FactoryException
     */
    public boolean saveWorkflow(String name, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        WfConfig c = (WfConfig) workflows.get(name);

        if ((c != null) && !replace) {
            return false;
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(bout);

        PrintWriter writer = new PrintWriter(out);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE workflow PUBLIC \"-//OpenSymphony Group//DTD OSWorkflow 2.7//EN\" \"http://www.opensymphony.com/osworkflow/workflow_2_7.dtd\">");
        descriptor.writeXML(writer, 0);
        writer.flush();
        writer.close();

        //@todo is a backup necessary?
        return write(name, bout.toByteArray());
    }

    /**
     *
     * @param workflowname
     * @param wf
     * @return
     */
    public boolean write(String workflowname, byte[] wf) {
        boolean written = false;

        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps;

            if (exists(workflowname, conn)) {
                ps = conn.prepareStatement("UPDATE " + wfTable + " SET " + wfDefinition + " = ?" + "WHERE " + wfName + "= ?");

                try {
                    ps.setBytes(1, wf);
                } catch (Exception e) {
                }

                ps.setString(2, workflowname);
            } else {
                ps = conn.prepareStatement("INSERT INTO " + wfTable + " (" + wfName + ", " + wfDefinition + ") VALUES (?, ?)");
                ps.setString(1, workflowname);

                try {
                    ps.setBytes(2, wf);
                } catch (Exception e) {
                }
            }

            ps.executeUpdate();
            ps.close();
            conn.close();
            written = true;
        } catch (SQLException e) {
            log.fatal("Could not write workflow [" + workflowname + "]", e);
        }

        return written;
    }

    /**
     *
     * @param workflowname
     * @param conn
     * @return
     */
    private boolean exists(String workflowname, Connection conn) {
        boolean exists = false;

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT " + wfName + " FROM " + wfTable + " WHERE " + wfName + " = ?");
            ps.setString(1, workflowname);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exists = true;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.fatal("Could not check if [" + workflowname + "] exists", e);
        }

        return exists;
    }



    /**
     *
     * @throws javax.naming.NamingException
     */
    private void init() throws NamingException {
        workflows = new HashMap();

        String dsName = "";

        try {
            ds = (DataSource) new InitialContext().lookup(getProperties().getProperty("datasource"));
        } catch (NamingException e) {
            log.fatal("Could not look up DataSource using JNDI location: " + dsName, e);
            throw (e);
        }
    }

    /**
     *
     * @param wfName
     * @return
     * @throws java.io.IOException
     */
    private WorkflowDescriptor load(final String wfName) throws IOException, SAXException, InvalidWorkflowDescriptorException {
        byte[] wf = read(wfName);

        if (wf == null) {
            throw new IOException();
        }

        ByteArrayInputStream is = new ByteArrayInputStream(wf);

        try {
            return WorkflowLoader.load(is);
        } catch (InvalidWorkflowDescriptorException e) {
            throw e;
        }
    }

    /**
     *
     * @param c
     */
    private void loadWorkflow(WfConfig c) {
        try {
            c.descriptor = load(c.wfName);
        } catch (Exception e) {
            log.fatal("Error creating workflow descriptor" + e.getMessage(), e);
            throw new RuntimeException("Error in workflow descriptor: " + e.getMessage());
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    class WfConfig {
        String wfName;
        WorkflowDescriptor descriptor;
        long lastModified;

        public WfConfig(String name) {
            wfName = name;
        }
    }
}
