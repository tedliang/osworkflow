/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.SimpleStep;
import com.opensymphony.workflow.spi.SimpleWorkflowEntry;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;

import java.sql.Timestamp;

import java.util.*;

import javax.ejb.SessionBean;


/**
 * @ejb.bean
 *  type="Stateless"
 *  name="WorkflowStore"
 *  view-type="remote"
 *  transaction-type="Container"
 *
 * @ejb.ejb-ref
 *  ejb-name="HistoryStepPrev"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="CurrentStepPrev"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="CurrentStep"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="HistoryStep"
 *  view-type="local"
 *
 * @ejb.ejb-ref
 *  ejb-name="WorkflowEntry"
 *  view-type="local"
 *
 * @ejb.permission unchecked="true"
 * @ejb.transaction type="Supports"
 *
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @version $Revision: 1.3 $
 *
 * Date: Apr 7, 2003
 * Time: 10:57:28 PM
 */
public abstract class WorkflowStoreSessionEJB implements SessionBean {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @ejb.interface-method
     */
    public void setEntryState(long entryId, int state) throws StoreException {
        try {
            WorkflowEntryLocal entry = WorkflowEntryHomeFactory.getLocalHome().findByPrimaryKey(new Long(entryId));
            entry.setState(state);
        } catch (Exception e) {
            throw new StoreException("Could not find workflow instance #" + entryId, e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        try {
            WorkflowEntryLocal entry = WorkflowEntryHomeFactory.getLocalHome().findByPrimaryKey(new Long(entryId));
            entry.setState(WorkflowEntry.ACTIVATED);

            Timestamp realDueDate = null;

            if (dueDate != null) {
                realDueDate = new Timestamp(dueDate.getTime());
            }

            CurrentStepLocal step = CurrentStepHomeFactory.getLocalHome().create(entryId, stepId, owner, new Timestamp(startDate.getTime()), realDueDate, status);

            long id = step.getId().longValue();

            for (int i = 0; i < previousIds.length; i++) {
                long previousId = previousIds[i];
                CurrentStepPrevHomeFactory.getLocalHome().create(id, previousId);
            }

            return new SimpleStep(id, entryId, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);
        } catch (Exception e) {
            throw new StoreException("Could not create new current step for workflow instance #" + entryId + " step #" + stepId + ":" + e, e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        try {
            WorkflowEntryLocal entry = WorkflowEntryHomeFactory.getLocalHome().create(workflowName);

            return new SimpleWorkflowEntry(entry.getId().longValue(), entry.getWorkflowName(), WorkflowEntry.CREATED);
        } catch (Exception e) {
            throw new StoreException("Could not create new workflow instance", e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public List findCurrentSteps(long entryId) throws StoreException {
        try {
            Collection results = CurrentStepHomeFactory.getLocalHome().findByEntryId(entryId);
            TreeSet set = new TreeSet(new StepComparator());

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                CurrentStepLocal stepLocal = (CurrentStepLocal) iterator.next();

                long id = stepLocal.getId().longValue();
                Collection prevSteps = CurrentStepPrevHomeFactory.getLocalHome().findByStepId(id);
                long[] prevIds = new long[prevSteps.size()];
                int i = 0;

                for (Iterator iterator2 = prevSteps.iterator();
                        iterator2.hasNext();) {
                    CurrentStepPrevLocal stepPrev = (CurrentStepPrevLocal) iterator2.next();
                    prevIds[i] = stepPrev.getPreviousId().longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(id, stepLocal.getEntryId(), stepLocal.getStepId(), stepLocal.getActionId(), stepLocal.getOwner(), stepLocal.getStartDate(), stepLocal.getDueDate(), stepLocal.getFinishDate(), stepLocal.getStatus(), prevIds, stepLocal.getCaller());
                set.add(step);
            }

            return new ArrayList(set);
        } catch (Exception e) {
            throw new StoreException("Error in findCurrentSteps", e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public WorkflowEntry findEntry(long entryId) throws StoreException {
        try {
            WorkflowEntryLocal entry = WorkflowEntryHomeFactory.getLocalHome().findByPrimaryKey(new Long(entryId));

            return new SimpleWorkflowEntry(entry.getId().longValue(), entry.getWorkflowName(), entry.getState());
        } catch (Exception e) {
            throw new StoreException("Could not find workflow instance #" + entryId, e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public List findHistorySteps(long entryId) throws StoreException {
        try {
            Collection results = HistoryStepHomeFactory.getLocalHome().findByEntryId(entryId);
            TreeSet set = new TreeSet(new StepComparator());

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                HistoryStepLocal stepRemote = (HistoryStepLocal) iterator.next();

                long id = stepRemote.getId().longValue();
                Collection prevSteps = HistoryStepPrevHomeFactory.getLocalHome().findByStepId(id);
                long[] prevIds = new long[prevSteps.size()];
                int i = 0;

                for (Iterator iterator2 = prevSteps.iterator();
                        iterator2.hasNext();) {
                    HistoryStepPrevLocal stepPrev = (HistoryStepPrevLocal) iterator2.next();
                    prevIds[i] = stepPrev.getPreviousId().longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(stepRemote.getId().longValue(), stepRemote.getEntryId(), stepRemote.getStepId(), stepRemote.getActionId(), stepRemote.getOwner(), stepRemote.getStartDate(), stepRemote.getDueDate(), stepRemote.getFinishDate(), stepRemote.getStatus(), prevIds, stepRemote.getCaller());
                set.add(step);
            }

            return new ArrayList(set);
        } catch (Exception e) {
            throw new StoreException("Could not find history steps for workflow instance #" + entryId, e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        try {
            CurrentStepLocal currentStep = CurrentStepHomeFactory.getLocalHome().findByPrimaryKey(new Long(step.getId()));
            currentStep.setActionId(actionId);
            currentStep.setFinishDate(new Timestamp(finishDate.getTime()));
            currentStep.setStatus(status);
            currentStep.setCaller(caller);

            SimpleStep theStep = (SimpleStep) step;
            theStep.setActionId(actionId);
            theStep.setFinishDate(finishDate);
            theStep.setStatus(status);
            theStep.setCaller(caller);

            return theStep;
        } catch (Exception e) {
            throw new StoreException("Could not mark step finished for #" + step.getEntryId(), e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public void moveToHistory(Step step) throws StoreException {
        Long id = new Long(step.getId());

        try {
            CurrentStepLocal currentStep = CurrentStepHomeFactory.getLocalHome().findByPrimaryKey(id);
            long[] previousIds = step.getPreviousStepIds();
            Timestamp realDueDate = null;

            if (step.getDueDate() != null) {
                realDueDate = new Timestamp(step.getDueDate().getTime());
            }

            Timestamp finishedDate = null;

            if (step.getFinishDate() != null) {
                finishedDate = new Timestamp(step.getFinishDate().getTime());
            }

            HistoryStepLocalHome historyHome = HistoryStepHomeFactory.getLocalHome();
            historyHome.create(id.longValue(), step.getEntryId(), step.getStepId(), step.getActionId(), step.getOwner(), new Timestamp(step.getStartDate().getTime()), realDueDate, finishedDate, step.getStatus(), step.getCaller());

            for (int i = 0; i < previousIds.length; i++) {
                long previousId = previousIds[i];
                HistoryStepPrevHomeFactory.getLocalHome().create(id.longValue(), previousId);
            }

            Collection oldPrevSteps = CurrentStepPrevHomeFactory.getLocalHome().findByStepId(id.longValue());

            for (Iterator iterator = oldPrevSteps.iterator();
                    iterator.hasNext();) {
                CurrentStepPrevLocal oldPrevStep = (CurrentStepPrevLocal) iterator.next();
                oldPrevStep.remove();
            }

            currentStep.remove();
        } catch (Exception e) {
            throw new StoreException("Could not move step to history for workflow instance #" + id, e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public List query(WorkflowQuery query) throws StoreException {
        // not implemented
        throw new StoreException("EJB store does not support queries");
    }

    /**
     * @ejb.interface-method
     */
    public List query(WorkflowExpressionQuery query) throws StoreException {
        // not implemented
        throw new StoreException("EJB store does not support queries");
    }
}
