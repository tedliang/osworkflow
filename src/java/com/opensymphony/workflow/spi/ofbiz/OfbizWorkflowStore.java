/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ofbiz;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.workflow.QueryNotSupportedException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.UtilMisc;

import java.sql.Timestamp;

import java.util.*;


/**
 * OpenForBusiness Entity Engine implemenation.
 * <p>
 *
 * Has one <b>optional</b> property that can be provided:
 * <ul>
 *  <li>delegator - the delegator name, defaults to "default"</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class OfbizWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(OfbizWorkflowStore.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private GenericDelegator gd;
    private String delegatorName;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntryState(long entryId, int state) throws StoreException {
        try {
            GenericValue gv = gd.findByPrimaryKey("OSWorkflowEntry", UtilMisc.toMap("id", new Long(entryId)));
            gv.set("state", new Integer(state));
            gd.store(gv);
        } catch (GenericEntityException e) {
            throw new StoreException("Could not update workflow instance #" + entryId + " to status " + state, e);
        }
    }

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap(2);
        args.put("entityId", new Long(entryId));
        args.put("entityName", "WorkflowEntry");

        return PropertySetManager.getInstance("ofbiz", args);
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws StoreException {
        try {
            Long id = gd.getNextSeqId("OSCurrentStep");
            HashMap valueMap = new HashMap();
            valueMap.put("id", id);
            valueMap.put("entryId", new Long(entryId));
            valueMap.put("actionId", new Integer(0));
            valueMap.put("stepId", new Integer(stepId));
            valueMap.put("owner", owner);
            valueMap.put("startDate", new Timestamp(startDate.getTime()));

            Timestamp realDueDate = null;

            if (dueDate != null) {
                realDueDate = new Timestamp(dueDate.getTime());
            }

            valueMap.put("dueDate", realDueDate);
            valueMap.put("finishDate", null);
            valueMap.put("status", status);

            GenericValue gv = gd.create("OSCurrentStep", valueMap);
            ArrayList storeList = new ArrayList();
            storeList.add(gv);

            if (previousIds != null) {
                if (!((previousIds.length == 1) && (previousIds[0] == 0))) {
                    for (int i = 0; i < previousIds.length; i++) {
                        long previousId = previousIds[i];
                        GenericValue prevGv = gd.create("OSCurrentStepPrev", UtilMisc.toMap("id", id, "previousId", new Long(previousId)));
                        storeList.add(prevGv);
                    }
                }
            }

            gd.storeAll(storeList);

            return new SimpleStep(id.longValue(), entryId, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);
        } catch (GenericEntityException e) {
            throw new StoreException("Could not create new current step for #" + entryId, e);
        }
    }

    public WorkflowEntry createEntry(String workflowName) throws StoreException {
        try {
            Long id = gd.getNextSeqId("OSWorkflowEntry");
            GenericValue gv = gd.create("OSWorkflowEntry", UtilMisc.toMap("id", id, "name", workflowName, "state", new Integer(WorkflowEntry.CREATED)));
            gd.storeAll(UtilMisc.toList(gv));

            return new SimpleWorkflowEntry(id.longValue(), workflowName, WorkflowEntry.CREATED);
        } catch (GenericEntityException e) {
            throw new StoreException("Could not create workflow instance", e);
        }
    }

    public List findCurrentSteps(long entryId) throws StoreException {
        try {
            Collection c = gd.findByAnd("OSCurrentStep", UtilMisc.toMap("entryId", new Long(entryId)));
            ArrayList list = new ArrayList();

            for (Iterator iterator = c.iterator(); iterator.hasNext();) {
                GenericValue gv = (GenericValue) iterator.next();
                long id = gv.getLong("id").longValue();
                int stepId = gv.getInteger("stepId").intValue();
                int actionId = gv.getInteger("actionId").intValue();
                String owner = gv.getString("owner");
                Timestamp startDate = gv.getTimestamp("startDate");
                Timestamp dueDate = gv.getTimestamp("dueDate");
                Timestamp finishDate = gv.getTimestamp("finishDate");
                String status = gv.getString("status");
                String caller = gv.getString("caller");

                Collection prevGvs = gd.findByAnd("OSCurrentStepPrev", UtilMisc.toMap("id", new Long(id)));
                long[] prevIds = new long[prevGvs.size()];
                int i = 0;

                for (Iterator iterator2 = prevGvs.iterator();
                        iterator2.hasNext();) {
                    GenericValue prevGv = (GenericValue) iterator2.next();
                    prevIds[i] = prevGv.getLong("previousId").longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, caller);
                list.add(step);
            }

            return list;
        } catch (GenericEntityException e) {
            throw new StoreException("Could not find current steps for #" + entryId, e);
        }
    }

    public WorkflowEntry findEntry(long entryId) throws StoreException {
        try {
            GenericValue gv = gd.findByPrimaryKey("OSWorkflowEntry", UtilMisc.toMap("id", new Long(entryId)));
            String workflowName = gv.getString("name");

            return new SimpleWorkflowEntry(entryId, workflowName, gv.getInteger("state").intValue());
        } catch (GenericEntityException e) {
            throw new StoreException("Could not find workflow instance #" + entryId, e);
        }
    }

    public List findHistorySteps(long entryId) throws StoreException {
        try {
            Collection c = gd.findByAnd("OSHistoryStep", UtilMisc.toMap("entryId", new Long(entryId)), UtilMisc.toList("id DESC"));
            ArrayList list = new ArrayList();

            for (Iterator iterator = c.iterator(); iterator.hasNext();) {
                GenericValue gv = (GenericValue) iterator.next();
                long id = gv.getLong("id").longValue();
                int stepId = gv.getInteger("stepId").intValue();
                int actionId = gv.getInteger("actionId").intValue();
                String owner = gv.getString("owner");
                Timestamp startDate = gv.getTimestamp("startDate");
                Timestamp dueDate = gv.getTimestamp("dueDate");
                Timestamp finishDate = gv.getTimestamp("finishDate");
                String status = gv.getString("status");
                String caller = gv.getString("caller");

                Collection prevGvs = gd.findByAnd("OSHistoryStepPrev", UtilMisc.toMap("id", new Long(id)));
                long[] prevIds = new long[prevGvs.size()];
                int i = 0;

                for (Iterator iterator2 = prevGvs.iterator();
                        iterator2.hasNext();) {
                    GenericValue prevGv = (GenericValue) iterator2.next();
                    prevIds[i] = prevGv.getLong("previousId").longValue();
                    i++;
                }

                SimpleStep step = new SimpleStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, caller);
                list.add(step);
            }

            return list;
        } catch (GenericEntityException e) {
            throw new StoreException("Could not find history steps for #" + entryId, e);
        }
    }

    public void init(Map props) throws StoreException {
        delegatorName = (String) props.get("delegator");

        if (delegatorName == null) {
            delegatorName = "default";
        }

        try {
            gd = GenericDelegator.getGenericDelegator(delegatorName);
        } catch (Exception t) {
            throw new StoreException("Error getting GenericDelegator", t);
        }
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) throws StoreException {
        try {
            GenericValue gv = gd.findByPrimaryKey("OSCurrentStep", UtilMisc.toMap("id", new Long(step.getId())));
            gv.set("actionId", new Integer(actionId));
            gv.set("finishDate", new Timestamp(finishDate.getTime()));
            gv.set("status", status);
            gv.set("caller", caller);
            gd.store(gv);

            SimpleStep theStep = (SimpleStep) step;
            theStep.setStatus(status);
            theStep.setFinishDate(finishDate);
            theStep.setActionId(actionId);
            theStep.setCaller(caller);

            return theStep;
        } catch (GenericEntityException e) {
            throw new StoreException("Error marking step #" + step.getId() + " finished", e);
        }
    }

    public void moveToHistory(Step step) throws StoreException {
        try {
            Long id = new Long(step.getId());
            gd.removeByAnd("OSCurrentStep", UtilMisc.toMap("id", id));

            HashMap valueMap = new HashMap();
            valueMap.put("id", id);
            valueMap.put("entryId", new Long(step.getEntryId()));
            valueMap.put("actionId", new Integer(step.getActionId()));
            valueMap.put("stepId", new Integer(step.getStepId()));
            valueMap.put("owner", step.getOwner());
            valueMap.put("startDate", new Timestamp(step.getStartDate().getTime()));

            Timestamp realDueDate = null;

            if (step.getDueDate() != null) {
                realDueDate = new Timestamp(step.getDueDate().getTime());
            }

            valueMap.put("dueDate", realDueDate);

            if (step.getFinishDate() != null) {
                valueMap.put("finishDate", new Timestamp(step.getFinishDate().getTime()));
            }

            valueMap.put("status", step.getStatus());
            valueMap.put("caller", step.getCaller());

            GenericValue gv = gd.create("OSHistoryStep", valueMap);
            ArrayList storeList = new ArrayList();
            storeList.add(gv);

            long[] previousIds = step.getPreviousStepIds();

            if (previousIds != null) {
                for (int i = 0; i < previousIds.length; i++) {
                    long previousId = previousIds[i];
                    GenericValue prevGv = gd.create("OSHistoryStepPrev", UtilMisc.toMap("id", id, "previousId", new Long(previousId)));
                    storeList.add(prevGv);
                }
            }

            gd.storeAll(storeList);
        } catch (GenericEntityException e) {
            throw new StoreException("Could not move to history step for #" + step.getEntryId(), e);
        }
    }

    public List query(WorkflowExpressionQuery query) throws StoreException {
        throw new QueryNotSupportedException("Ofbiz Store does not support queries");
    }

    public List query(WorkflowQuery query) throws StoreException {
        throw new QueryNotSupportedException("Ofbiz Store does not support queries");
    }
}
