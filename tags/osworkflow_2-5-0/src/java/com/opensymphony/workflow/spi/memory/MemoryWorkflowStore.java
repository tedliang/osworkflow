/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.memory;

import com.opensymphony.module.propertyset.*;

import com.opensymphony.util.DataUtil;
import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.*;

import java.util.*;


/**
 * Simple memory implementation.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class MemoryWorkflowStore implements WorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static HashMap entryCache = new HashMap();
    private static HashMap currentStepsCache = new HashMap();
    private static HashMap historyStepsCache = new HashMap();
    private static HashMap propertySetCache = new HashMap();
    private static long globalEntryId = 1;
    private static long globalStepId = 1;

    //~ Methods ////////////////////////////////////////////////////////////////

    public PropertySet getPropertySet(long entryId) {
        PropertySet ps = (PropertySet) propertySetCache.get(new Long(entryId));

        if (ps == null) {
            ps = PropertySetManager.getInstance("memory", null);
            propertySetCache.put(new Long(entryId), ps);
        }

        return ps;
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) {
        long id = globalStepId++;
        SimpleStep step = new SimpleStep(id, entryId, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);

        List currentSteps = (List) currentStepsCache.get(new Long(entryId));

        if (currentSteps == null) {
            currentSteps = new ArrayList();
            currentStepsCache.put(new Long(entryId), currentSteps);
        }

        currentSteps.add(step);

        return step;
    }

    public WorkflowEntry createEntry(String workflowName) {
        long id = globalEntryId++;
        SimpleWorkflowEntry entry = new SimpleWorkflowEntry(id, workflowName, false);
        entryCache.put(new Long(id), entry);

        return entry;
    }

    public List findCurrentSteps(long entryId) {
        List currentSteps = (List) currentStepsCache.get(new Long(entryId));

        if (currentSteps == null) {
            currentSteps = new ArrayList();
            currentStepsCache.put(new Long(entryId), currentSteps);
        }

        return currentSteps;
    }

    public WorkflowEntry findEntry(long entryId) {
        return (WorkflowEntry) entryCache.get(new Long(entryId));
    }

    public List findHistorySteps(long entryId) {
        List historySteps = (List) historyStepsCache.get(new Long(entryId));

        if (historySteps == null) {
            historySteps = new ArrayList();
            historyStepsCache.put(new Long(entryId), historySteps);
        }

        return historySteps;
    }

    public void init(Map props) {
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) {
        List currentSteps = (List) currentStepsCache.get(new Long(step.getEntryId()));

        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            SimpleStep theStep = (SimpleStep) iterator.next();

            if (theStep.getId() == step.getId()) {
                theStep.setStatus(status);
                theStep.setActionId(actionId);
                theStep.setFinishDate(finishDate);
                theStep.setCaller(caller);

                return theStep;
            }
        }

        return null;
    }

    public void moveToHistory(Step step) {
        List currentSteps = (List) currentStepsCache.get(new Long(step.getEntryId()));

        List historySteps = (List) historyStepsCache.get(new Long(step.getEntryId()));

        if (historySteps == null) {
            historySteps = new ArrayList();
            historyStepsCache.put(new Long(step.getEntryId()), historySteps);
        }

        SimpleStep simpleStep = (SimpleStep) step;

        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step currentStep = (Step) iterator.next();

            if (simpleStep.getId() == currentStep.getId()) {
                iterator.remove();
                historySteps.add(simpleStep);

                break;
            }
        }
    }

    public List query(WorkflowQuery query) {
        ArrayList results = new ArrayList();

        for (Iterator iterator = entryCache.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            Long entryId = (Long) mapEntry.getKey();

            if (query(entryId, query)) {
                results.add(entryId);
            }
        }

        return results;
    }

    private boolean query(Long entryId, WorkflowQuery query) {
        if (query.getLeft() == null) {
            return queryBasic(entryId, query);
        } else {
            int operator = query.getOperator();
            WorkflowQuery left = query.getLeft();
            WorkflowQuery right = query.getRight();

            switch (operator) {
            case WorkflowQuery.AND:
                return query(entryId, left) && query(entryId, right);

            case WorkflowQuery.OR:
                return query(entryId, left) || query(entryId, right);

            case WorkflowQuery.XOR:
                return query(entryId, left) ^ query(entryId, right);
            }
        }

        return false;
    }

    private boolean queryBasic(Long entryId, WorkflowQuery query) {
        // the query object is a comparison
        Object value = query.getValue();
        int operator = query.getOperator();
        int field = query.getField();
        int type = query.getType();

        switch (operator) {
        case WorkflowQuery.EQUALS:
            return queryEquals(entryId, field, type, value);

        case WorkflowQuery.NOT_EQUALS:
            return queryNotEquals(entryId, field, type, value);

        case WorkflowQuery.GT:
            return queryGreaterThan(entryId, field, type, value);

        case WorkflowQuery.LT:
            return queryLessThan(entryId, field, type, value);
        }

        return false;
    }

    private boolean queryEquals(Long entryId, int field, int type, Object value) {
        List steps = null;

        if (type == WorkflowQuery.CURRENT) {
            steps = (List) currentStepsCache.get(entryId);
        } else {
            steps = (List) historyStepsCache.get(entryId);
        }

        switch (field) {
        case WorkflowQuery.ACTION:

            long actionId = DataUtil.getLong((Long) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getActionId() == actionId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.CALLER:

            String caller = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getCaller()).equals(caller)) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.FINISH_DATE:

            Date finishDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (finishDate.equals(step.getFinishDate())) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.OWNER:

            String owner = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getOwner()).equals(owner)) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.START_DATE:

            Date startDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (startDate.equals(step.getStartDate())) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STEP:

            int stepId = DataUtil.getInt((Integer) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (stepId == step.getStepId()) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STATUS:

            String status = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getStatus()).equals(status)) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    private boolean queryGreaterThan(Long entryId, int field, int type, Object value) {
        List steps = null;

        if (type == WorkflowQuery.CURRENT) {
            steps = (List) currentStepsCache.get(entryId);
        } else {
            steps = (List) historyStepsCache.get(entryId);
        }

        switch (field) {
        case WorkflowQuery.ACTION:

            long actionId = DataUtil.getLong((Long) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getActionId() > actionId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.CALLER:

            String caller = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getCaller()).compareTo(caller) > 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.FINISH_DATE:

            Date finishDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getFinishDate().compareTo(finishDate) > 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.OWNER:

            String owner = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getOwner()).compareTo(owner) > 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.START_DATE:

            Date startDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getStartDate().compareTo(startDate) > 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STEP:

            int stepId = DataUtil.getInt((Integer) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getStepId() > stepId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STATUS:

            String status = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getStatus()).compareTo(status) > 0) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    private boolean queryLessThan(Long entryId, int field, int type, Object value) {
        List steps = null;

        if (type == WorkflowQuery.CURRENT) {
            steps = (List) currentStepsCache.get(entryId);
        } else {
            steps = (List) historyStepsCache.get(entryId);
        }

        switch (field) {
        case WorkflowQuery.ACTION:

            long actionId = DataUtil.getLong((Long) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getActionId() < actionId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.CALLER:

            String caller = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getCaller()).compareTo(caller) < 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.FINISH_DATE:

            Date finishDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getFinishDate().compareTo(finishDate) < 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.OWNER:

            String owner = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getOwner()).compareTo(owner) < 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.START_DATE:

            Date startDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getStartDate().compareTo(startDate) < 0) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STEP:

            int stepId = DataUtil.getInt((Integer) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getStepId() < stepId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STATUS:

            String status = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (TextUtils.noNull(step.getStatus()).compareTo(status) < 0) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    private boolean queryNotEquals(Long entryId, int field, int type, Object value) {
        List steps = null;

        if (type == WorkflowQuery.CURRENT) {
            steps = (List) currentStepsCache.get(entryId);
        } else {
            steps = (List) historyStepsCache.get(entryId);
        }

        switch (field) {
        case WorkflowQuery.ACTION:

            long actionId = DataUtil.getLong((Long) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (step.getActionId() != actionId) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.CALLER:

            String caller = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (!TextUtils.noNull(step.getCaller()).equals(caller)) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.FINISH_DATE:

            Date finishDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (!finishDate.equals(step.getFinishDate())) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.OWNER:

            String owner = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (!TextUtils.noNull(step.getOwner()).equals(owner)) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.START_DATE:

            Date startDate = (Date) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (!startDate.equals(step.getStartDate())) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STEP:

            int stepId = DataUtil.getInt((Integer) value);

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (stepId != step.getStepId()) {
                    return true;
                }
            }

            return false;

        case WorkflowQuery.STATUS:

            String status = (String) value;

            for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                SimpleStep step = (SimpleStep) iterator.next();

                if (!TextUtils.noNull(step.getStatus()).equals(status)) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }
}
