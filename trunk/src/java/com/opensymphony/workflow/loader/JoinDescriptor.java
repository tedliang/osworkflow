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
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class JoinDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();
    protected ResultDescriptor result;
    protected String conditionType;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JoinDescriptor() {
    }

    public JoinDescriptor(Element join) {
        init(join);
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

    public void setResult(ResultDescriptor result) {
        this.result = result;
    }

    public ResultDescriptor getResult() {
        return result;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(conditions);
        result.validate();
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<join id=\"" + getId() + "\">");
        XMLUtil.printIndent(out, indent++);
        out.println("<conditions type=\"" + conditionType + "\">");

        for (int i = 0; i < conditions.size(); i++) {
            ConditionDescriptor result = (ConditionDescriptor) conditions.get(i);
            result.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</conditions>");
        result.writeXML(out, indent);
        XMLUtil.printIndent(out, --indent);
        out.println("</join>");
    }

    protected void init(Element join) {
        try {
            setId(Integer.parseInt(join.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid join id value " + join.getAttribute("id"));
        }

        // get conditions
        Element conditions = XMLUtil.getChildElement(join, "conditions");
        conditionType = conditions.getAttribute("type");

        NodeList conditionNodes = conditions.getElementsByTagName("condition");
        int length = conditionNodes.getLength();

        for (int i = 0; i < length; i++) {
            Element condition = (Element) conditionNodes.item(i);
            ConditionDescriptor conditionDescriptor = new ConditionDescriptor(condition);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }

        //<unconditional-result status="Underway" owner="test" step="2"/>
        Element resultElement = (Element) join.getElementsByTagName("unconditional-result").item(0);
        result = new ResultDescriptor(resultElement);
        result.setParent(this);
    }
}
