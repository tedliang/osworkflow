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
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.5 $
 */
public class RestrictionDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();

    //~ Constructors ///////////////////////////////////////////////////////////

    public RestrictionDescriptor() {
    }

    public RestrictionDescriptor(Element restriction) {
        init(restriction);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public List getConditions() {
        return conditions;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(conditions);
    }

    public void writeXML(PrintWriter out, int indent) {
        if (conditions.size() == 0) {
            return;
        }

        XMLUtil.printIndent(out, indent++);
        out.println("<restrict-to>");

        if (conditions.size() > 0) {
            for (int i = 0; i < conditions.size(); i++) {
                ConditionsDescriptor condition = (ConditionsDescriptor) conditions.get(i);
                condition.writeXML(out, indent);
            }
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</restrict-to>");
    }

    protected void init(Element restriction) {
        // set up condition - OPTIONAL
        List conditionNodes = XMLUtil.getChildElements(restriction, "conditions");
        int length = conditionNodes.size();

        for (int i = 0; i < length; i++) {
            Element condition = (Element) conditionNodes.get(i);
            ConditionsDescriptor conditionDescriptor = new ConditionsDescriptor(condition);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }
    }
}
