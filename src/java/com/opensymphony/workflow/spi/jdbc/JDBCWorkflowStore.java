/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.jdbc;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.util.EJBUtils;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

import java.util.*;
import java.util.Date;

import javax.sql.DataSource;


/**
 * JDBC implementation.
 * <p>
 *
 * The following properties are all <b>required</b>:
 * <ul>
 *  <li><b>datasource</b> - the JNDI location for the DataSource that is to be used.</li>
 *  <li><b>entry.sequence</b> - SQL query that returns the next ID for a workflow entry</li>
 *  <li><b>entry.table</b> - table name for workflow entry</li>
 *  <li><b>entry.id</b> - column name for workflow entry ID field</li>
 *  <li><b>entry.name</b> - column name for workflow entry name field</li>
 *  <li><b>entry.state</b> - column name for workflow entry state field</li>
 *  <li><b>step.sequence</b> - SQL query that returns the next ID for a workflow step</li>
 *  <li><b>history.table</b> - table name for steps in history</li>
 *  <li><b>current.table</b> - table name for current steps</li>
 *  <li><b>step.id</b> - column name for step ID field</li>
 *  <li><b>step.entryId</b> - column name for workflow entry ID field (foreign key relationship to [entry.table].[entry.id])</li>
 *  <li><b>step.stepId</b> - column name for step workflow definition step field</li>
 *  <li><b>step.actionId</b> - column name for step action field</li>
 *  <li><b>step.owner</b> - column name for step owner field</li>
 *  <li><b>step.caller</b> - column name for step caller field</li>
 *  <li><b>step.startDate</b> - column name for step start date field</li>
 *  <li><b>step.dueDate</b> - column name for optional step due date field</li>
 *  <li><b>step.finishDate</b> - column name for step finish date field</li>
 *  <li><b>step.status</b> - column name for step status field</li>
 *  <li><b>currentPrev.table</b> - table name for the previous IDs for current steps</li>
 *  <li><b>historyPrev.table</b> - table name for the previous IDs for history steps</li>
 *  <li><b>step.previousId</b> - column name for step ID field (foreign key relation to [history.table].[step.id] or [current.table].[step.id])</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class JDBCWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(JDBCWorkflowStore.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private DataSource ds;
    private String currentPrevTable;
    private String currentTable;
    private String entryId;
    private String entryName;
    private String entrySequence;
    private String entryState;
    private String entryTable;
    private String historyPrevTable;
    private String historyTable;
    private String stepActionId;
    private String stepCaller;
    private String stepDueDate;
    private String stepEntryId;
    private String stepFinishDate;
    private String stepId;
    private String stepOwner;
    private String stepPreviousId;
    private String stepSequence;
    private String stepStartDate;
    private String stepStatus;
    private String stepStepId;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntryState(long id, int state) throws StoreException {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = ds.getConnection();

            String sql = "UPDATE " + entryTable + " SET " + entryState + " = ? WHERE " + entryId + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, state);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StoreException("Unable to update state for workflow instance #" + id + " to " + state, e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap(1);
        args.put("globalKey", "osff_" + entryId);

        return PropertySetManager.getInstance("jdbc", args);
    }

    public Step createCurrentStep(long entryId, int wfStepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "INSERT INTO " + currentTable + " (" + stepId + "," + stepEntryId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepCaller + " ) VALUES (?, ?, ?, null, ?, ?, ?, null, ?, null)";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            long id = getNextStepSequence(conn);
            stmt.setLong(1, id);
            stmt.setLong(2, entryId);
            stmt.setInt(3, wfStepId);
            stmt.setString(4, owner);
            stmt.setTimestamp(5, new Timestamp(startDate.getTime()));

            if (dueDate != null) {
                stmt.setTimestamp(6, new Timestamp(dueDate.getTime()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }

            stmt.setString(7, status);
            stmt.executeUpdate();

            if ((previousIds != null) && (previousIds.length > 0)) {
                if (!((previousIds.length == 1) && (previousIds[0] == 0))) {
                    sql = "INSERT INTO " + currentPrevTable + " (" + stepId + ", " + stepPreviousId + ") VALUES (?, ?)";
                    log.debug("Executing SQL statement: " + sql);
                    cleanup(null, stmt, null);
                    stmt = conn.prepareStatement(sql);

                    for (int i = 0; i < previousIds.length; i++) {
                        long previousId = previousIds[i];
                        stmt.setLong(1, id);
                        stmt.setLong(2, previousId);
                        stmt.executeUpdate();
                    }
                }
            }

            return new SimpleStep(id, entryId, wfStepId, 0, owner, startDate, dueDate, null, status, previousIds, null);
        } catch (SQLException e) {
            throw new StoreException("Unable to create current step for workflow instance #" + entryId, e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "INSERT INTO " + entryTable + " (" + entryId + ", " + entryName + ", " + entryState + ") VALUES (?,?,?)";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            long id = getNextEntrySequence(conn);
            stmt.setLong(1, id);
            stmt.setString(2, workflowName);
            stmt.setInt(3, WorkflowEntry.CREATED);
            stmt.executeUpdate();

            return new SimpleWorkflowEntry(id, workflowName, WorkflowEntry.CREATED);
        } catch (SQLException e) {
            throw new StoreException("Error creating new workflow instance", e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public List findCurrentSteps(long entryId) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        PreparedStatement stmt2 = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + stepId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepCaller + " FROM " + currentTable + " WHERE " + stepEntryId + " = ?";
            String sql2 = "SELECT " + stepPreviousId + " FROM " + currentPrevTable + " WHERE " + stepId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql2);
            }

            stmt2 = conn.prepareStatement(sql2);
            stmt.setLong(1, entryId);

            rset = stmt.executeQuery();

            ArrayList currentSteps = new ArrayList();

            while (rset.next()) {
                long id = rset.getLong(1);
                int stepId = rset.getInt(2);
                int actionId = rset.getInt(3);
                String owner = rset.getString(4);
                Date startDate = rset.getTimestamp(5);
                Date dueDate = rset.getTimestamp(6);
                Date finishDate = rset.getTimestamp(7);
                String status = rset.getString(8);
                String caller = rset.getString(9);

                ArrayList prevIdsList = new ArrayList();
                stmt2.setLong(1, id);

                ResultSet rs = stmt2.executeQuery();

                while (rs.next()) {
                    long prevId = rs.getLong(1);
                    prevIdsList.add(new Long(prevId));
                }

                long[] prevIds = new long[prevIdsList.size()];
                int i = 0;

                for (Iterator iterator = prevIdsList.iterator();
                        iterator.hasNext();) {
                    Long aLong = (Long) iterator.next();
                    prevIds[i] = aLong.longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, caller);
                currentSteps.add(step);
            }

            return currentSteps;
        } catch (SQLException e) {
            throw new StoreException("Unable to locate current steps for workflow instance #" + entryId, e);
        } finally {
            cleanup(null, stmt2, null);
            cleanup(conn, stmt, rset);
        }
    }

    public WorkflowEntry findEntry(long theEntryId) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + entryName + ", " + entryState + " FROM " + entryTable + " WHERE " + entryId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, theEntryId);

            rset = stmt.executeQuery();
            rset.next();

            String workflowName = rset.getString(1);
            int state = rset.getInt(2);

            return new SimpleWorkflowEntry(theEntryId, workflowName, state);
        } catch (SQLException e) {
            throw new StoreException("Error finding workflow instance #" + entryId);
        } finally {
            cleanup(conn, stmt, rset);
        }
    }

    public List findHistorySteps(long entryId) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + stepId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepCaller + " FROM " + historyTable + " WHERE " + stepEntryId + " = ? ORDER BY " + stepId + " DESC";
            String sql2 = "SELECT " + stepPreviousId + " FROM " + historyPrevTable + " WHERE " + stepId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql2);
            }

            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt.setLong(1, entryId);

            rset = stmt.executeQuery();

            ArrayList currentSteps = new ArrayList();

            while (rset.next()) {
                long id = rset.getLong(1);
                int stepId = rset.getInt(2);
                int actionId = rset.getInt(3);
                String owner = rset.getString(4);
                Date startDate = rset.getTimestamp(5);
                Date dueDate = rset.getTimestamp(6);
                Date finishDate = rset.getTimestamp(7);
                String status = rset.getString(8);
                String caller = rset.getString(9);

                ArrayList prevIdsList = new ArrayList();
                stmt2.setLong(1, id);

                ResultSet rs = stmt2.executeQuery();

                while (rs.next()) {
                    long prevId = rs.getLong(1);
                    prevIdsList.add(new Long(prevId));
                }

                long[] prevIds = new long[prevIdsList.size()];
                int i = 0;

                for (Iterator iterator = prevIdsList.iterator();
                        iterator.hasNext();) {
                    Long aLong = (Long) iterator.next();
                    prevIds[i] = aLong.longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, caller);
                currentSteps.add(step);
            }

            return currentSteps;
        } catch (SQLException e) {
            throw new StoreException("Unable to locate history steps for workflow instance #" + entryId, e);
        } finally {
            cleanup(conn, stmt, rset);
        }
    }

    public void init(Map props) throws StoreException {
        entrySequence = (String) props.get("entry.sequence");
        stepSequence = (String) props.get("step.sequence");
        entryTable = (String) props.get("entry.table");
        entryId = (String) props.get("entry.id");
        entryName = (String) props.get("entry.name");
        entryState = (String) props.get("entry.state");
        historyTable = (String) props.get("history.table");
        currentTable = (String) props.get("current.table");
        currentPrevTable = (String) props.get("currentPrev.table");
        historyPrevTable = (String) props.get("historyPrev.table");
        stepId = (String) props.get("step.id");
        stepEntryId = (String) props.get("step.entryId");
        stepStepId = (String) props.get("step.stepId");
        stepActionId = (String) props.get("step.actionId");
        stepOwner = (String) props.get("step.owner");
        stepCaller = (String) props.get("step.caller");
        stepStartDate = (String) props.get("step.startDate");
        stepFinishDate = (String) props.get("step.finishDate");
        stepDueDate = (String) props.get("step.dueDate");
        stepStatus = (String) props.get("step.status");
        stepPreviousId = (String) props.get("step.previousId");

        String jndi = (String) props.get("datasource");

        try {
            ds = (DataSource) EJBUtils.lookup(jndi);

            if (ds == null) {
                ds = (DataSource) new javax.naming.InitialContext().lookup(jndi);
            }
        } catch (Exception e) {
            throw new StoreException("Error looking up DataSource at " + jndi, e);
        }

        if (ds == null) {
            throw new StoreException("No DataSource found at " + jndi);
        }
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "UPDATE " + currentTable + " SET " + stepStatus + " = ?, " + stepActionId + " = ?, " + stepFinishDate + " = ?, " + stepCaller + " = ? WHERE " + stepId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setLong(2, actionId);
            stmt.setTimestamp(3, new Timestamp(finishDate.getTime()));
            stmt.setString(4, caller);
            stmt.setLong(5, step.getId());
            stmt.executeUpdate();

            SimpleStep theStep = (SimpleStep) step;
            theStep.setActionId(actionId);
            theStep.setFinishDate(finishDate);
            theStep.setStatus(status);
            theStep.setCaller(caller);

            return theStep;
        } catch (SQLException e) {
            throw new StoreException("Unable to mark step finished for #" + step.getEntryId(), e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public void moveToHistory(Step step) throws StoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "INSERT INTO " + historyTable + " (" + stepId + "," + stepEntryId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepCaller + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.setLong(2, step.getEntryId());
            stmt.setInt(3, step.getStepId());
            stmt.setInt(4, step.getActionId());
            stmt.setString(5, step.getOwner());
            stmt.setTimestamp(6, new Timestamp(step.getStartDate().getTime()));
            stmt.setTimestamp(7, new Timestamp(step.getFinishDate().getTime()));
            stmt.setString(8, step.getStatus());
            stmt.setString(9, step.getCaller());
            stmt.executeUpdate();

            long[] previousIds = step.getPreviousStepIds();

            if ((previousIds != null) && (previousIds.length > 0)) {
                sql = "INSERT INTO " + historyPrevTable + " (" + stepId + ", " + stepPreviousId + ") VALUES (?, ?)";
                log.debug("Executing SQL statement: " + sql);
                cleanup(null, stmt, null);
                stmt = conn.prepareStatement(sql);

                for (int i = 0; i < previousIds.length; i++) {
                    long previousId = previousIds[i];
                    stmt.setLong(1, step.getId());
                    stmt.setLong(2, previousId);
                    stmt.executeUpdate();
                }
            }

            sql = "DELETE FROM " + currentPrevTable + " WHERE " + stepId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            cleanup(null, stmt, null);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.executeUpdate();

            sql = "DELETE FROM " + currentTable + " WHERE " + stepId + " = ?";

            if (log.isDebugEnabled()) {
                log.debug("Executing SQL statement: " + sql);
            }

            cleanup(null, stmt, null);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StoreException("Unable to move current step to history step for #" + step.getEntryId(), e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public List query(WorkflowQuery query) throws StoreException {
        List results = new ArrayList();

        // going to try to do all the comparisons in one query
        String sel;
        String table;

        int qtype = query.getType();

        if (qtype == 0) { // then not set, so look in sub queries
                          // todo: not sure if you would have a query that would look in both old and new, if so, i'll have to change this - TR
                          // but then again, why are there redundant tables in the first place? the data model should probably change

            if (query.getLeft() != null) {
                qtype = query.getLeft().getType();
            }
        }

        if (qtype == WorkflowQuery.CURRENT) {
            table = currentTable;
        } else {
            table = historyTable;
        }

        sel = "SELECT DISTINCT(" + stepEntryId + ") FROM " + table + " WHERE ";
        sel += queryWhere(query);

        if (log.isDebugEnabled()) {
            log.debug(sel);
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sel);

            while (rs.next()) {
                // get entryIds and add to results list
                Long id = new Long(rs.getLong(stepEntryId));
                results.add(id);
            }
        } catch (SQLException ex) {
            throw new StoreException("SQL Exception in query: " + ex.getMessage());
        } finally {
            cleanup(conn, stmt, rs);
        }

        return results;
    }

    protected Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    protected long getNextEntrySequence(Connection c) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("Executing SQL statement: " + entrySequence);
        }

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(entrySequence);
            rset = stmt.executeQuery();
            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }

    protected long getNextStepSequence(Connection c) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("Executing SQL statement: " + stepSequence);
        }

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(stepSequence);
            rset = stmt.executeQuery();
            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }

    private void cleanup(Connection connection, Statement statement, ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException ex) {
                log.error("Error closing resultset", ex);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                log.error("Error closing statement", ex);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                log.error("Error closing connection", ex);
            }
        }
    }

    private static String escape(String s) {
        StringBuffer sb = new StringBuffer(s);

        char c;
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            c = chars[i];

            switch (c) {
            case '\'':
                sb.insert(i, '\'');
                i++;

                break;

            case '\\':
                sb.insert(i, '\\');
                i++;
            }
        }

        return sb.toString();
    }

    private String queryComparison(WorkflowQuery query) {
        Object value = query.getValue();
        int operator = query.getOperator();
        int field = query.getField();

        //int type = query.getType();
        String oper;

        switch (operator) {
        case WorkflowQuery.EQUALS:
            oper = " = ";

            break;

        case WorkflowQuery.NOT_EQUALS:
            oper = " <> ";

            break;

        case WorkflowQuery.GT:
            oper = " > ";

            break;

        case WorkflowQuery.LT:
            oper = " < ";

            break;

        default:
            oper = " = ";
        }

        String left;
        String right;

        switch (field) {
        case WorkflowQuery.ACTION: // actionId
            left = stepActionId;

            break;

        case WorkflowQuery.CALLER:
            left = stepCaller;

            break;

        case WorkflowQuery.FINISH_DATE:
            left = stepFinishDate;

            break;

        case WorkflowQuery.OWNER:
            left = stepOwner;

            break;

        case WorkflowQuery.START_DATE:
            left = stepStartDate;

            break;

        case WorkflowQuery.STEP: // stepId
            left = stepStepId;

            break;

        case WorkflowQuery.STATUS:
            left = stepStatus;

            break;

        default:
            left = "1";
        }

        if (value != null) {
            right = "'" + escape(value.toString()) + "'";
        } else {
            right = "null";
        }

        return left + oper + right;
    }

    private String queryWhere(WorkflowQuery query) {
        if (query.getLeft() == null) {
            // leaf node
            return queryComparison(query);
        } else {
            int operator = query.getOperator();
            WorkflowQuery left = query.getLeft();
            WorkflowQuery right = query.getRight();

            switch (operator) {
            case WorkflowQuery.AND:
                return "(" + queryWhere(left) + " AND " + queryWhere(right) + ")";

            case WorkflowQuery.OR:
                return "(" + queryWhere(left) + " OR " + queryWhere(right) + ")";

            case WorkflowQuery.XOR:
                return "(" + queryWhere(left) + " XOR " + queryWhere(right) + ")";
            }
        }

        return ""; // not sure if we should throw an exception or how this should be handled
    }
}