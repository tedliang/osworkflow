/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class ConditionDescriptor extends AbstractDescriptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Map args = new HashMap();

    /**
     * The name field helps the editor identify the condition template used.
     */
    protected String name;
    protected String type;
    protected boolean negate = false;

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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

        NodeList args = condition.getElementsByTagName("arg");

        for (int l = 0; l < args.getLength(); l++) {
            Element arg = (Element) args.item(l);
            this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
        }
    }
}
