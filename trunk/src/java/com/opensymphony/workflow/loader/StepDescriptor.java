/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.*;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.6 $
 */
public class StepDescriptor extends AbstractDescriptor implements Validatable {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(StepDescriptor.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    protected List actions = new ArrayList();

    /**
     * this list maintained internally to allow for proper xml serialization.
     * All common-action elements in the XML file are expanded into ActionDescriptors
     * and are available via getActions()
     */
    protected List commonActions = new ArrayList();
    protected List permissions = new ArrayList();
    protected String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public StepDescriptor() {
    }

    public StepDescriptor(Element step) {
        init(step);
    }

    /** sets parent */
    public StepDescriptor(Element step, AbstractDescriptor parent) {
        setParent(parent);
        init(step);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public ActionDescriptor getAction(int id) {
        for (Iterator iterator = actions.iterator(); iterator.hasNext();) {
            ActionDescriptor action = (ActionDescriptor) iterator.next();

            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }

    /**
     * Get a List of {@link ActionDescriptor}s for this step
     */
    public List getActions() {
        return actions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Get a List of {@link PermissionDescriptor}s for this step
     */
    public List getPermissions() {
        return permissions;
    }

    public boolean resultsInJoin(int join) {
        for (Iterator iterator = actions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getUnconditionalResult().getJoin() == join) {
                return true;
            }

            List results = actionDescriptor.getConditionalResults();

            for (Iterator iterator2 = results.iterator(); iterator2.hasNext();) {
                ConditionalResultDescriptor resultDescriptor = (ConditionalResultDescriptor) iterator2.next();

                if (resultDescriptor.getJoin() == join) {
                    return true;
                }
            }
        }

        return false;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(actions);
        ValidationHelper.validate(permissions);

        Iterator iter = commonActions.iterator();

        while (iter.hasNext()) {
            Object o = iter.next();

            try {
                Integer actionId = new Integer(o.toString());
                ActionDescriptor commonActionReference = (ActionDescriptor) ((WorkflowDescriptor) getParent()).getCommonActions().get(actionId);

                if (commonActionReference == null) {
                    throw new InvalidWorkflowDescriptorException("Common action " + actionId + " specified in step " + getName() + " does not exist");
                }
            } catch (NumberFormatException ex) {
                throw new InvalidWorkflowDescriptorException("Common action " + o + " is not a valid action ID");
            }
        }
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.print("<step id=\"" + getId() + "\"");

        if ((name != null) && (name.length() > 0)) {
            out.print(" name=\"" + name + "\"");
        }

        out.println(">");

        if (permissions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<external-permissions>");

            for (int i = 0; i < permissions.size(); i++) {
                PermissionDescriptor permission = (PermissionDescriptor) permissions.get(i);
                permission.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</external-permissions>");
        }

        if (actions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<actions>");

            // special serialization common-action elements
            for (int i = 0; i < commonActions.size(); i++) {
                out.println("<common-action id=\"" + commonActions.get(i) + "\" />");
            }

            for (int i = 0; i < actions.size(); i++) {
                ActionDescriptor action = (ActionDescriptor) actions.get(i);
                action.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</actions>");
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</step>");
    }

    protected void init(Element step) {
        try {
            setId(Integer.parseInt(step.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid step id value " + step.getAttribute("id"));
        }

        name = step.getAttribute("name");

        // set up permissions - OPTIONAL
        Element p = XMLUtil.getChildElement(step, "external-permissions");

        if (p != null) {
            NodeList permissions = p.getElementsByTagName("permission");

            for (int i = 0; i < permissions.getLength(); i++) {
                Element permission = (Element) permissions.item(i);
                PermissionDescriptor permissionDescriptor = new PermissionDescriptor(permission);
                permissionDescriptor.setParent(this);
                this.permissions.add(permissionDescriptor);
            }
        }

        // set up actions - OPTIONAL
        Element a = XMLUtil.getChildElement(step, "actions");

        if (a != null) {
            NodeList actions = a.getElementsByTagName("action");

            for (int i = 0; i < actions.getLength(); i++) {
                Element action = (Element) actions.item(i);
                this.actions.add(new ActionDescriptor(action));
            }

            // look for common-action elements
            NodeList commonActions = a.getElementsByTagName("common-action");

            for (int i = 0; i < commonActions.getLength(); i++) {
                Element commonAction = (Element) commonActions.item(i);

                WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor) (getParent());

                try {
                    Integer actionId = new Integer(commonAction.getAttribute("id"));

                    ActionDescriptor commonActionReference = (ActionDescriptor) workflowDescriptor.getCommonActions().get(actionId);

                    if (commonActionReference != null) {
                        this.actions.add(commonActionReference);
                    }

                    this.commonActions.add(actionId);
                } catch (Exception ex) {
                    log.warn("Invalid common actionId:" + ex);
                }
            }
        }
    }
}
