/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public class ConditionDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Map args = new HashMap();

    /**
     * The name field helps the editor identify the condition template used.
     */
    protected String name;
    protected String type;
    protected boolean negate = false;
    private List nestedConditions = new ArrayList();

    //~ Constructors ///////////////////////////////////////////////////////////

    public ConditionDescriptor() {
    }

    public ConditionDescriptor(Element function) {
        init(function);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Map getArgs() {
        return args;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public boolean isNegate() {
        return negate;
    }

    public boolean isNested() {
        return "nested".equals(type);
    }

    public void setNestedConditions(List nestedConditions) {
        this.nestedConditions = nestedConditions;
    }

    public List getNestedConditions() {
        return nestedConditions;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        if ("nested".equals(type) && (nestedConditions.size() == 0)) {
            throw new InvalidWorkflowDescriptorException("Nested condition must contain conditions");
        } else if ("nested".equals(type)) {
            String operator = (String) args.get("operator");

            if (operator == null) {
                throw new InvalidWorkflowDescriptorException("Nested condition must specify 'operator' argument");
            }

            if (!"AND".equals(operator) && !"OR".equals(operator)) {
                throw new InvalidWorkflowDescriptorException("Invalid operator argument '" + operator + "' for nested condition, must be AND or OR");
            }
        } else if (!"nested".equals(type) && (nestedConditions.size() > 0)) {
            throw new InvalidWorkflowDescriptorException("Condition type " + type + " cannot contain nested conditions");
        }
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<condition " + (hasId() ? ("id=\"" + getId() + "\" ") : "") + (((name != null) && (name.length() > 0)) ? ("name=\"" + getName() + "\" ") : "") + (negate ? ("negate=\"true\" ") : "") + "type=\"" + type + "\">");

        Iterator iter = args.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            XMLUtil.printIndent(out, indent);
            out.print("<arg name=\"");
            out.print(entry.getKey());
            out.print("\">");

            if ("beanshell".equals(type) || "bsf".equals(type)) {
                out.print("<![CDATA[");
                out.print(entry.getValue());
                out.print("]]>");
            } else {
                out.print(XMLUtil.encode(entry.getValue()));
            }

            out.println("</arg>");
        }

        for (int i = 0; i < nestedConditions.size(); i++) {
            ConditionDescriptor condition = (ConditionDescriptor) nestedConditions.get(i);
            condition.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</condition>");
    }

    protected void init(Element condition) {
        type = condition.getAttribute("type");

        try {
            setId(Integer.parseInt(condition.getAttribute("id")));
        } catch (NumberFormatException e) {
        }

        String n = condition.getAttribute("negate");

        if ("true".equalsIgnoreCase(n) || "yes".equalsIgnoreCase(n)) {
            negate = true;
        } else {
            negate = false;
        }

        if (condition.getAttribute("name") != null) {
            name = condition.getAttribute("name");
        }

        List args = XMLUtil.getChildElements(condition, "arg");

        for (int l = 0; l < args.size(); l++) {
            Element arg = (Element) args.get(l);
            this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
        }

        List children = XMLUtil.getChildElements(condition, "condition");

        for (int l = 0; l < children.size(); l++) {
            Element c = (Element) children.get(l);
            ConditionDescriptor nestedCondition = new ConditionDescriptor(c);
            nestedCondition.setParent(this);
            nestedConditions.add(nestedCondition);
        }
    }
}
