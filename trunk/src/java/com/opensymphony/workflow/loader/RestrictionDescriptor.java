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
 * @version $Revision: 1.7 $
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

    /**
     * @deprecated A restrict-to can only have one conditions element,
     * please use {@link #getConditionsDescriptor()} instead.
     */
    public List getConditions() {
        return conditions;
    }

    public void setConditionsDescriptor(ConditionsDescriptor descriptor) {
        if (conditions.size() == 1) {
            conditions.set(0, descriptor);
        } else {
            conditions.add(descriptor);
        }
    }

    public ConditionsDescriptor getConditionsDescriptor() {
        if (conditions.size() == 0) {
            return null;
        }

        return (ConditionsDescriptor) conditions.get(0);
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        if (conditions.size() > 1) {
            throw new InvalidWorkflowDescriptorException("A restrict-to element can only have one conditions child element");
        }

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
            ConditionsDescriptor conditionDescriptor = DescriptorFactory.getFactory().createConditionsDescriptor(condition);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }
    }
}
