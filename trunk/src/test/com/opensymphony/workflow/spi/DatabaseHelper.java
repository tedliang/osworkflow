/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


/**
 * @author Eric Pugh
 *
 * This helper class populates our various databases. Using the hibernate schemaexport
 * allows us to easily export to multiple databases.
 */
public class DatabaseHelper {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Log log = LogFactory.getLog(DatabaseHelper.class);
    private static boolean databaseBuilt = false;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Build the database for the JDBC SPI.  It requires that there
     * not be any primary keys, so use a different type.
     * @throws Exception
     */
    public static void exportSchemaForJDBC() throws Exception {
        openDatabase();
    }

    private static String getDatabaseCreationScript() throws Exception {
        File file = new File("src/etc/deployment/jdbc/mckoi.sql");
        Assert.assertTrue(file.exists());

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        return readTextStream(bis);
    }

    private static void openDatabase() {
        if (databaseBuilt) {
            return;
        }

        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.mckoi.JDBCDriver");
            connection = DriverManager.getConnection("jdbc:mckoi:local://./src/test/mcKoi.conf?create=true", "test", "test");
            statement = connection.createStatement();

            String sql = getDatabaseCreationScript();
            String[] sqls = StringUtils.split(sql, ";");

            for (int i = 0; i < sqls.length; i++) {
                String s = StringUtils.stripToEmpty(sqls[i]);
                s = StringUtils.replace(s, "\r", "");
                s = StringUtils.replace(s, "\n", "");

                //String s = sqls[i];
                if (s.length() > 0) {
                    statement.executeQuery(s);
                }
            }
        } catch (Exception e) {
            log.error("Database did't go off perfect:" + e.getMessage());

            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    //not catch
                }
            }
        }

        databaseBuilt = true;

        //  return connection;
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
