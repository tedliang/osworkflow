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
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    private static Configuration configuration;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return
     */
    public static Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

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
                if (sqlLine.length() > 0) {
                    try {
                        statement.executeQuery(sqlLine);
                    } catch (MSQLException msqlEx) {
                        // Eat any drop tables that fail.  The IF EXISTS clause doesn't seem to work.
                        if (msqlEx.getMessage().indexOf("does not exist") == -1) {
                            throw msqlEx;
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
        Configuration configuration = new Configuration();

        //cfg.addClass(HibernateHistoryStep.class);
        File fileHibernateCurrentStep = new File("src/java/com/opensymphony/workflow/spi/hibernate/HibernateCurrentStep.hbm.xml");
        File fileHibernateHistoryStep = new File("src/java/com/opensymphony/workflow/spi/hibernate/HibernateHistoryStep.hbm.xml");
        File fileHibernateWorkflowEntry = new File("src/java/com/opensymphony/workflow/spi/hibernate/HibernateWorkflowEntry.hbm.xml");
        Assert.assertTrue(fileHibernateCurrentStep.exists());
        Assert.assertTrue(fileHibernateHistoryStep.exists());
        Assert.assertTrue(fileHibernateWorkflowEntry.exists());
        configuration.addFile(fileHibernateCurrentStep);
        configuration.addFile(fileHibernateHistoryStep);
        configuration.addFile(fileHibernateWorkflowEntry);
        configuration.addClass(PropertySetItem.class);

        //    Use SchemaExport to see what Hibernate would have created!
        // Seems to need to do both, I think due to the createDS having the key
        // to create the database, but the config in hibernate not having the key..?
        createDatabase("src/etc/deployment/hibernate/mckoi.sql");
        new SchemaExport(configuration).create(true, true);
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
