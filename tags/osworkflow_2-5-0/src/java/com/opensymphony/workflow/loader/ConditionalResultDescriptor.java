/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.1.1.1 $
 */
public class ConditionalResultDescriptor extends ResultDescriptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditions = new ArrayList();
    protected String conditionType;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ConditionalResultDescriptor() {
    }

    public ConditionalResultDescriptor(Element conditionalResult) {
        init(conditionalResult);
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
        XMLUtil.printIndent(out, indent++);
        out.println("<conditions type=\"" + conditionType + "\">");

        for (int i = 0; i < conditions.size(); i++) {
            ConditionDescriptor condition = (ConditionDescriptor) conditions.get(i);
            condition.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</conditions>");

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

        Element conditions = XMLUtil.getChildElement(conditionalResult, "conditions");
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
