/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.6 $
 */
public class ConditionalResultDescriptor extends ResultDescriptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();

    //~ Constructors ///////////////////////////////////////////////////////////

    public ConditionalResultDescriptor() {
    }

    public ConditionalResultDescriptor(Element conditionalResult) {
        init(conditionalResult);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public List getConditions() {
        return conditions;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        super.validate();

        if (conditions.size() == 0) {
            throw new InvalidWorkflowDescriptorException("Conditional result from " + ((ActionDescriptor) getParent()).getName() + " to " + getDestination() + " must have at least one condition");
        }

        ValidationHelper.validate(conditions);
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);

        StringBuffer buf = new StringBuffer();
        buf.append("<result");

        if (hasId()) {
            buf.append(" id=\"").append(getId()).append("\"");
        }

        buf.append(" old-status=\"").append(oldStatus).append("\"");

        if (join != 0) {
            buf.append(" join=\"").append(join).append("\"");
        } else if (split != 0) {
            buf.append(" split=\"").append(split).append("\"");
        } else {
            buf.append(" status=\"").append(status).append("\"");
            buf.append(" step=\"").append(step).append("\"");

            if ((owner != null) && (owner.length() > 0)) {
                buf.append(" owner=\"").append(owner).append("\"");
            }
        }

        buf.append(">");
        out.println(buf);

        for (int i = 0; i < conditions.size(); i++) {
            ConditionsDescriptor condition = (ConditionsDescriptor) conditions.get(i);
            condition.writeXML(out, indent);
        }

        if (validators.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<validators>");

            for (int i = 0; i < validators.size(); i++) {
                ValidatorDescriptor validator = (ValidatorDescriptor) validators.get(i);
                validator.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</validators>");
        }

        printPreFunctions(out, indent);
        printPostFunctions(out, indent);
        XMLUtil.printIndent(out, --indent);
        out.println("</result>");
    }

    protected void init(Element conditionalResult) {
        super.init(conditionalResult);

        List conditionNodes = XMLUtil.getChildElements(conditionalResult, "conditions");

        int length = conditionNodes.size();

        for (int i = 0; i < length; i++) {
            Element condition = (Element) conditionNodes.get(i);
            ConditionsDescriptor conditionDescriptor = new ConditionsDescriptor(condition);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }
    }

    private String getDestination() {
        if (join != 0) {
            return "join #" + join;
        } else if (split != 0) {
            return "split #" + split;
        } else {
            return "step #" + step;
        }
    }
}