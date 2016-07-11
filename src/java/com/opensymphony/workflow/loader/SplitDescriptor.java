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
 */
public class SplitDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List results = new ArrayList();
    protected List dynamicResults = new ArrayList();
    protected List conditionalResults = new ArrayList();

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * @deprecated use {@link DescriptorFactory} instead
     */
    SplitDescriptor() {
    }

    /**
     * @deprecated use {@link DescriptorFactory} instead
     */
    SplitDescriptor(Element split) {
        init(split);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public List getResults() {
        return results;
    }

    public List getDynamicResults() {
        return dynamicResults;
    }

    public List getConditionalResults() {
        return conditionalResults;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(results);
        ValidationHelper.validate(dynamicResults);
        ValidationHelper.validate(conditionalResults);
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);
        out.println("<split id=\"" + getId() + "\">");

        for (int i = 0; i < results.size(); i++) {
            ResultDescriptor result = (ResultDescriptor) results.get(i);
            result.writeXML(out, indent);
        }

        for (int i = 0; i < dynamicResults.size(); i++) {
            DynamicResultDescriptor result = (DynamicResultDescriptor) dynamicResults.get(i);
            result.writeXML(out, indent);
        }

        for (int i = 0; i < conditionalResults.size(); i++) {
            ConditionalResultDescriptor result = (ConditionalResultDescriptor) conditionalResults.get(i);
            result.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</split>");
    }

    private void init(Element split) {
        try {
            setId(Integer.parseInt(split.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid split id value " + split.getAttribute("id"));
        }

        List uResults = XMLUtil.getChildElements(split, "unconditional-result");

        for (int i = 0; i < uResults.size(); i++) {
            Element result = (Element) uResults.get(i);
            ResultDescriptor resultDescriptor = new ResultDescriptor(result);
            resultDescriptor.setParent(this);
            results.add(resultDescriptor);
        }


        List dResults = XMLUtil.getChildElements(split, "dynamic-result");

        for (int i = 0; i < dResults.size(); i++) {
            Element result = (Element) dResults.get(i);
            DynamicResultDescriptor resultDescriptor = new DynamicResultDescriptor(result);
            resultDescriptor.setParent(this);
            dynamicResults.add(resultDescriptor);
        }


        List cResults = XMLUtil.getChildElements(split, "conditional-result");

        for (int i = 0; i < cResults.size(); i++) {
            Element result = (Element) cResults.get(i);
            ConditionalResultDescriptor resultDescriptor = new ConditionalResultDescriptor(result);
            resultDescriptor.setParent(this);
            conditionalResults.add(resultDescriptor);
        }

    }
}
