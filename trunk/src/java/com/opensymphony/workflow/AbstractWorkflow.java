/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.provider.BeanProvider;
import com.opensymphony.provider.bean.DefaultBeanProvider;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.config.ConfigLoader;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.*;
import com.opensymphony.workflow.util.beanshell.BeanShellCondition;
import com.opensymphony.workflow.util.beanshell.BeanShellFunctionProvider;
import com.opensymphony.workflow.util.beanshell.BeanShellRegister;
import com.opensymphony.workflow.util.beanshell.BeanShellValidator;
import com.opensymphony.workflow.util.bsf.BSFCondition;
import com.opensymphony.workflow.util.bsf.BSFFunctionProvider;
import com.opensymphony.workflow.util.bsf.BSFRegister;
import com.opensymphony.workflow.util.bsf.BSFValidator;
import com.opensymphony.workflow.util.ejb.local.LocalEJBCondition;
import com.opensymphony.workflow.util.ejb.local.LocalEJBFunctionProvider;
import com.opensymphony.workflow.util.ejb.local.LocalEJBRegister;
import com.opensymphony.workflow.util.ejb.local.LocalEJBValidator;
import com.opensymphony.workflow.util.ejb.remote.RemoteEJBCondition;
import com.opensymphony.workflow.util.ejb.remote.RemoteEJBFunctionProvider;
import com.opensymphony.workflow.util.ejb.remote.RemoteEJBRegister;
import com.opensymphony.workflow.util.ejb.remote.RemoteEJBValidator;
import com.opensymphony.workflow.util.jndi.JNDICondition;
import com.opensymphony.workflow.util.jndi.JNDIFunctionProvider;
import com.opensymphony.workflow.util.jndi.JNDIRegister;
import com.opensymphony.workflow.util.jndi.JNDIValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

import java.net.URL;

import java.util.*;


