/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ojb;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.workflow.QueryNotSupportedException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.*;

import java.math.BigDecimal;

import java.util.*;


/**
 * @author picard
 * Created on 9 sept. 2003
 */
public class OJBWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(OJBWorkflowStore.class);

    //~ Constructors ///////////////////////////////////////////////////////////

    public OJBWorkflowStore() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntryState(long entryId, int state) throws StoreException {
        PersistenceBroker broker = null;

        try {
            broker = this.getBroker();

            Criteria criteria = new Criteria();
            criteria.addEqualTo("id", new Long(entryId));

            Query query = new QueryByCriteria(OJBWorkflowEntry.class, criteria);
            OJBWorkflowEntry entry = (OJBWorkflowEntry) broker.getObjectByQuery(query);
            entry.setState(state);
            broker.store(entry);
        } catch (Throwable e) {
            throw new StoreException("Error to retrieve entry", new Exception(e));
        } finally {
            if (broker != null) {
                broker.close();
            }
        }
    }

    public PropertySet getPropertySet(long entryId) throws StoreException {
        HashMap args = new HashMap();
        args.put("globalKey", "osff_" + entryId);

        return PropertySetManager.getInstance("ojb", args);
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        PersistenceBroker broker = null;
        OJBCurrentStep step = new OJBCurrentStep();
        OJBWorkflowEntry entry;

        try {
            broker = this.getBroker();

            Criteria criteria = new Criteria();
            criteria.addEqualTo("id", new Long(entryId));

            Query requete = new QueryByCriteria(OJBWorkflowEntry.class, criteria);

            entry = (OJBWorkflowEntry) broker.getObjectByQuery(requete);

            step.setEntry(entry);
            step.setStepId(stepId);
            step.setOwner(owner);
            step.setStartDate(startDate);
            step.setDueDate(dueDate);
            step.setStatus(status);

            List stepIdList = new ArrayList(previousIds.length);

            for (int i = 0; i < previousIds.length; i++) {
                long previousId = previousIds[i];
                stepIdList.add(new Long(previousId));
            }

            if (!stepIdList.isEmpty()) {
                criteria = new Criteria();
                criteria.addIn("id", stepIdList);

                requete = new QueryByCriteria(OJBCurrentStep.class, criteria);

                Collection clPreviousStep = broker.getCollectionByQuery(requete);

                step.setPreviousSteps(new ArrayList(clPreviousStep));
            } else {
                step.setPreviousSteps(Collections.EMPTY_LIST);
            }

            if (entry.getCurrentSteps() == null) {
                ArrayList cSteps = new ArrayList(1);
                cSteps.add(step);
                entry.setCurrentSteps(cSteps);
            } else {
                entry.getCurrentSteps().add(step);
            }

            broker.store(entry);
        } catch (Exception e) {
            step = null;
            throw new StoreException("Error creating new workflow entry", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return step;
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        PersistenceBroker broker = null;

        OJBWorkflowEntry entry = new OJBWorkflowEntry();
        entry.setState(WorkflowEntry.CREATED);
        entry.setWorkflowName(workflowName);

        try {
            broker = this.getBroker();

            broker.store(entry);
        } catch (Exception e) {
            throw new StoreException("Error creating new workflow entry", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return entry;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.spi.WorkflowStore#findCurrentSteps(long)
     */
    public List findCurrentSteps(long entryId) throws StoreException {
        PersistenceBroker broker = null;

        Collection clStep = Collections.EMPTY_LIST;

        try {
            broker = this.getBroker();

            Criteria critere = new Criteria();
            critere.addEqualTo("entry.id", new Long(entryId));

            Query requete = new QueryByCriteria(OJBCurrentStep.class, critere);

            clStep = broker.getCollectionByQuery(requete);
        } catch (Exception e) {
            throw new StoreException("Error to retrieve current steps", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return new ArrayList(clStep);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.spi.WorkflowStore#findEntry(long)
     */
    public WorkflowEntry findEntry(long entryId) throws StoreException {
        PersistenceBroker broker = null;

        WorkflowEntry entry = null;

        try {
            broker = this.getBroker();

            Criteria criteria = new Criteria();
            criteria.addEqualTo("id", new Long(entryId));

            Query query = new QueryByCriteria(OJBWorkflowEntry.class, criteria);

            entry = (OJBWorkflowEntry) broker.getObjectByQuery(query);
        } catch (Throwable e) {
            throw new StoreException("Error to retrieve entry", new Exception(e));
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return entry;
    }

    public List findHistorySteps(long entryId) throws StoreException {
        PersistenceBroker broker = null;

        Collection clStep = Collections.EMPTY_LIST;

        try {
            broker = this.getBroker();

            Criteria critere = new Criteria();
            critere.addEqualTo("entry.id", new Long(entryId));

            Query requete = new QueryByCriteria(OJBHistoryStep.class, critere);

            clStep = broker.getCollectionByQuery(requete);
        } catch (Exception e) {
            throw new StoreException("Error to retrieve history steps", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return new ArrayList(clStep);
    }

    public void init(Map props) throws StoreException {
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        PersistenceBroker broker = null;

        OJBCurrentStep currentStep = (OJBCurrentStep) step;

        try {
            broker = this.getBroker();

            currentStep.setActionId(actionId);
            currentStep.setFinishDate(finishDate);
            currentStep.setStatus(status);
            currentStep.setCaller(caller);

            broker.store(currentStep);
        } catch (Exception e) {
            log.error("An exception occured", e);

            throw new StoreException("Error to store current step", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return step;
    }

    public void moveToHistory(Step step) throws StoreException {
        PersistenceBroker broker = null;

        try {
            broker = this.getBroker();

            Criteria criteria = new Criteria();
            criteria.addEqualTo("id", new Long(step.getEntryId()));

            Query query = new QueryByCriteria(OJBWorkflowEntry.class, criteria);

            OJBWorkflowEntry entry = (OJBWorkflowEntry) broker.getObjectByQuery(query);

            if (entry != null) {
                OJBHistoryStep hstep = new OJBHistoryStep((OJBStep) step);

                entry.getCurrentSteps().remove(step);

                if (entry.getHistorySteps() == null) {
                    ArrayList hSteps = new ArrayList(1);
                    hSteps.add(hstep);
                    entry.setHistorySteps(hSteps);
                } else {
                    entry.getHistorySteps().add(hstep);
                }

                broker.delete(new OJBCurrentStep((OJBStep) step));

                broker.store(entry);
            }
        } catch (Exception e) {
            throw new StoreException("Error to move current step to history", e);
        } finally {
            if (broker != null) {
                broker.close();
            }
        }
    }

    public List query(WorkflowExpressionQuery query) throws StoreException {
        throw new QueryNotSupportedException("OJB Store does not support WorkflowExpressionQuery");
    }

    public List query(WorkflowQuery query) throws StoreException {
        PersistenceBroker broker = null;

        List results = new ArrayList();

        try {
            broker = this.getBroker();

            int qtype = query.getType();

            if (qtype == 0) { // then not set, so look in sub queries
                              // todo: not sure if you would have a query that would look in both old and new, if so, i'll have to change this - TR
                              // but then again, why are there redundant tables in the first place? the data model should probably change

                if (query.getLeft() != null) {
                    qtype = query.getLeft().getType();
                }
            }

            String sel = queryWhere(query);

            if (log.isDebugEnabled()) {
                log.debug(sel);
            }

            Criteria critere = new Criteria();
            critere.addSql(sel);

            ReportQueryByCriteria report;

            if (qtype == WorkflowQuery.CURRENT) {
                report = new ReportQueryByCriteria(OJBCurrentStep.class, critere, true); // true = Select distinct
            } else {
                report = new ReportQueryByCriteria(OJBHistoryStep.class, critere, true); // true = Select distinct
            }

            report.setColumns(new String[] {"entryId"});

            Iterator iter = broker.getReportQueryIteratorByQuery(report);

            while (iter.hasNext()) {
                Object[] obj = (Object[]) iter.next();
                BigDecimal entryId = (BigDecimal) obj[0];

                results.add(new Long(entryId.longValue()));
            }
        } catch (Exception e) {
            throw new StoreException("SQL Exception in query: " + e.getMessage());
        } finally {
            if (broker != null) {
                broker.close();
            }
        }

        return results;
    }

    private PersistenceBroker getBroker() throws PBFactoryException {
        PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();

        return broker;
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
            left = "ACTION_ID";

            break;

        case WorkflowQuery.CALLER:
            left = "CALLER";

            break;

        case WorkflowQuery.FINISH_DATE:
            left = "FINISH_DATE";

            break;

        case WorkflowQuery.OWNER:
            left = "OWNER";

            break;

        case WorkflowQuery.START_DATE:
            left = "START_DATE";

            break;

        case WorkflowQuery.STEP: // stepId
            left = "STEP_ID";

            break;

        case WorkflowQuery.STATUS:
            left = "STATUS";

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
