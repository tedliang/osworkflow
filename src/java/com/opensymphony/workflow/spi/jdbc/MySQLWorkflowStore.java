/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.jdbc;

import com.opensymphony.workflow.StoreException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;


/**
 * @author Christopher Farnham
 * Created on Feb 27, 2004
 */
public class MySQLWorkflowStore extends JDBCWorkflowStore {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String _stepSequenceIncrement = null;
    private String _stepSequenceRetrieve = null;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void init(Map props) throws StoreException {
        super.init(props);
        _stepSequenceIncrement = (String) props.get("step.sequence.increment");
        _stepSequenceRetrieve = (String) props.get("step.sequence.retrieve");
    }

    protected long getNextStepSequence(Connection c) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(_stepSequenceIncrement);
            stmt.executeUpdate();
            rset = stmt.executeQuery(_stepSequenceRetrieve);

            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }
}
