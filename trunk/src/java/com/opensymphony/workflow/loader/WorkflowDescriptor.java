/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;

import java.util.*;


/**
 * Describes a single workflow
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.2 $
 */
public class WorkflowDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List globalActions = new ArrayList();
    protected List initialActions = new ArrayList();
    protected List joins = new ArrayList();
    protected List registers = new ArrayList();
    protected List splits = new ArrayList();
    protected List steps = new ArrayList();
    protected Map timerFunctions = new HashMap();

    //~ Constructors ///////////////////////////////////////////////////////////

    public WorkflowDescriptor() {
    }

    public WorkflowDescriptor(Element root) {
        init(root);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public ActionDescriptor getAction(int id) {
        // check global actions
        for (Iterator iterator = globalActions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getId() == id) {
                return actionDescriptor;
            }
        }

        // check steps
        for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
            StepDescriptor stepDescriptor = (StepDescriptor) iterator.next();
            ActionDescriptor actionDescriptor = stepDescriptor.getAction(id);

            if (actionDescriptor != null) {
                return actionDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a List of the global actions specified
     * @return A list of {@link ActionDescriptor} objects
     */
    public List getGlobalActions() {
        return globalActions;
    }

    public ActionDescriptor getInitialAction(int id) {
        for (Iterator iterator = initialActions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getId() == id) {
                return actionDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a List of initial steps for this workflow
     * @return A list of {@link ActionDescriptor} objects
     */
    public List getInitialActions() {
        return initialActions;
    }

    public JoinDescriptor getJoin(int id) {
        for (Iterator iterator = joins.iterator(); iterator.hasNext();) {
            JoinDescriptor joinDescriptor = (JoinDescriptor) iterator.next();

            if (joinDescriptor.getId() == id) {
                return joinDescriptor;
            }
        }

        return null;
    }

    /**
    * Get a List of initial steps for this workflow
    * @return A list of {@link JoinDescriptor} objects
    */
    public List getJoins() {
        return joins;
    }

    public List getRegisters() {
        return registers;
    }

    public SplitDescriptor getSplit(int id) {
        for (Iterator iterator = splits.iterator(); iterator.hasNext();) {
            SplitDescriptor splitDescriptor = (SplitDescriptor) iterator.next();

            if (splitDescriptor.getId() == id) {
                return splitDescriptor;
            }
        }

        return null;
    }

    /**
    * Get a List of initial steps for this workflow
    * @return A list of {@link SplitDescriptor} objects
    */
    public List getSplits() {
        return splits;
    }

    public StepDescriptor getStep(int id) {
        for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
            StepDescriptor step = (StepDescriptor) iterator.next();

            if (step.getId() == id) {
                return step;
            }
        }

        return null;
    }

    /**
     * Get a List of steps in this workflow
     * @return a List of {@link StepDescriptor} objects
     */
    public List getSteps() {
        return steps;
    }

    /**
     * Update a trigger function
     * @param id The id for the trigger function
     * @param descriptor The descriptor for the trigger function
     * @return The old trigger function with the specified ID, if any existed
     */
    public FunctionDescriptor setTriggerFunction(int id, FunctionDescriptor descriptor) {
        return (FunctionDescriptor) timerFunctions.put(new Integer(id), descriptor);
    }

    public FunctionDescriptor getTriggerFunction(int id) {
        return (FunctionDescriptor) this.timerFunctions.get(new Integer(id));
    }

    /**
     * Get a Map of all trigger functions in this workflow
     * @return a Map with Integer keys and {@link FunctionDescriptor} values
     */
    public Map getTriggerFunctions() {
        return timerFunctions;
    }

    /**
     * Add a global action
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     * @param descriptor The action descriptor to add
     */
    public void addGlobalAction(ActionDescriptor descriptor) {
        if (getAction(descriptor.getId()) != null) {
            throw new IllegalArgumentException("Action with id " + descriptor.getId() + " already exists");
        }

        globalActions.add(descriptor);
    }

    /**
     * Add an initial action
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     * @param descriptor The action descriptor to add
     */
    public void addInitialAction(ActionDescriptor descriptor) {
        if (getAction(descriptor.getId()) != null) {
            throw new IllegalArgumentException("Action with id " + descriptor.getId() + " already exists");
        }

        initialActions.add(descriptor);
    }

    /**
     * Add a join
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     * @param descriptor The join descriptor to add
     */
    public void addJoin(JoinDescriptor descriptor) {
        if (getJoin(descriptor.getId()) != null) {
            throw new IllegalArgumentException("Join with id " + descriptor.getId() + " already exists");
        }

        joins.add(descriptor);
    }

    /**
     * Add a split
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     * @param descriptor The split descriptor to add
     */
    public void addSplit(SplitDescriptor descriptor) {
        if (getSplit(descriptor.getId()) != null) {
            throw new IllegalArgumentException("Split with id " + descriptor.getId() + " already exists");
        }

        splits.add(descriptor);
    }

    /**
     * Add a step
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     * @param descriptor The step descriptor to add
     */
    public void addStep(StepDescriptor descriptor) {
        if (getStep(descriptor.getId()) != null) {
            throw new IllegalArgumentException("Step with id " + descriptor.getId() + " already exists");
        }

        steps.add(descriptor);
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(this.getRegisters());
        ValidationHelper.validate(this.getTriggerFunctions().values());
        ValidationHelper.validate(this.getGlobalActions());
        ValidationHelper.validate(this.getInitialActions());
        ValidationHelper.validate(this.getSteps());
        ValidationHelper.validate(this.getSplits());
        ValidationHelper.validate(this.getJoins());

        Set actions = new HashSet();
        Iterator i = globalActions.iterator();

        while (i.hasNext()) {
            ActionDescriptor action = (ActionDescriptor) i.next();
            actions.add(new Integer(action.getId()));
        }

        i = getSteps().iterator();

        while (i.hasNext()) {
            StepDescriptor step = (StepDescriptor) i.next();
            Iterator j = step.getActions().iterator();

            while (j.hasNext()) {
                ActionDescriptor action = (ActionDescriptor) j.next();

                if (!actions.add(new Integer(action.getId()))) {
                    throw new InvalidWorkflowDescriptorException("Duplicate occurance of action ID " + action.getId() + " found in step " + step.getId());
                }
            }
        }
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<workflow>");

        if (registers.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<registers>");

            for (int i = 0; i < registers.size(); i++) {
                RegisterDescriptor register = (RegisterDescriptor) registers.get(i);
                register.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</registers>");
        }

        XMLUtil.printIndent(out, indent++);
        out.println("<initial-actions>");

        for (int i = 0; i < initialActions.size(); i++) {
            ActionDescriptor action = (ActionDescriptor) initialActions.get(i);
            action.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</initial-actions>");

        if (globalActions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<global-actions>");

            for (int i = 0; i < globalActions.size(); i++) {
                ActionDescriptor action = (ActionDescriptor) globalActions.get(i);
                action.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</global-actions>");
        }

        XMLUtil.printIndent(out, indent++);
        out.println("<steps>");

        for (int i = 0; i < steps.size(); i++) {
            StepDescriptor step = (StepDescriptor) steps.get(i);
            step.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</steps>");

        if (splits.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<splits>");

            for (int i = 0; i < splits.size(); i++) {
                SplitDescriptor split = (SplitDescriptor) splits.get(i);
                split.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</splits>");
        }

        if (joins.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<joins>");

            for (int i = 0; i < joins.size(); i++) {
                JoinDescriptor join = (JoinDescriptor) joins.get(i);
                join.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</joins>");
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</workflow>");
    }

    protected void init(Element root) {
        // handle registers - OPTIONAL
        Element r = XMLUtil.getChildElement(root, "registers");

        if (r != null) {
            NodeList registers = r.getElementsByTagName("register");

            for (int i = 0; i < registers.getLength(); i++) {
                Element register = (Element) registers.item(i);
                RegisterDescriptor registerDescriptor = new RegisterDescriptor(register);
                registerDescriptor.setParent(this);
                this.registers.add(registerDescriptor);
            }
        }

        // handle initial-steps - REQUIRED
        Element intialActionsElement = XMLUtil.getChildElement(root, "initial-actions");
        NodeList initialActions = intialActionsElement.getElementsByTagName("action");

        for (int i = 0; i < initialActions.getLength(); i++) {
            Element initialAction = (Element) initialActions.item(i);
            ActionDescriptor actionDescriptor = new ActionDescriptor(initialAction);
            actionDescriptor.setParent(this);
            this.initialActions.add(actionDescriptor);
        }

        // handle global-actions - OPTIONAL
        Element globalActionsElement = XMLUtil.getChildElement(root, "global-actions");

        if (globalActionsElement != null) {
            NodeList globalActions = globalActionsElement.getElementsByTagName("action");

            for (int i = 0; i < globalActions.getLength(); i++) {
                Element globalAction = (Element) globalActions.item(i);
                ActionDescriptor actionDescriptor = new ActionDescriptor(globalAction);
                actionDescriptor.setParent(this);
                this.globalActions.add(actionDescriptor);
            }
        }

        // handle timer-functions - OPTIONAL
        Element timerFunctionsElement = XMLUtil.getChildElement(root, "trigger-functions");

        if (timerFunctionsElement != null) {
            NodeList timerFunctions = timerFunctionsElement.getElementsByTagName("trigger-function");

            for (int i = 0; i < timerFunctions.getLength(); i++) {
                Element timerFunction = (Element) timerFunctions.item(i);
                Integer id = new Integer(timerFunction.getAttribute("id"));
                FunctionDescriptor function = new FunctionDescriptor((Element) timerFunction.getElementsByTagName("function").item(0));
                function.setParent(this);
                this.timerFunctions.put(id, function);
            }
        }

        // handle steps - REQUIRED
        Element stepsElement = XMLUtil.getChildElement(root, "steps");
        NodeList steps = stepsElement.getElementsByTagName("step");

        for (int i = 0; i < steps.getLength(); i++) {
            Element step = (Element) steps.item(i);
            StepDescriptor stepDescriptor = new StepDescriptor(step);
            stepDescriptor.setParent(this);
            this.steps.add(stepDescriptor);
        }

        // handle splits - OPTIONAL
        Element splitsElement = XMLUtil.getChildElement(root, "splits");

        if (splitsElement != null) {
            NodeList split = splitsElement.getElementsByTagName("split");

            for (int i = 0; i < split.getLength(); i++) {
                Element s = (Element) split.item(i);
                SplitDescriptor splitDescriptor = new SplitDescriptor(s);
                splitDescriptor.setParent(this);
                this.splits.add(splitDescriptor);
            }
        }

        // handle joins - OPTIONAL:
        Element joinsElement = XMLUtil.getChildElement(root, "joins");

        if (joinsElement != null) {
            NodeList join = joinsElement.getElementsByTagName("join");

            for (int i = 0; i < join.getLength(); i++) {
                Element s = (Element) join.item(i);
                JoinDescriptor joinDescriptor = new JoinDescriptor(s);
                joinDescriptor.setParent(this);
                this.joins.add(joinDescriptor);
            }
        }
    }
}
