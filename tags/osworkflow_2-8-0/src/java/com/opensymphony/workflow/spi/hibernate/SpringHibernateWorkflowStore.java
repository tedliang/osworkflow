/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;

import com.opensymphony.workflow.QueryNotSupportedException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.FieldExpression;
import com.opensymphony.workflow.query.NestedExpression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.workflow.util.PropertySetDelegate;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author        Quake Wang
 * @since        2004-5-2
 *
 **/
public class SpringHibernateWorkflowStore extends HibernateDaoSupport implements WorkflowStore {
    //~ Instance fields ////////////////////////////////////////////////////////

    private PropertySetDelegate propertySetDelegate;
    private String cacheRegion = null;
    private boolean cacheable = false;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public void setEntryState(long entryId, int state) throws StoreException {
        HibernateWorkflowEntry entry = loadEntry(entryId);
        entry.setState(state);
        getHibernateTemplate().update(entry);
    }

    public PropertySet getPropertySet(long entryId) throws StoreException {
        if (propertySetDelegate != null) {
            return propertySetDelegate.getPropertySet(entryId);
        }

        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(getSessionFactory());

        args.put("configurationProvider", configurationProvider);

        return PropertySetManager.getInstance("hibernate", args);
    }

    public void setPropertySetDelegate(PropertySetDelegate propertySetDelegate) {
        this.propertySetDelegate = propertySetDelegate;
    }

    public PropertySetDelegate getPropertySetDelegate() {
        return propertySetDelegate;
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        HibernateCurrentStep step = new HibernateCurrentStep();
        HibernateWorkflowEntry entry = loadEntry(entryId);

        step.setEntry(entry);
        step.setStepId(stepId);
        step.setOwner(owner);
        step.setStartDate(startDate);
        step.setDueDate(dueDate);
        step.setStatus(status);

        final List stepIdList = new ArrayList(previousIds.length);

        for (int i = 0; i < previousIds.length; i++) {
            long previousId = previousIds[i];
            stepIdList.add(new Long(previousId));
        }

        if (!stepIdList.isEmpty()) {
            step.setPreviousSteps((List) getHibernateTemplate().execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        return session.createQuery("FROM " + HibernateCurrentStep.class.getName() + " step WHERE step.id IN (:stepIds)").setParameterList("stepIds", stepIdList).setCacheable(isCacheable()).setCacheRegion(getCacheRegion()).list();
                    }
                }));
        } else {
            step.setPreviousSteps(Collections.EMPTY_LIST);
        }

        getHibernateTemplate().save(step);

        return step;
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        HibernateWorkflowEntry entry = new HibernateWorkflowEntry();
        entry.setState(WorkflowEntry.CREATED);
        entry.setWorkflowName(workflowName);
        getHibernateTemplate().save(entry);

        return entry;
    }

    public List findCurrentSteps(final long entryId) throws StoreException {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    return session.createQuery("FROM " + HibernateCurrentStep.class.getName() + " step WHERE step.entry.id = :entryId").setLong("entryId", entryId).setCacheable(isCacheable()).setCacheRegion(getCacheRegion()).list();
                }
            });
    }

    public WorkflowEntry findEntry(long entryId) throws StoreException {
        return loadEntry(entryId);
    }

    public List findHistorySteps(final long entryId) throws StoreException {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    return session.createQuery("FROM " + HibernateHistoryStep.class.getName() + " step WHERE step.entry.id = :entryId").setLong("entryId", entryId).setCacheable(isCacheable()).setCacheRegion(getCacheRegion()).list();
                }
            });
    }

    public void init(Map props) throws StoreException {
        // TODO Auto-generated method stub       
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        HibernateCurrentStep currentStep = (HibernateCurrentStep) step;

        currentStep.setActionId(actionId);
        currentStep.setFinishDate(finishDate);
        currentStep.setStatus(status);
        currentStep.setCaller(caller);
        getHibernateTemplate().update(currentStep);

        return currentStep;
    }

    public void moveToHistory(Step step) throws StoreException {
        HibernateHistoryStep hStep = new HibernateHistoryStep((HibernateStep) step);
        getHibernateTemplate().delete(step);
        getHibernateTemplate().save(hStep);
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

        Criteria criteria = getSession().createCriteria(entityClass);
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

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.spi.WorkflowStore#query(com.opensymphony.workflow.query.WorkflowExpressionQuery)
     */
    public List query(WorkflowExpressionQuery query) throws StoreException {
        com.opensymphony.workflow.query.Expression expression = query.getExpression();

        Criterion expr;

        Class entityClass = getQueryClass(expression, null);

        if (expression.isNested()) {
            expr = buildNested((NestedExpression) expression);
        } else {
            expr = queryComparison((FieldExpression) expression);
        }

        Criteria criteria = getSession().createCriteria(entityClass);
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

    protected String getCacheRegion() {
        return cacheRegion;
    }

    protected boolean isCacheable() {
        return cacheable;
    }

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
                throw new QueryNotSupportedException("XOR Operator in Queries not supported by " + this.getClass().getName());

            default:
                throw new QueryNotSupportedException("Operator '" + operator + "' is not supported by " + this.getClass().getName());
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

    private HibernateWorkflowEntry loadEntry(long entryId) {
        return (HibernateWorkflowEntry) getHibernateTemplate().load(HibernateWorkflowEntry.class, new Long(entryId));
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
