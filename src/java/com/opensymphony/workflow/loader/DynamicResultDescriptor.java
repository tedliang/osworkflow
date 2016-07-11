/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import org.w3c.dom.Element;

import java.io.PrintWriter;


/**
 * @author <a href="mailto:teddyliang@gmail.com">Ted Liang</a>
 */
public class DynamicResultDescriptor extends ResultDescriptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected String size;

    //~ Constructors ///////////////////////////////////////////////////////////

    DynamicResultDescriptor(Element conditionalResult) {
        init(conditionalResult);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getSize() {
        return size;
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);

        StringBuffer buf = new StringBuffer();
        buf.append("<dynamic-result");

        if (hasId()) {
            buf.append(" id=\"").append(getId()).append('\"');
        }

        if ((dueDate != null) && (dueDate.length() > 0)) {
            buf.append(" due-date=\"").append(getDueDate()).append('\"');
        }

        buf.append(" old-status=\"").append(oldStatus).append('\"');


        buf.append(" status=\"").append(status).append('\"');
        buf.append(" step=\"").append(step).append('\"');
        buf.append(" size=\"").append(size).append('\"');

        if ((owner != null) && (owner.length() > 0)) {
            buf.append(" owner=\"").append(owner).append('\"');
        }

        if ((displayName != null) && (displayName.length() > 0)) {
            buf.append(" display-name=\"").append(displayName).append('\"');
        }

        buf.append('>');
        out.println(buf);

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
        out.println("</dynamic-result>");
    }

    protected void init(Element dynamicResult) {
        super.init(dynamicResult);

        size = dynamicResult.getAttribute("size");
    }
}
