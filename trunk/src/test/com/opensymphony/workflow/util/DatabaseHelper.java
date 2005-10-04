/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import junit.framework.Assert;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.dialect.MckoiDialect;

import java.io.*;

import java.net.URL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;

import javax.sql.DataSource;


/**
 * @author Eric Pugh
 *
 * This helper class populates a test database.
 */
public class DatabaseHelper {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(DatabaseHelper.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public static org.hibernate.SessionFactory createHibernate3SessionFactory() throws Exception {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration.setProperty("hibernate.dialect", MckoiDialect.class.getName());

        URL currentStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate3/HibernateCurrentStep.hbm.xml");
        URL historyStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate3/HibernateHistoryStep.hbm.xml");
        URL workflowEntry = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate3/HibernateWorkflowEntry.hbm.xml");
        Assert.assertTrue(currentStep != null);
        Assert.assertTrue(historyStep != null);
        Assert.assertTrue(workflowEntry != null);
        configuration.addURL(currentStep);
        configuration.addURL(historyStep);
        configuration.addURL(workflowEntry);

        new org.hibernate.tool.hbm2ddl.SchemaExport(configuration).create(false, true);

        return configuration.buildSessionFactory();
    }

    /**
      * Use the default Hibernate *.hbm.xml files.  These build the primary keys
      * based on an identity or sequence, whatever is native to the database.
      * @throws Exception
      */
    public static SessionFactory createHibernateSessionFactory() throws Exception {
        Configuration configuration = new Configuration();

        //cfg.addClass(HibernateHistoryStep.class);
        URL currentStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateCurrentStep.hbm.xml");
        URL historyStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateHistoryStep.hbm.xml");
        URL workflowEntry = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateWorkflowEntry.hbm.xml");
        URL propertySet = DatabaseHelper.class.getResource("/com/opensymphony/module/propertyset/hibernate/PropertySetItemImpl.hbm.xml");
        Assert.assertTrue(currentStep != null);
        Assert.assertTrue(historyStep != null);
        Assert.assertTrue(workflowEntry != null);
        Assert.assertTrue(propertySet != null);
        configuration.addURL(currentStep);
        configuration.addURL(historyStep);
        configuration.addURL(workflowEntry);
        configuration.addURL(propertySet);

        new SchemaExport(configuration).create(false, true);

        return configuration.buildSessionFactory();
    }

    public static SessionFactory createPropertySetSessionFactory() throws Exception {
        Configuration configuration = new Configuration();

        URL propertySet = DatabaseHelper.class.getResource("/com/opensymphony/module/propertyset/hibernate/PropertySetItemImpl.hbm.xml");

        Assert.assertTrue(propertySet != null);

        configuration.addURL(propertySet);

        new SchemaExport(configuration).create(false, true);

        return configuration.buildSessionFactory();
    }

    /**
     * Create the database by loading a URL pointing at a SQL script.
     */
    public static void runScript(URL url, String dsLocation) {
        Assert.assertNotNull("Database url is null", url);

        try {
            String sql = getDatabaseCreationScript(url);
            runScript(sql, dsLocation);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Create a new database and initialize it with the specified sql script.
     * @param sql the sql to execute
     */
    public static void runScript(String sql, String dsLocation) {
        Connection connection;
        Statement statement = null;
        String sqlLine = null;

        try {
            InitialContext context = new InitialContext();
            DataSource ds = (DataSource) context.lookup(dsLocation);
            connection = ds.getConnection();
            statement = connection.createStatement();

            String[] sqls = StringUtils.split(sql, ";");

            for (int i = 0; i < sqls.length; i++) {
                sqlLine = StringUtils.stripToEmpty(sqls[i]);
                sqlLine = StringUtils.replace(sqlLine, "\r", "");
                sqlLine = StringUtils.replace(sqlLine, "\n", "");

                //String s = sqls[i];
                if ((sqlLine.length() > 0) && (sqlLine.charAt(0) != '#')) {
                    try {
                        statement.executeQuery(sqlLine);
                    } catch (SQLException e) {
                        if (sqlLine.toLowerCase().indexOf("drop") == -1) {
                            log.error("Error executing " + sqlLine, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Database creation error.  sqlLine:" + sqlLine, e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    //not catch
                }
            }
        }
    }

    private static String getDatabaseCreationScript(URL url) throws IOException {
        InputStreamReader reader = new InputStreamReader(url.openStream());
        StringBuffer sb = new StringBuffer(100);
        int c = 0;

        while (c > -1) {
            c = reader.read();

            if (c > -1) {
                sb.append((char) c);
            }
        }

        return sb.toString();
    }
}
