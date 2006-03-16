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
import com.opensymphony.workflow.query.*;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * A workflow store backed by Hibernate for persistence.  To use this with the standard
 * persistence factory, pass to the DefaultConfiguration.persistenceArgs the SessionFactory to
 * use:
 * <code>DefaultConfiguration.persistenceArgs.put("sessionFactory", DatabaseHelper.getSessionFactory());</code>
 * See the HibernateFunctionalWorkflowTestCase for more help.
 *
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
            return session.find("FROM step IN CLASS " + HibernateCurrentStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.entity(entry.getClass()));
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
            return session.find("FROM step IN CLASS " + HibernateHistoryStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.entity(entry.getClass()));
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
            session.save(hStep);
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
        com.opensymphony.workflow.query.Expression expression = query.getExpression();

        Criterion expr;

        Class entityClass = getQueryClass(expression, null);

        if (expression.isNested()) {
            expr = buildNested((NestedExpression) expression);
        } else {
            expr = queryComparison((FieldExpression) expression);
        }

        //get results and send them back
        Criteria criteria = session.createCriteria(entityClass);
        criteria.add(expr);

        try {
            Set results = new HashSet();

            Iterator iter = criteria.list().iterator();

            while (iter.hasNext()) {
                Object next = iter.next();
                Object item;

                if (next instanceof HibernateStep) {
                    HibernateStep step = (HibernateStep) next;
                    item = new Long(step.getEntryId());
                } else {
                    WorkflowEntry entry = (WorkflowEntry) next;
                    item = new Long(entry.getId());
                }

                results.add(item);
            }

            return new ArrayList(results);
        } catch (HibernateException e) {
            throw new StoreException("Error executing query " + expression, e);
        }
    }

    public List query(WorkflowQuery query) throws StoreException {
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
        Criterion expression = buildExpression(query);
        criteria.add(expression);

        //get results and send them back
        try {
            Set results = new HashSet();
            Iterator iter = criteria.list().iterator();

            while (iter.hasNext()) {
                HibernateStep step = (HibernateStep) iter.next();
                results.add(new Long(step.getEntryId()));
            }

            return new ArrayList(results);
        } catch (HibernateException e) {
            throw new StoreException("Error executing query " + expression, e);
        }
    }

    /**
     * Returns an expression generated from this query
     */
    private Criterion getExpression(WorkflowQuery query) {
        int operator = query.getOperator();

        switch (operator) {
        case WorkflowQuery.EQUALS:
            return Expression.eq(getFieldName(query.getField()), query.getValue());

        case WorkflowQuery.NOT_EQUALS:
            return Expression.not(Expression.like(getFieldName(query.getField()), query.getValue()));

        case WorkflowQuery.GT:
            return Expression.gt(getFieldName(query.getField()), query.getValue());

        case WorkflowQuery.LT:
            return Expression.lt(getFieldName(query.getField()), query.getValue());

        default:
            return Expression.eq(getFieldName(query.getField()), query.getValue());
        }
    }

    /**
     * returns the correct name of the field given or "1" if none is found
     * which matches the input.
     */
    private String getFieldName(int field) {
        switch (field) {
        case FieldExpression.ACTION: // actionId
            return "actionId";

        case FieldExpression.CALLER:
            return "caller";

        case FieldExpression.FINISH_DATE:
            return "finishDate";

        case FieldExpression.OWNER:
            return "owner";

        case FieldExpression.START_DATE:
            return "startDate";

        case FieldExpression.STEP: // stepId
            return "stepId";

        case FieldExpression.STATUS:
            return "status";

        case FieldExpression.STATE:
            return "state";

        case FieldExpression.NAME:
            return "workflowName";

        case FieldExpression.DUE_DATE:
            return "dueDate";

        default:
            return "1";
        }
    }

    private Class getQueryClass(com.opensymphony.workflow.query.Expression expr, Collection classesCache) throws StoreException {
        if (classesCache == null) {
            classesCache = new HashSet();
        }

        if (expr instanceof FieldExpression) {
            FieldExpression fieldExpression = (FieldExpression) expr;

            switch (fieldExpression.getContext()) {
            case FieldExpression.CURRENT_STEPS:
                classesCache.add(HibernateCurrentStep.class);

                break;

            case FieldExpression.HISTORY_STEPS:
                classesCache.add(HibernateHistoryStep.class);

                break;

            case FieldExpression.ENTRY:
                classesCache.add(HibernateWorkflowEntry.class);

                break;

            default:
                throw new QueryNotSupportedException("Query for unsupported context " + fieldExpression.getContext());
            }
        } else {
            NestedExpression nestedExpression = (NestedExpression) expr;

            for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
                com.opensymphony.workflow.query.Expression expression = nestedExpression.getExpression(i);

                if (expression.isNested()) {
                    classesCache.add(getQueryClass(nestedExpression.getExpression(i), classesCache));
                } else {
                    classesCache.add(getQueryClass(expression, classesCache));
                }
            }
        }

        if (classesCache.size() > 1) {
            throw new QueryNotSupportedException("Store does not support nested queries of different types (types found:" + classesCache + ")");
        }

        return (Class) classesCache.iterator().next();
    }

    /**
     *  Recursive method for building Expressions using Query objects.
     */
    private Criterion buildExpression(WorkflowQuery query) throws StoreException {
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
                return Expression.and(buildExpression(left), buildExpression(right));

            case WorkflowQuery.OR:
                return Expression.or(buildExpression(left), buildExpression(right));

            case WorkflowQuery.XOR:
                throw new StoreException("XOR Operator in Queries not supported by " + this.getClass().getName());

            default:
                throw new StoreException("Operator '" + operator + "' is not supported by " + this.getClass().getName());
            }
        }
    }

    private Criterion buildNested(NestedExpression nestedExpression) throws StoreException {
        Criterion full = null;

        for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
            Criterion expr;
            com.opensymphony.workflow.query.Expression expression = nestedExpression.getExpression(i);

            if (expression.isNested()) {
                expr = buildNested((NestedExpression) nestedExpression.getExpression(i));
            } else {
                FieldExpression sub = (FieldExpression) nestedExpression.getExpression(i);
                expr = queryComparison(sub);

                if (sub.isNegate()) {
                    expr = Expression.not(expr);
                }
            }

            if (full == null) {
                full = expr;
            } else {
                switch (nestedExpression.getExpressionOperator()) {
                case NestedExpression.AND:
                    full = Expression.and(full, expr);

                    break;

                case NestedExpression.OR:
                    full = Expression.or(full, expr);
                }
            }
        }

        return full;
    }

    private Criterion queryComparison(FieldExpression expression) {
        int operator = expression.getOperator();

        switch (operator) {
        case FieldExpression.EQUALS:
            return Expression.eq(getFieldName(expression.getField()), expression.getValue());

        case FieldExpression.NOT_EQUALS:
            return Expression.not(Expression.like(getFieldName(expression.getField()), expression.getValue()));

        case FieldExpression.GT:
            return Expression.gt(getFieldName(expression.getField()), expression.getValue());

        case FieldExpression.LT:
            return Expression.lt(getFieldName(expression.getField()), expression.getValue());

        default:
            return Expression.eq(getFieldName(expression.getField()), expression.getValue());
        }
    }
}
