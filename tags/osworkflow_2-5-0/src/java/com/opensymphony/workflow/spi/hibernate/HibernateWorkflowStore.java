/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.1.1.1 $
 */
public class HibernateWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(HibernateWorkflowStore.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    Session session;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateWorkflowStore(Session session) {
        this.session = session;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap();
        args.put("entryName", "OSWorkflowEntry");
        args.put("entryId", new Long(entryId));
        args.put("session", session);

        return PropertySetManager.getInstance("hibernate", args);
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) {
        HibernateCurrentStep step = new HibernateCurrentStep();
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
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
            } catch (HibernateException e) {
                log.error("An exception occured", e);
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

            //session.save(step);
            return step;
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
        }
    }

    public WorkflowEntry createEntry(String workflowName) {
        HibernateWorkflowEntry entry = new HibernateWorkflowEntry();
        entry.setInitialized(true);
        entry.setWorkflowName(workflowName);

        try {
            session.save(entry);
        } catch (HibernateException e) {
            log.error("An exception occured", e);
        }

        return entry;
    }

    public List findCurrentSteps(long entryId) {
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
        }

        try {
            return session.find("FROM step IN CLASS " + HibernateCurrentStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.association(entry.getClass()));
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return Collections.EMPTY_LIST;
        }
    }

    public WorkflowEntry findEntry(long entryId) {
        try {
            List result = session.find("FROM entry IN CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG);

            return (WorkflowEntry) result.get(0);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
        }
    }

    public List findHistorySteps(long entryId) {
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry in CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(entryId), Hibernate.LONG).get(0);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
        }

        try {
            return session.find("FROM step IN CLASS " + HibernateHistoryStep.class.getName() + " WHERE step.entry = ?", entry, Hibernate.association(entry.getClass()));
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return Collections.EMPTY_LIST;
        }
    }

    public void init(Map props) {
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) {
        HibernateCurrentStep currentStep = (HibernateCurrentStep) step;

        currentStep.setActionId(actionId);
        currentStep.setFinishDate(finishDate);
        currentStep.setStatus(status);
        currentStep.setCaller(caller);

        try {
            session.save(currentStep);

            return currentStep;
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return null;
        }
    }

    public void moveToHistory(Step step) {
        HibernateWorkflowEntry entry;

        try {
            entry = (HibernateWorkflowEntry) session.find("FROM entry IN CLASS " + HibernateWorkflowEntry.class.getName() + " WHERE entry.id = ?", new Long(step.getEntryId()), Hibernate.LONG).get(0);
        } catch (HibernateException e) {
            log.error("An exception occured", e);

            return;
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

            //session.delete(step);
            //session.save(hStep, new Long(hStep.getId()));
        } catch (HibernateException e) {
            log.error("An exception occured", e);
        }
    }

    public List query(WorkflowQuery query) {
        return null;
    }
}
