/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.2 $
 */
public class RestrictionDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();
    protected String conditionType;

    //~ Constructors ///////////////////////////////////////////////////////////

    public RestrictionDescriptor() {
    }

    public RestrictionDescriptor(Element restriction) {
        init(restriction);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionType() {
        return conditionType;
    }

    public List getConditions() {
        return conditions;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(conditions);
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<restrict-to>");

        if (conditions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<conditions type=\"" + conditionType + "\">");

            for (int i = 0; i < conditions.size(); i++) {
                ConditionDescriptor condition = (ConditionDescriptor) conditions.get(i);
                condition.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</conditions>");
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</restrict-to>");
    }

    protected void init(Element restriction) {
        // set up condition - OPTIONAL
        Element conditions = XMLUtil.getChildElement(restriction, "conditions");

        if (conditions != null) {
            conditionType = conditions.getAttribute("type");

            NodeList conditionNodes = conditions.getElementsByTagName("condition");
            int length = conditionNodes.getLength();

            for (int i = 0; i < length; i++) {
                Element condition = (Element) conditionNodes.item(i);
                ConditionDescriptor conditionDescriptor = new ConditionDescriptor(condition);
                conditionDescriptor.setParent(this);
                this.conditions.add(conditionDescriptor);
            }
        }
    }
}
