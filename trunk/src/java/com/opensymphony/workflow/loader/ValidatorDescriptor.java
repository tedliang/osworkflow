/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.*;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.4 $
 */
public class ValidatorDescriptor extends AbstractDescriptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Map args = new HashMap();
    protected String type;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ValidatorDescriptor() {
    }

    public ValidatorDescriptor(Element validator) {
        init(validator);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Map getArgs() {
        return args;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<validator " + (hasId() ? ("id=\"" + getId() + "\" ") : "") + "type=\"" + type + "\">");

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
        out.println("</validator>");
    }

    protected void init(Element validator) {
        type = validator.getAttribute("type");

        try {
            setId(Integer.parseInt(validator.getAttribute("id")));
        } catch (NumberFormatException e) {
        }

        this.args = new HashMap();

        List args = XMLUtil.getChildElements(validator, "arg");

        for (int l = 0; l < args.size(); l++) {
            Element arg = (Element) args.get(l);
            this.args.put(arg.getAttribute("name"), XMLUtil.getText(arg));
        }
    }
}
