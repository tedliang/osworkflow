/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class JoinDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();
    protected ResultDescriptor result;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JoinDescriptor() {
    }

    public JoinDescriptor(Element join) {
        init(join);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

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

        if (result == null) {
            throw new InvalidWorkflowDescriptorException("Join has no result");
        }

        result.validate();
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<join id=\"" + getId() + "\">");

        if (conditions.size() > 0) {
            for (int i = 0; i < conditions.size(); i++) {
                ConditionsDescriptor condition = (ConditionsDescriptor) conditions.get(i);
                condition.writeXML(out, indent);
            }
        }

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
        List conditionNodes = XMLUtil.getChildElements(join, "conditions");
        int length = conditionNodes.size();

        for (int i = 0; i < length; i++) {
            Element condition = (Element) conditionNodes.get(i);
            ConditionsDescriptor conditionDescriptor = new ConditionsDescriptor(condition);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }

        //<unconditional-result status="Underway" owner="test" step="2"/>
        Element resultElement = XMLUtil.getChildElement(join, "unconditional-result");
        result = new ResultDescriptor(resultElement);
        result.setParent(this);
    }
}