/**
 * Abstract workflow instance that serves as the base for specific implementations, such as EJB or SOAP.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class AbstractWorkflow implements Workflow {
    //~ Static fields/initializers /////////////////////////////////////////////

    // statics
    public static final String CLASS_NAME = "class.name";
    public static final String EJB_LOCATION = "ejb.location";
    public static final String JNDI_LOCATION = "jndi.location";
    public static final String BSF_LANGUAGE = "language";
    public static final String BSF_SOURCE = "source";
    public static final String BSF_ROW = "row";
    public static final String BSF_COL = "col";
    public static final String BSF_SCRIPT = "script";
    public static final String BSH_SCRIPT = "script";
    private static final Log log = LogFactory.getLog(AbstractWorkflow.class);
    protected static boolean configLoaded = false;
    private static BeanProvider beanProvider = new DefaultBeanProvider();

    //~ Instance fields ////////////////////////////////////////////////////////

    protected WorkflowContext context;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AbstractWorkflow() {
        try {
            loadConfig(null);
        } catch (FactoryException e) {
            throw new InternalWorkflowException("Error loading config", (e.getRootCause() != null) ? e.getRootCause() : e);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @ejb.interface-method
     * @deprecated use {@link #getAvailableActions(long, Map)}  with an empty Map instead.
     */
    public int[] getAvailableActions(long id) throws WorkflowException {
        return getAvailableActions(id, new HashMap());
    }

    /**
     * Get the available actions for the specified workflow instance.
     * @ejb.interface-method
     * @param id The workflow instance id.
     * @param inputs The inputs map to pass on to conditions
     * @return An array of action id's that can be performed on the specified entry
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     */
    public int[] getAvailableActions(long id, Map inputs) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        if (entry == null) {
            throw new IllegalArgumentException("No such workflow id " + id);
        }

        if (entry.getState() != WorkflowEntry.ACTIVATED) {
            log.debug("--> state is " + entry.getState());

            return new int[0];
        }

        WorkflowDescriptor wf = getWorkflow(entry.getWorkflowName());

        if (wf == null) {
            throw new IllegalArgumentException("No such workflow " + entry.getWorkflowName());
        }

        List l = new ArrayList();
        PropertySet ps = store.getPropertySet(id);
        Map transientVars = (inputs == null) ? new HashMap() : new HashMap(inputs);
        Collection currentSteps = store.findCurrentSteps(id);

        populateTransientMap(entry, transientVars, wf.getRegisters(), new Integer(0), currentSteps);

        // get global actions
        List globalActions = wf.getGlobalActions();

        for (Iterator iterator = globalActions.iterator(); iterator.hasNext();) {
            ActionDescriptor action = (ActionDescriptor) iterator.next();
            RestrictionDescriptor restriction = action.getRestriction();
            String conditionType = null;
            List conditions = null;

            if (restriction != null) {
                conditionType = restriction.getConditionType();
                conditions = restriction.getConditions();
            }

            if (passesConditions(conditionType, conditions, transientVars, ps)) {
                l.add(new Integer(action.getId()));
            }
        }

        // get normal actions
        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step step = (Step) iterator.next();
            l.addAll(getAvailableActionsForStep(wf, step, transientVars, ps));
        }

        int[] actions = new int[l.size()];

        for (int i = 0; i < actions.length; i++) {
            actions[i] = ((Integer) l.get(i)).intValue();
        }

        return actions;
    }

    /**
     * @ejb.interface-method
     */
    public List getCurrentSteps(long id) throws StoreException {
        WorkflowStore store = getPersistence();

        return store.findCurrentSteps(id);
    }

    /**
     * @ejb.interface-method
     */
    public int getEntryState(long id) throws StoreException {
        WorkflowStore store = getPersistence();

        return store.findEntry(id).getState();
    }

    /**
     * @ejb.interface-method
     */
    public List getHistorySteps(long id) throws StoreException {
        WorkflowStore store = getPersistence();

        return store.findHistorySteps(id);
    }

    /**
     * @ejb.interface-method
     */
    public Properties getPersistenceProperties() {
        Properties p = new Properties();
        Iterator iter = ConfigLoader.persistenceArgs.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            p.setProperty((String) entry.getKey(), (String) entry.getValue());
        }

        return p;
    }

    /**
     * Get the PropertySet for the specified workflow ID
     * @ejb.interface-method
     * @param id The workflow ID
     */
    public PropertySet getPropertySet(long id) throws StoreException {
        PropertySet ps = getPersistence().getPropertySet(id);

        return ps;
    }

    /**
     * @ejb.interface-method
     */
    public List getSecurityPermissions(long id) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);
        WorkflowDescriptor wf = getWorkflow(entry.getWorkflowName());

        PropertySet ps = store.getPropertySet(id);
        Map transientVars = new HashMap();
        Collection currentSteps = store.findCurrentSteps(id);
        populateTransientMap(entry, transientVars, wf.getRegisters(), null, currentSteps);

        List s = new ArrayList();

        for (Iterator interator = currentSteps.iterator(); interator.hasNext();) {
            Step step = (Step) interator.next();

            int stepId = step.getStepId();

            StepDescriptor xmlStep = wf.getStep(stepId);

            List securities = xmlStep.getPermissions();

            for (Iterator iterator2 = securities.iterator();
                    iterator2.hasNext();) {
                PermissionDescriptor security = (PermissionDescriptor) iterator2.next();

                // to have the permission, the condition must be met or not specified
                // securities can't have restrictions based on inputs, so it's null
                if (passesConditions(security.getRestriction().getConditionType(), security.getRestriction().getConditions(), transientVars, ps)) {
                    s.add(security.getName());
                }
            }
        }

        return s;
    }

    /**
     * @ejb.interface-method
     */
    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) throws FactoryException {
        return getWorkflow(workflowName);
    }

    /**
     * @ejb.interface-method
     */
    public String getWorkflowName(long id) throws StoreException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        if (entry != null) {
            return entry.getWorkflowName();
        } else {
            return null;
        }
    }

    /**
     * Get a list of workflow names available
     * @ejb.interface-method
     * @return String[] an array of workflow names.
     * @throws UnsupportedOperationException if the underlying workflow factory cannot obtain a list of workflow names.
     */
    public String[] getWorkflowNames() throws FactoryException {
        return ConfigLoader.getWorkflowNames();
    }

    /**
     * @ejb.interface-method
     */
    public boolean canInitialize(String workflowName, int initialAction) throws WorkflowException {
        return canInitialize(workflowName, initialAction, null);
    }

    /**
     * @ejb.interface-method
     * @param workflowName the name of the workflow to check
     * @param initialAction The initial action to check
     * @param inputs the inputs map
     * @return true if the workflow can be initialized
     * @throws WorkflowException if an unexpected error happens
     */
    public boolean canInitialize(String workflowName, int initialAction, Map inputs) throws WorkflowException {
        final String mockWorkflowName = workflowName;
        WorkflowEntry mockEntry = new WorkflowEntry() {
            public long getId() {
                return 0;
            }

            public String getWorkflowName() {
                return mockWorkflowName;
            }

            public boolean isInitialized() {
                return false;
            }

            public int getState() {
                return WorkflowEntry.CREATED;
            }
        };

        // since no state change happens here, a memory instance is just fine
        PropertySet ps = PropertySetManager.getInstance("memory", null);
        Map transientVars = new HashMap();

        if (inputs != null) {
            transientVars.putAll(inputs);
        }

        populateTransientMap(mockEntry, transientVars, Collections.EMPTY_LIST, new Integer(initialAction), Collections.EMPTY_LIST);

        return canInitialize(workflowName, initialAction, transientVars, ps);
    }

    /**
     * @ejb.interface-method
     */
    public boolean canModifyEntryState(long id, int newState) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);
        int currentState = entry.getState();
        boolean result = false;

        switch (newState) {
        case WorkflowEntry.CREATED:
            result = false;

        case WorkflowEntry.ACTIVATED:

            if ((currentState == WorkflowEntry.CREATED) || (currentState == WorkflowEntry.SUSPENDED)) {
                result = true;
            }

            break;

        case WorkflowEntry.SUSPENDED:

            if (currentState == WorkflowEntry.ACTIVATED) {
                result = true;
            }

            break;

        case WorkflowEntry.KILLED:

            if ((currentState == WorkflowEntry.CREATED) || (currentState == WorkflowEntry.ACTIVATED) || (currentState == WorkflowEntry.SUSPENDED)) {
                result = true;
            }

            break;

        default:
            result = false;

            break;
        }

        return result;
    }

    public void changeEntryState(long id, int newState) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        if (canModifyEntryState(id, newState)) {
            store.setEntryState(id, newState);
        } else {
            throw new InvalidEntryStateException("Can't transition workflow instance #" + id + ". Current state is " + entry.getState() + ", requested state is " + newState);
        }

        if (log.isDebugEnabled()) {
            log.debug(entry.getId() + " : State is now : " + entry.getState());
        }
    }

    public void doAction(long id, int actionId, Map inputs) throws WorkflowException {
        int[] availableActions = getAvailableActions(id, inputs);
        boolean validAction = false;

        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        if (entry.getState() != WorkflowEntry.ACTIVATED) {
            return;
        }

        for (int i = 0; i < availableActions.length; i++) {
            if (availableActions[i] == actionId) {
                validAction = true;

                break;
            }
        }

        if (!validAction) {
            throw new IllegalArgumentException("Action " + actionId + " is invalid");
        }

        WorkflowDescriptor wf = getWorkflow(entry.getWorkflowName());

        List currentSteps = store.findCurrentSteps(id);
        ActionDescriptor action = wf.getAction(actionId);

        PropertySet ps = store.getPropertySet(id);
        Map transientVars = new HashMap();

        if (inputs != null) {
            transientVars.putAll(inputs);
        }

        populateTransientMap(entry, transientVars, wf.getRegisters(), new Integer(actionId), currentSteps);

        try {
            transitionWorkflow(entry, currentSteps, store, wf, action, transientVars, inputs, ps);
            completeEntry(id);
        } catch (WorkflowException e) {
            context.setRollbackOnly();
            throw e;
        }
    }

    public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);
        WorkflowDescriptor wf = getWorkflow(entry.getWorkflowName());

        PropertySet ps = store.getPropertySet(id);
        Map transientVars = new HashMap();
        populateTransientMap(entry, transientVars, wf.getRegisters(), null, store.findCurrentSteps(id));

        executeFunction(wf.getTriggerFunction(triggerId), transientVars, ps);
    }

    public long initialize(String workflowName, int initialAction, Map inputs) throws InvalidRoleException, InvalidInputException, WorkflowException {
        WorkflowDescriptor wf = getWorkflow(workflowName);

        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.createEntry(workflowName);

        // start with a memory property set, but clone it after we have an ID
        PropertySet ps = store.getPropertySet(entry.getId());
        Map transientVars = new HashMap();

        if (inputs != null) {
            transientVars.putAll(inputs);
        }

        populateTransientMap(entry, transientVars, wf.getRegisters(), new Integer(initialAction), Collections.EMPTY_LIST);

        if (!canInitialize(workflowName, initialAction, transientVars, ps)) {
            context.setRollbackOnly();
            throw new InvalidRoleException("You are restricted from initializing this workflow");
        }

        ActionDescriptor action = wf.getInitialAction(initialAction);

        try {
            transitionWorkflow(entry, Collections.EMPTY_LIST, store, wf, action, transientVars, inputs, ps);
        } catch (WorkflowException e) {
            context.setRollbackOnly();
            throw e;
        }

        long entryId = entry.getId();

        // now clone the memory PS to the real PS
        //PropertySetManager.clone(ps, store.getPropertySet(entryId));
        return entryId;
    }

    /**
     * @ejb.interface-method
     */
    public List query(WorkflowQuery query) throws StoreException {
        return getPersistence().query(query);
    }

    /**
     * @ejb.interface-method
     */
    public List query(WorkflowExpressionQuery query) throws WorkflowException {
        return getPersistence().query(query);
    }

    /**
     * @ejb.interface-method
     */
    public boolean saveWorkflowDescriptor(String workflowName, WorkflowDescriptor descriptor, boolean replace) throws FactoryException {
        boolean success = ConfigLoader.saveWorkflow(workflowName, descriptor, replace);

        return success;
    }

    protected List getAvailableActionsForStep(WorkflowDescriptor wf, Step step, Map transientVars, PropertySet ps) throws WorkflowException {
        List l = new ArrayList();
        StepDescriptor s = wf.getStep(step.getStepId());

        if (s == null) {
            log.warn("getAvailableActionsForStep called for non-existent step Id #" + step.getStepId());

            return l;
        }

        List actions = s.getActions();

        if ((actions == null) || (actions.size() == 0)) {
            return l;
        }

        for (Iterator iterator2 = actions.iterator(); iterator2.hasNext();) {
            ActionDescriptor action = (ActionDescriptor) iterator2.next();
            RestrictionDescriptor restriction = action.getRestriction();
            String conditionType = null;
            List conditions = null;

            if (restriction != null) {
                conditionType = restriction.getConditionType();
                conditions = restriction.getConditions();
            }

            if (passesConditions(conditionType, conditions, Collections.unmodifiableMap(transientVars), ps)) {
                l.add(new Integer(action.getId()));
            }
        }

        return l;
    }

    protected WorkflowStore getPersistence() throws StoreException {
        return StoreFactory.getPersistence(context);
    }

    /**
     * Returns a workflow definition object associated with the given name.
     *
     * @param name the name of the workflow
     * @return the object graph that represents a workflow definition
     */
    protected synchronized WorkflowDescriptor getWorkflow(String name) throws FactoryException {
        return ConfigLoader.getWorkflow(name);
    }

    protected void completeEntry(long id) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        WorkflowDescriptor wf = getWorkflow(entry.getWorkflowName());

        Collection currentSteps = store.findCurrentSteps(id);

        boolean isCompleted = true;

        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step step = (Step) iterator.next();
            StepDescriptor stepDes = wf.getStep(step.getStepId());

            // if at least on current step have an available action
            if (stepDes.getActions().size() > 0) {
                isCompleted = false;
            }
        }

        if (isCompleted == true) {
            store.setEntryState(id, WorkflowEntry.COMPLETED);
        }
    }

    /**
     * Load the default configuration from the current context classloader. The search order is:
     * <li>osworkflow.xml</li>
     * <li>/osworkflow.xml</li>
     * <li>META-INF/osworkflow.xml</li>
     * <li>/META-INF/osworkflow.xml</li>
     */
    protected void loadConfig() throws FactoryException {
        loadConfig(null);
    }

    /**
     * Loads the configurtion file <b>osworkflow.xml</b> from the thread's class loader if no url is specified.
     * @param url the URL to first attempt to load the configuration file from. If this url is unavailable,
     * then the default search mechanism is used (as outlined in {@link #loadConfig}).
     */
    protected void loadConfig(URL url) throws FactoryException {
        if (configLoaded) {
            return;
        }

        InputStream is = null;

        if (url != null) {
            try {
                is = url.openStream();
            } catch (Exception ex) {
            }
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("META-INF/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/META-INF/osworkflow.xml");
            } catch (Exception e) {
            }
        }

        if (is != null) {
            ConfigLoader.load(is);
            configLoaded = true;
        }
    }

    protected Object loadObject(String clazz) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(clazz).newInstance();
        } catch (Exception e) {
            log.error("Could not load object '" + clazz + "'", e);

            return null;
        }
    }

    protected boolean passesCondition(ConditionDescriptor conditionDesc, Map transientVars, PropertySet ps) throws WorkflowException {
        String type = conditionDesc.getType();

        HashMap args = new HashMap(conditionDesc.getArgs());

        for (Iterator iterator = args.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            mapEntry.setValue(translateVariables((String) mapEntry.getValue(), transientVars, ps));
        }

        String clazz;

        if ("remote-ejb".equals(type)) {
            clazz = RemoteEJBCondition.class.getName();
        } else if ("local-ejb".equals(type)) {
            clazz = LocalEJBCondition.class.getName();
        } else if ("jndi".equals(type)) {
            clazz = JNDICondition.class.getName();
        } else if ("bsf".equals(type)) {
            clazz = BSFCondition.class.getName();
        } else if ("beanshell".equals(type)) {
            clazz = BeanShellCondition.class.getName();
        } else {
            clazz = (String) args.get(CLASS_NAME);
        }

        Condition condition = (Condition) loadObject(clazz);

        if (condition == null) {
            String message = "Could not load Condition: " + clazz;
            throw new WorkflowException(message);
        }

        try {
            boolean passed = condition.passesCondition(transientVars, args, ps);

            if (conditionDesc.isNegate()) {
                passed = !passed;
            }

            return passed;
        } catch (Exception e) {
            String message = "Unknown exception encountered when trying condition: " + clazz;
            context.setRollbackOnly();
            throw new WorkflowException(message, e);
        }
    }

    protected boolean passesConditions(String conditionType, List conditions, Map transientVars, PropertySet ps) throws WorkflowException {
        if ((conditions == null) || (conditions.size() == 0)) {
            return true;
        }

        boolean and = "AND".equals(conditionType);
        boolean or = !and;

        for (Iterator iterator = conditions.iterator(); iterator.hasNext();) {
            ConditionDescriptor conditionDescriptor = (ConditionDescriptor) iterator.next();
            boolean result = passesCondition(conditionDescriptor, transientVars, ps);

            if (and && !result) {
                return false;
            } else if (or && result) {
                return true;
            }
        }

        if (and) {
            return true;
        } else if (or) {
            return false;
        } else {
            return false;
        }
    }

    protected void populateTransientMap(WorkflowEntry entry, Map transientVars, List registers, Integer actionId, Collection currentSteps) throws WorkflowException {
        transientVars.put("context", context);
        transientVars.put("entry", entry);
        transientVars.put("store", getPersistence());
        transientVars.put("descriptor", getWorkflow(entry.getWorkflowName()));

        if (actionId != null) {
            transientVars.put("actionId", actionId);
        }

        transientVars.put("currentSteps", Collections.unmodifiableCollection(currentSteps));

        // now talk to the registers for any extra objects needed in scope
        for (Iterator iterator = registers.iterator(); iterator.hasNext();) {
            RegisterDescriptor register = (RegisterDescriptor) iterator.next();
            Map args = register.getArgs();

            String type = register.getType();
            String clazz;

            if ("remote-ejb".equals(type)) {
                clazz = RemoteEJBRegister.class.getName();
            } else if ("local-ejb".equals(type)) {
                clazz = LocalEJBRegister.class.getName();
            } else if ("jndi".equals(type)) {
                clazz = JNDIRegister.class.getName();
            } else if ("bsf".equals(type)) {
                clazz = BSFRegister.class.getName();
            } else if ("beanshell".equals(type)) {
                clazz = BeanShellRegister.class.getName();
            } else {
                clazz = (String) args.get(CLASS_NAME);
            }

            Register r = (Register) loadObject(clazz);

            if (r == null) {
                String message = "Could not load register class: " + clazz;
                throw new WorkflowException(message);
            }

            try {
                transientVars.put(register.getVariableName(), r.registerVariable(context, entry, args));
            } catch (Exception e) {
                String message = "An unknown exception occured while registering variable using class: " + clazz;
                context.setRollbackOnly();
                throw new WorkflowException(message, e);
            }
        }
    }

    /**
     * Validates input against a list of ValidatorDescriptor objects.
     *
     * @param entry the workflow instance
     * @param validators the list of ValidatorDescriptors
     * @param transientVars the transientVars
     * @param ps the persistence variables
     * @throws InvalidInputException if the input is deemed invalid by any validator
     */
    protected void verifyInputs(WorkflowEntry entry, List validators, Map transientVars, PropertySet ps) throws WorkflowException {
        for (Iterator iterator = validators.iterator(); iterator.hasNext();) {
            ValidatorDescriptor input = (ValidatorDescriptor) iterator.next();

            if (input != null) {
                String type = input.getType();
                HashMap args = new HashMap(input.getArgs());

                for (Iterator iterator2 = args.entrySet().iterator();
                        iterator2.hasNext();) {
                    Map.Entry mapEntry = (Map.Entry) iterator2.next();
                    mapEntry.setValue(translateVariables((String) mapEntry.getValue(), transientVars, ps));
                }

                String clazz;

                if ("remote-ejb".equals(type)) {
                    clazz = RemoteEJBValidator.class.getName();
                } else if ("local-ejb".equals(type)) {
                    clazz = LocalEJBValidator.class.getName();
                } else if ("jndi".equals(type)) {
                    clazz = JNDIValidator.class.getName();
                } else if ("bsf".equals(type)) {
                    clazz = BSFValidator.class.getName();
                } else if ("beanshell".equals(type)) {
                    clazz = BeanShellValidator.class.getName();
                } else {
                    clazz = (String) args.get(CLASS_NAME);
                }

                Validator validator = (Validator) loadObject(clazz);

                if (validator == null) {
                    String message = "Could not load validator class: " + clazz;
                    throw new WorkflowException(message);
                }

                try {
                    validator.validate(transientVars, args, ps);
                } catch (InvalidInputException e) {
                    throw e;
                } catch (Exception e) {
                    String message = "An unknown exception occured executing Validator: " + clazz;
                    context.setRollbackOnly();
                    throw new WorkflowException(message, e);
                }
            }
        }
    }

    Object getVariableFromMaps(String var, Map transientVars, PropertySet ps) {
        int firstDot = var.indexOf('.');
        String actualVar = var;

        if (firstDot != -1) {
            actualVar = var.substring(0, firstDot);
        }

        Object o = transientVars.get(actualVar);

        if (o == null) {
            o = ps.getAsActualType(actualVar);
        }

        if (firstDot != -1) {
            o = beanProvider.getProperty(o, var.substring(firstDot + 1));
        }

        return o;
    }

    /**
     * Parses a string for instances of "${foo}" and returns a string with all instances replaced
     * with the string value of the foo object (<b>foo.toString()</b>). If the string being passed
     * in only refers to a single variable and contains no other characters (for example: ${foo}),
     * then the actual object is returned instead of converting it to a string.
     */
    Object translateVariables(String s, Map transientVars, PropertySet ps) {
        String temp = s.trim();

        if (temp.startsWith("${") && temp.endsWith("}") && (temp.indexOf('$', 1) == -1)) {
            // the string is just a variable reference, don't convert it to a string
            String var = temp.substring(2, temp.length() - 1);

            return getVariableFromMaps(var, transientVars, ps);
        } else {
            // the string passed in contains multiple variables (or none!) and should be treated as a string
            while (true) {
                int x = s.indexOf("${");
                int y = s.indexOf("}", x);

                if ((x != -1) && (y != -1)) {
                    String var = s.substring(x + 2, y);
                    String t = null;
                    Object o = getVariableFromMaps(var, transientVars, ps);

                    if (o != null) {
                        t = o.toString();
                    }

                    if (t != null) {
                        s = s.substring(0, x) + t + s.substring(y + 1);
                    } else {
                        // the variable doesn't exist, so don't display anything
                        s = s.substring(0, x) + s.substring(y + 1);
                    }
                } else {
                    break;
                }
            }

            return s;
        }
    }

    private Step getCurrentStep(WorkflowDescriptor wfDesc, int actionId, List currentSteps, Map transientVars, PropertySet ps) throws WorkflowException {
        if (currentSteps.size() == 1) {
            return (Step) currentSteps.get(0);
        }

        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step step = (Step) iterator.next();
            ActionDescriptor action = wfDesc.getStep(step.getStepId()).getAction(actionId);

            if (action != null) {
                List availActions = getAvailableActionsForStep(wfDesc, step, transientVars, ps);

                if (availActions.contains(new Integer(action.getId()))) {
                    return step;
                }
            }
        }

        return null;
    }

    private boolean canInitialize(String workflowName, int initialAction, Map transientVars, PropertySet ps) throws WorkflowException {
        WorkflowDescriptor wf = getWorkflow(workflowName);

        ActionDescriptor actionDescriptor = wf.getInitialAction(initialAction);

        if (actionDescriptor == null) {
            throw new WorkflowException("Invalid Initial Action");
        }

        RestrictionDescriptor restriction = actionDescriptor.getRestriction();
        String conditionType = null;
        List conditions = null;

        if (restriction != null) {
            conditionType = restriction.getConditionType();
            conditions = restriction.getConditions();
        }

        return passesConditions(conditionType, conditions, Collections.unmodifiableMap(transientVars), ps);
    }

    private void createNewCurrentStep(ResultDescriptor theResult, WorkflowEntry entry, WorkflowStore store, int actionId, Step currentStep, long[] previousIds, Map transientVars, PropertySet ps) throws StoreException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Outcome: stepId=" + theResult.getStep() + ", status=" + theResult.getStatus() + ", owner=" + theResult.getOwner() + ", actionId=" + actionId + ", currentStep=" + ((currentStep != null) ? currentStep.getStepId() : 0));
            }

            if (previousIds == null) {
                previousIds = new long[0];
            }

            String owner = TextUtils.noNull(theResult.getOwner());

            if (owner.equals("")) {
                owner = null;
            } else {
                Object o = translateVariables(owner, transientVars, ps);
                owner = (o != null) ? o.toString() : null;
            }

            String oldStatus = theResult.getOldStatus();
            oldStatus = translateVariables(oldStatus, transientVars, ps).toString();

            String status = theResult.getStatus();
            status = translateVariables(status, transientVars, ps).toString();

            if (currentStep != null) {
                store.markFinished(currentStep, actionId, new Date(), oldStatus, context.getCaller());
                store.moveToHistory(currentStep);

                //store.moveToHistory(actionId, new Date(), currentStep, oldStatus, context.getCaller());
            }

            // construct the start date and optional due date
            Date startDate = new Date();
            Date dueDate = null;

            if ((theResult.getDueDate() != null) && (theResult.getDueDate().length() > 0)) {
                Object dueDateObject = translateVariables(theResult.getDueDate(), transientVars, ps);

                if (dueDateObject instanceof Date) {
                    dueDate = (Date) dueDateObject;
                } else if (dueDateObject instanceof String) {
                    long offset = TextUtils.parseLong((String) dueDateObject);

                    if (offset > 0) {
                        dueDate = new Date(startDate.getTime() + offset);
                    }
                } else if (dueDateObject instanceof Number) {
                    Number num = (Number) dueDateObject;
                    long offset = num.longValue();

                    if (offset > 0) {
                        dueDate = new Date(startDate.getTime() + offset);
                    }
                }
            }

            store.createCurrentStep(entry.getId(), theResult.getStep(), owner, startDate, dueDate, status, previousIds);
        } catch (StoreException e) {
            context.setRollbackOnly();
            throw e;
        }
    }

    /**
     * Executes a function.
     *
     * @param function the function to execute
     * @param transientVars the transientVars given by the end-user
     * @param ps the persistence variables
     */
    private void executeFunction(FunctionDescriptor function, Map transientVars, PropertySet ps) throws WorkflowException {
        if (function != null) {
            String type = function.getType();

            HashMap args = new HashMap(function.getArgs());

            for (Iterator iterator = args.entrySet().iterator();
                    iterator.hasNext();) {
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                mapEntry.setValue(translateVariables((String) mapEntry.getValue(), transientVars, ps));
            }

            String clazz;

            if ("remote-ejb".equals(type)) {
                clazz = RemoteEJBFunctionProvider.class.getName();
            } else if ("local-ejb".equals(type)) {
                clazz = LocalEJBFunctionProvider.class.getName();
            } else if ("jndi".equals(type)) {
                clazz = JNDIFunctionProvider.class.getName();
            } else if ("bsf".equals(type)) {
                clazz = BSFFunctionProvider.class.getName();
            } else if ("beanshell".equals(type)) {
                clazz = BeanShellFunctionProvider.class.getName();
            } else {
                clazz = (String) args.get(CLASS_NAME);
            }

            FunctionProvider provider = (FunctionProvider) loadObject(clazz);

            if (provider == null) {
                String message = "Could not load FunctionProvider class: " + clazz;
                context.setRollbackOnly();
                throw new WorkflowException(message);
            }

            try {
                provider.execute(transientVars, args, ps);
            } catch (WorkflowException e) {
                context.setRollbackOnly();
                throw e;
            }
        }
    }

    private void transitionWorkflow(WorkflowEntry entry, List currentSteps, WorkflowStore store, WorkflowDescriptor wf, ActionDescriptor action, Map transientVars, Map inputs, PropertySet ps) throws WorkflowException {
        Step step = getCurrentStep(wf, action.getId(), currentSteps, transientVars, ps);

        // validate transientVars (optional)
        if (action.getValidators().size() > 0) {
            verifyInputs(entry, action.getValidators(), Collections.unmodifiableMap(transientVars), ps);
        }

        // preFunctions
        List preFunctions = action.getPreFunctions();

        for (Iterator iterator = preFunctions.iterator(); iterator.hasNext();) {
            FunctionDescriptor function = (FunctionDescriptor) iterator.next();
            executeFunction(function, transientVars, ps);
        }

        // check each conditional result
        List conditionalResults = action.getConditionalResults();
        List extraPreFunctions = null;
        List extraPostFunctions = null;
        ResultDescriptor[] theResults = new ResultDescriptor[1];

        for (Iterator iterator = conditionalResults.iterator();
                iterator.hasNext();) {
            ConditionalResultDescriptor conditionalResult = (ConditionalResultDescriptor) iterator.next();

            if (passesConditions(conditionalResult.getConditionType(), conditionalResult.getConditions(), Collections.unmodifiableMap(transientVars), ps)) {
                //if (evaluateExpression(conditionalResult.getCondition(), entry, wf.getRegisters(), null, transientVars)) {
                theResults[0] = conditionalResult;
                verifyInputs(entry, conditionalResult.getValidators(), Collections.unmodifiableMap(transientVars), ps);
                extraPreFunctions = conditionalResult.getPreFunctions();
                extraPostFunctions = conditionalResult.getPostFunctions();

                break;
            }
        }

        // use unconditional-result if a condition hasn't been met
        if (theResults[0] == null) {
            theResults[0] = action.getUnconditionalResult();
            verifyInputs(entry, theResults[0].getValidators(), Collections.unmodifiableMap(transientVars), ps);
            extraPreFunctions = theResults[0].getPreFunctions();
            extraPostFunctions = theResults[0].getPostFunctions();
        }

        if (log.isDebugEnabled()) {
            log.debug("theResult=" + theResults[0].getStep() + " " + theResults[0].getStatus());
        }

        if (extraPreFunctions != null) {
            // run any extra pre-functions that haven't been run already
            for (Iterator iterator = extraPreFunctions.iterator();
                    iterator.hasNext();) {
                FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                executeFunction(function, transientVars, ps);
            }
        }

        // go to next step
        if (theResults[0].getSplit() != 0) {
            // the result is a split request, handle it correctly
            SplitDescriptor splitDesc = wf.getSplit(theResults[0].getSplit());
            Collection results = splitDesc.getResults();
            List splitPreFunctions = new ArrayList();
            List splitPostFunctions = new ArrayList();

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                ResultDescriptor resultDescriptor = (ResultDescriptor) iterator.next();
                verifyInputs(entry, resultDescriptor.getValidators(), Collections.unmodifiableMap(transientVars), ps);
                splitPreFunctions.addAll(resultDescriptor.getPreFunctions());
                splitPostFunctions.addAll(resultDescriptor.getPostFunctions());
            }

            // now execute the pre-functions
            for (Iterator iterator = splitPreFunctions.iterator();
                    iterator.hasNext();) {
                FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                executeFunction(function, transientVars, ps);
            }

            // now make these steps...
            boolean moveFirst = true;

            theResults = new ResultDescriptor[results.size()];
            results.toArray(theResults);

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                ResultDescriptor resultDescriptor = (ResultDescriptor) iterator.next();
                Step moveToHistoryStep = null;

                if (moveFirst) {
                    moveToHistoryStep = step;
                }

                long[] previousIds = null;

                if (step != null) {
                    previousIds = new long[] {step.getId()};
                }

                createNewCurrentStep(resultDescriptor, entry, store, action.getId(), moveToHistoryStep, previousIds, transientVars, ps);
                moveFirst = false;
            }

            // now execute the post-functions
            for (Iterator iterator = splitPostFunctions.iterator();
                    iterator.hasNext();) {
                FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                executeFunction(function, transientVars, ps);
            }
        } else if (theResults[0].getJoin() != 0) {
            // this is a join, finish this step...
            JoinDescriptor joinDesc = wf.getJoin(theResults[0].getJoin());
            step = store.markFinished(step, action.getId(), new Date(), theResults[0].getOldStatus(), context.getCaller());

            // ... now check to see if the expression evaluates
            // (get only current steps that have a result to this join)
            ArrayList joinSteps = new ArrayList();
            joinSteps.add(step);

            //currentSteps = store.findCurrentSteps(id); // shouldn't need to refresh the list
            for (Iterator iterator = currentSteps.iterator();
                    iterator.hasNext();) {
                Step currentStep = (Step) iterator.next();

                if (currentStep.getId() != step.getId()) {
                    StepDescriptor stepDesc = wf.getStep(currentStep.getStepId());

                    if (stepDesc.resultsInJoin(theResults[0].getJoin())) {
                        joinSteps.add(currentStep);
                    }
                }
            }

            JoinNodes jn = new JoinNodes(joinSteps);
            transientVars.put("jn", jn);

            if (passesConditions(joinDesc.getConditionType(), joinDesc.getConditions(), Collections.unmodifiableMap(transientVars), ps)) {
                // move the rest without creating a new step ...
                ResultDescriptor joinresult = joinDesc.getResult();
                verifyInputs(entry, joinresult.getValidators(), Collections.unmodifiableMap(transientVars), ps);

                // now execute the pre-functions
                for (Iterator iterator = joinresult.getPreFunctions().iterator();
                        iterator.hasNext();) {
                    FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                    executeFunction(function, transientVars, ps);
                }

                long[] previousIds = new long[joinSteps.size()];
                int i = 1;

                for (Iterator iterator = joinSteps.iterator();
                        iterator.hasNext();) {
                    Step currentStep = (Step) iterator.next();

                    if (currentStep.getId() != step.getId()) {
                        //store.moveToHistory(currentStep.getActionId(), currentStep.getFinishDate(), currentStep, theResult.getOldStatus(), context.getCaller());
                        store.moveToHistory(currentStep);
                        previousIds[i] = currentStep.getId();
                        i++;
                    }
                }

                // ... now finish this step normally
                previousIds[0] = step.getId();
                theResults[0] = joinDesc.getResult();
                createNewCurrentStep(joinDesc.getResult(), entry, store, action.getId(), step, previousIds, transientVars, ps);

                // now execute the post-functions
                for (Iterator iterator = joinresult.getPostFunctions().iterator();
                        iterator.hasNext();) {
                    FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                    executeFunction(function, transientVars, ps);
                }
            }
        } else {
            // normal finish, no splits or joins
            long[] previousIds = null;

            if (step != null) {
                previousIds = new long[] {step.getId()};
            }

            createNewCurrentStep(theResults[0], entry, store, action.getId(), step, previousIds, transientVars, ps);
        }

        // postFunctions (BOTH)
        if (extraPostFunctions != null) {
            for (Iterator iterator = extraPostFunctions.iterator();
                    iterator.hasNext();) {
                FunctionDescriptor function = (FunctionDescriptor) iterator.next();
                executeFunction(function, transientVars, ps);
            }
        }

        List postFunctions = action.getPostFunctions();

        for (Iterator iterator = postFunctions.iterator(); iterator.hasNext();) {
            FunctionDescriptor function = (FunctionDescriptor) iterator.next();
            executeFunction(function, transientVars, ps);
        }

        //if executed action was an initial action then workflow is activated
        if ((wf.getInitialAction(action.getId()) != null) && (entry.getState() != WorkflowEntry.ACTIVATED)) {
            changeEntryState(entry.getId(), WorkflowEntry.ACTIVATED);
        }

        //we have our results, lets check if we need to autoexec any of them
        int[] availableActions = getAvailableActions(entry.getId(), inputs);

        if (availableActions.length != 0) {
            for (int i = 0; i < theResults.length; i++) {
                ResultDescriptor theResult = theResults[i];
                StepDescriptor toCheck = wf.getStep(theResult.getStep());

                if (toCheck != null) {
                    Iterator iter = toCheck.getActions().iterator();
MAIN: 
                    while (iter.hasNext()) {
                        ActionDescriptor descriptor = (ActionDescriptor) iter.next();

                        if (descriptor.getAutoExecute()) {
                            //check if it's an action we can actually perform
                            for (int j = 0; j < availableActions.length; j++) {
                                if (descriptor.getId() == availableActions[j]) {
                                    doAction(entry.getId(), descriptor.getId(), inputs);

                                    break MAIN;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}