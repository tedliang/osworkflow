/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import com.mckoi.database.jdbc.MSQLException;

import com.opensymphony.module.propertyset.hibernate.PropertySetItem;

import junit.framework.Assert;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.sql.Connection;
import java.sql.Statement;

import javax.naming.InitialContext;

import javax.sql.DataSource;


/**
 * @author Eric Pugh
 *
 * This helper class populates a test mckoi database.
 */
public class DatabaseHelper {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Log log = LogFactory.getLog(DatabaseHelper.class);
    private static SessionFactory sessionFactory;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Create a new database and initialize it with the specified sql script.
     * @param scriptFile
     */
    public static void createDatabase(String scriptFile) {
        Connection connection;
        Statement statement = null;
        String sqlLine = null;

        try {
            InitialContext context = new InitialContext();
            DataSource ds = (DataSource) context.lookup("jdbc/CreateDS");
            connection = ds.getConnection();
            statement = connection.createStatement();

            String sql = getDatabaseCreationScript(scriptFile);
            String[] sqls = StringUtils.split(sql, ";");

            for (int i = 0; i < sqls.length; i++) {
                sqlLine = StringUtils.stripToEmpty(sqls[i]);
                sqlLine = StringUtils.replace(sqlLine, "\r", "");
                sqlLine = StringUtils.replace(sqlLine, "\n", "");

                //String s = sqls[i];
                if ((sqlLine.length() > 0) && (sqlLine.charAt(0) != '#')) {
                    try {
                        statement.executeQuery(sqlLine);
                    } catch (MSQLException e) {
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

        //  return connection;
    }

    /**
      * Use the default Hibernate *.hbm.xml files.  These build the primary keys
      * based on an identity or sequence, whatever is native to the database.
      * @throws Exception
      */
    public static void exportSchemaForHibernate() throws Exception {
        createDatabase("src/etc/deployment/hibernate/mckoi.sql");

        Configuration configuration = new Configuration();

        //cfg.addClass(HibernateHistoryStep.class);
        URL currentStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateCurrentStep.hbm.xml");
        URL historyStep = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateHistoryStep.hbm.xml");
        URL workflowEntry = DatabaseHelper.class.getResource("/com/opensymphony/workflow/spi/hibernate/HibernateWorkflowEntry.hbm.xml");
        Assert.assertTrue(currentStep != null);
        Assert.assertTrue(historyStep != null);
        Assert.assertTrue(workflowEntry != null);
        configuration.addURL(currentStep);
        configuration.addURL(historyStep);
        configuration.addURL(workflowEntry);
        configuration.addClass(PropertySetItem.class);

        //new SchemaExport(configuration).create(true, false);
        sessionFactory = configuration.buildSessionFactory();
    }

    private static String getDatabaseCreationScript(String scriptFile) throws Exception {
        File file = new File(scriptFile);
        Assert.assertTrue(file.exists());

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        return readTextStream(bis);
    }

    private static String readTextStream(InputStream is) throws Exception {
        //System.out.println("InputStream" + is.toString());
        InputStreamReader reader = new InputStreamReader(is);
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
