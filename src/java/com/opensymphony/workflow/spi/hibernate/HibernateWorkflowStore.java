/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.QueryNotSupportedException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A workflow store backed by Hibernate for persistence.  To use this with the standard
 * persistence factory, pass to the ConfigLoader.persistenceArgs the SessionFactory to
 * use:
 * <code>ConfigLoader.persistenceArgs.put("sessionFactory", DatabaseHelper.getSessionFactory());</code>
 * See the HibernateFunctionalWorkflowTestCase for more help.
 *
 * @author $Author: hani $
 * @version $Revision: 1.10 $
 */
public class HibernateWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(HibernateWorkflowStore.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    Session session;
    SessionFactory sessionFactory;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateWorkflowStore() {
    }

    public HibernateWorkflowStore(SessionFactory sessionFactory) throws StoreException {
        this.sessionFactory = sessionFactory;

        try {
            this.session = sessionFactory.openSession();
        } catch (HibernateException he) {
            log.error("constructor", he);
            throw new StoreException("constructor", he);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntryState(long entryId, int state) throws StoreException {
        try {
            HibernateWorkflowEntry entry = (HibernateWorkflowEntry) session.find("FROM entry IN CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
            entry.setState(state);
            session.save(entry);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return;
        }
    }

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(sessionFactory);

        args.put("configurationProvider", configurationProvider);

        return PropertySetManager.getInstance("hibernate", args);
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        HibernateCurrentStep step = new HibernateCurrentStep();
        HibernateWorkflowEntry entry;

        Transaction tx;

        try {
            tx = session.beginTransaction();
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException he) {
            log.error("Looking for workflow entry " + entryId, he);
            throw new StoreException("Looking for workflow entry " + entryId, he);
        }

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
            String stepIds = TextUtils.join(", ", stepIdList);

            try {
                step.setPreviousSteps(session.find("FROM step in CLASS " + HibernateCurrentStep.class.getName() + " WHERE step.id IN (" + stepIds + ")"));
            } catch (HibernateException he) {
                log.error("Looking for step in " + stepIds, he);
                throw new StoreException("Looking for step in " + stepIds, he);
            }
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

        try {
            session.save(entry);
            tx.commit();

            //session.save(step);
            return step;
        } catch (HibernateException he) {
            log.error("Saving new workflow entry", he);
            throw new StoreException("Saving new workflow entry", he);
        }
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        HibernateWorkflowEntry entry = new HibernateWorkflowEntry();
        entry.setState(WorkflowEntry.CREATED);
        entry.setWorkflowName(workflowName);

        Transaction tx;

        try {
            tx = session.beginTransaction();
            session.save(entry);
            tx.commit();
        } catch (HibernateException he) {
            log.error("Saving new workflow entry", he);
            throw new StoreException("Saving new workflow entry", he);
        }

        return entry;
    }

    public List findCurrentSteps(long entryId) throws StoreException {
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException he) {
            log.error("Looking for entryId " + entryId, he);
            throw new StoreException("Looking for entryId " + entryId, he);
        }

        try {
            return session.find("FROM step IN CLASS " + HibernateCurrentStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.association(entry.getClass()));
        } catch (HibernateException he) {
            log.error("Looking for step id" + entry, he);
            throw new StoreException("Looking for step id" + entry, he);
        }
    }

    public WorkflowEntry findEntry(long entryId) throws StoreException {
        try {
            List result = session.find("FROM entry IN CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG);

            return (WorkflowEntry) result.get(0);
        } catch (HibernateException he) {
            log.error("Looking for entry " + entryId, he);
            throw new StoreException("Loooking for entry " + entryId, he);
        }
    }

    public List findHistorySteps(long entryId) throws StoreException {
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException he) {
            log.error("Finding entry " + entryId, he);
            throw new StoreException("Finding entry " + entryId, he);
        }

        try {
            return session.find("FROM step IN CLASS " + HibernateHistoryStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.association(entry.getClass()));
        } catch (HibernateException he) {
            log.error("Looking for step with entry " + entry, he);
            throw new StoreException("Looking for step with entry " + entry, he);
        }
    }

    public void init(Map props) throws StoreException {
        try {
            //if(1==2){
            sessionFactory = (SessionFactory) props.get("sessionFactory");
            session = sessionFactory.openSession();

            //}
        } catch (HibernateException he) {
            log.error("Setting sessionFactory", he);
            throw new StoreException("Setting sessionFactory", he);
        }
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        HibernateCurrentStep currentStep = (HibernateCurrentStep) step;

        currentStep.setActionId(actionId);
        currentStep.setFinishDate(finishDate);
        currentStep.setStatus(status);
        currentStep.setCaller(caller);

        try {
            Transaction tx = session.beginTransaction();
            session.save(currentStep);
            tx.commit();

            return currentStep;
        } catch (HibernateException he) {
            log.error("Saving current step with action " + actionId, he);
            throw new StoreException("Saving current step with action " + actionId, he);
        }
    }

    public void moveToHistory(Step step) throws StoreException {
        HibernateWorkflowEntry entry;

        Transaction tx;

        try {
            tx = session.beginTransaction();
            entry = (HibernateWorkflowEntry) session.find("FROM entry IN CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(step.getEntryId()), Hibernate.LONG).get(0);
        } catch (HibernateException he) {
            log.error("Looking for workflow entry " + step.getEntryId(), he);
            throw new StoreException("Looking for workflow entry " + step.getEntryId(), he);
        }

        HibernateHistoryStep hStep = new HibernateHistoryStep((HibernateStep) step);

        entry.getCurrentSteps().remove(step);

        if (entry.getHistorySteps() == null) {
            ArrayList hSteps = new ArrayList(1);
            hSteps.add(hStep);
            entry.setHistorySteps(hSteps);
        } else {
            entry.getHistorySteps().add(hStep);
        }

        try {
            session.save(entry);
            tx.commit();

            //session.delete(step);
            //session.save(hStep, new Long(hStep.getId()));
        } catch (HibernateException he) {
            log.error("Saving workflow entry " + entry.getId(), he);
            throw new StoreException("Saving workflow entry " + entry.getId(), he);
        }
    }

    public List query(WorkflowExpressionQuery query) throws StoreException {
        throw new QueryNotSupportedException("Hibernate Store does not support WorkflowExpressionQuery");
    }

    public List query(WorkflowQuery query) throws StoreException {
        List results = new ArrayList();

        // going to try to do all the comparisons in one query
        String sel;
        Class entityClass;

        int qtype = query.getType();

        if (qtype == 0) { // then not set, so look in sub queries

            if (query.getLeft() != null) {
                qtype = query.getLeft().getType();
            }
        }

        if (qtype == WorkflowQuery.CURRENT) {
            entityClass = HibernateCurrentStep.class;
        } else {
            entityClass = HibernateHistoryStep.class;
        }

        Criteria criteria = session.createCriteria(entityClass);
        criteria.add(buildExpression(query));

        //get results and send them back
        return results;
    }

    /**
     * Returns an expression generated from this query
     */
    private Expression getExpression(WorkflowQuery query) {
        int operator = query.getOperator();

        switch (operator) {
        case WorkflowQuery.EQUALS:
            return Expression.eq(getFieldName(query.getField()), getSafeValue(query.getValue()));

        case WorkflowQuery.NOT_EQUALS:
            return Expression.not(Expression.like(getFieldName(query.getField()), getSafeValue(query.getValue())));

        case WorkflowQuery.GT:
            return Expression.gt(getFieldName(query.getField()), getSafeValue(query.getValue()));

        case WorkflowQuery.LT:
            return Expression.lt(getFieldName(query.getField()), getSafeValue(query.getValue()));

        default:
            return Expression.eq(getFieldName(query.getField()), getSafeValue(query.getValue()));
        }
    }

    /**
     * returns the correct name of the field given or "1" if none is found
     * which matches the input.
     * @param field
     * @return
     */
    private String getFieldName(int field) {
        switch (field) {
        case WorkflowQuery.ACTION: // actionId
            return "actionId";

        case WorkflowQuery.CALLER:
            return "caller";

        case WorkflowQuery.FINISH_DATE:
            return "finishDate";

        case WorkflowQuery.OWNER:
            return "owner";

        case WorkflowQuery.START_DATE:
            return "startDate";

        case WorkflowQuery.STEP: // stepId
            return "stepId";

        case WorkflowQuery.STATUS:
            return "status";

        default:
            return "1";
        }
    }

    /**
     * Returns a useabley formatted version of this object.
     * i.e. a toString() is done and escape sequences are removed
     * @param value
     * @return
     */
    private String getSafeValue(Object value) {
        if (value != null) {
            return "'" + escape(value.toString()) + "'";
        } else {
            return "null";
        }
    }

    /**
     *  Recursive method for building Expressions using Query objects.
     */
    private Expression buildExpression(WorkflowQuery query) throws StoreException {
        if (query.getLeft() == null) {
            if (query.getRight() == null) {
                return getExpression(query); //leaf node
            } else {
                throw new StoreException("Invalid WorkflowQuery object.  QueryLeft is null but QueryRight is not.");
            }
        } else {
            if (query.getRight() == null) {
                throw new StoreException("Invalid WorkflowQuery object.  QueryLeft is not null but QueryRight is.");
            }

            int operator = query.getOperator();
            WorkflowQuery left = query.getLeft();
            WorkflowQuery right = query.getRight();

            switch (operator) {
            case WorkflowQuery.AND:
                return Expression.and(buildExpression(query), buildExpression(query));

            case WorkflowQuery.OR:
                return Expression.or(buildExpression(query), buildExpression(query));

            case WorkflowQuery.XOR:
                throw new StoreException("XOR Operator in Queries not supported by " + this.getClass().getName());

            default:
                throw new StoreException("Operator '" + operator + "' is not supported by " + this.getClass().getName());
            }
        }
    }

    /**
     * Removes the escape sequences from this string.
     * @param s
     * @return
     */
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
}
