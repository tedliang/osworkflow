/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.*;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.7 $
 */
public class ActionDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List conditionalResults = new ArrayList();
    protected List postFunctions = new ArrayList();
    protected List preFunctions = new ArrayList();
    protected List validators = new ArrayList();
    protected Map metaAttributes = new HashMap();
    protected RestrictionDescriptor restriction;
    protected ResultDescriptor unconditionalResult;
    protected String name;
    protected String view;
    protected boolean autoExecute = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ActionDescriptor() {
    }

    public ActionDescriptor(Element action) {
        init(action);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setAutoExecute(boolean autoExecute) {
        this.autoExecute = autoExecute;
    }

    public boolean getAutoExecute() {
        return autoExecute;
    }

    public List getConditionalResults() {
        return conditionalResults;
    }

    public void setMetaAttributes(Map metaAttributes) {
        this.metaAttributes = metaAttributes;
    }

    public Map getMetaAttributes() {
        return metaAttributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List getPostFunctions() {
        return postFunctions;
    }

    public List getPreFunctions() {
        return preFunctions;
    }

    public void setRestriction(RestrictionDescriptor restriction) {
        this.restriction = restriction;
    }

    public RestrictionDescriptor getRestriction() {
        return restriction;
    }

    public void setUnconditionalResult(ResultDescriptor unconditionalResult) {
        this.unconditionalResult = unconditionalResult;
    }

    public ResultDescriptor getUnconditionalResult() {
        return unconditionalResult;
    }

    public List getValidators() {
        return validators;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (name != null) {
            sb.append(name);
        }

        if ((view != null) && (view.length() > 0)) {
            sb.append(" (").append(view).append(")");
        }

        return sb.toString();
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(preFunctions);
        ValidationHelper.validate(postFunctions);
        ValidationHelper.validate(validators);
        ValidationHelper.validate(conditionalResults);

        if (restriction != null) {
            restriction.validate();
        }

        if (unconditionalResult != null) {
            unconditionalResult.validate();
        }
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);

        StringBuffer buf = new StringBuffer("<action id=\"");
        buf.append(getId());
        buf.append("\"");

        if ((name != null) && (name.length() > 0)) {
            buf.append(" name=\"");
            buf.append(name);
            buf.append("\"");
        }

        if ((view != null) && (view.length() > 0)) {
            buf.append(" view=\"");
            buf.append(view);
            buf.append("\"");
        }

        if (autoExecute) {
            buf.append(" auto=\"true\"");
        }

        buf.append(">");
        out.println(buf.toString());

        Iterator iter = metaAttributes.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            XMLUtil.printIndent(out, indent);
            out.print("<meta name=\"");
            out.print(entry.getKey());
            out.print("\">");
            out.print(entry.getValue());
            out.println("</meta>");
        }

        if (restriction != null) {
            restriction.writeXML(out, indent);
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

        if (preFunctions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<pre-functions>");

            for (int i = 0; i < preFunctions.size(); i++) {
                FunctionDescriptor function = (FunctionDescriptor) preFunctions.get(i);
                function.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</pre-functions>");
        }

        XMLUtil.printIndent(out, indent++);
        out.println("<results>");

        for (int i = 0; i < conditionalResults.size(); i++) {
            ConditionalResultDescriptor result = (ConditionalResultDescriptor) conditionalResults.get(i);
            result.writeXML(out, indent);
        }

        if (unconditionalResult != null) {
            unconditionalResult.writeXML(out, indent);
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</results>");

        if (postFunctions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<post-functions>");

            for (int i = 0; i < postFunctions.size(); i++) {
                FunctionDescriptor function = (FunctionDescriptor) postFunctions.get(i);
                function.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</post-functions>");
        }

        XMLUtil.printIndent(out, --indent);
        out.println("</action>");
    }

    protected void init(Element action) {
        try {
            setId(Integer.parseInt(action.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid action id value " + action.getAttribute("id"));
        }

        this.name = action.getAttribute("name");
        this.view = action.getAttribute("view");
        this.autoExecute = "true".equals(action.getAttribute("auto"));

        NodeList children = action.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          Node child = (Node) children.item(i);
          if (child.getNodeName().equals("meta")) {
            Element meta = (Element) child;
            String value = XMLUtil.getText(meta);
            this.metaAttributes.put(meta.getAttribute("name"), value);
          }
        }

        // set up validators - OPTIONAL
        Element v = XMLUtil.getChildElement(action, "validators");

        if (v != null) {
            NodeList validators = v.getElementsByTagName("validator");

            for (int k = 0; k < validators.getLength(); k++) {
                Element validator = (Element) validators.item(k);
                ValidatorDescriptor validatorDescriptor = new ValidatorDescriptor(validator);
                validatorDescriptor.setParent(this);
                this.validators.add(validatorDescriptor);
            }
        }

        // set up pre-functions - OPTIONAL
        Element pre = XMLUtil.getChildElement(action, "pre-functions");

        if (pre != null) {
            NodeList preFunctions = pre.getElementsByTagName("function");

            for (int k = 0; k < preFunctions.getLength(); k++) {
                Element preFunction = (Element) preFunctions.item(k);
                FunctionDescriptor functionDescriptor = new FunctionDescriptor(preFunction);
                functionDescriptor.setParent(this);
                this.preFunctions.add(functionDescriptor);
            }
        }

        // set up results - REQUIRED
        Element resultsElememt = XMLUtil.getChildElement(action, "results");
        NodeList results = resultsElememt.getElementsByTagName("result");

        for (int k = 0; k < results.getLength(); k++) {
            Element result = (Element) results.item(k);
            ConditionalResultDescriptor conditionalResultDescriptor = new ConditionalResultDescriptor(result);
            conditionalResultDescriptor.setParent(this);
            this.conditionalResults.add(conditionalResultDescriptor);
        }

        Element unconditionalResult = (Element) resultsElememt.getElementsByTagName("unconditional-result").item(0);
        this.unconditionalResult = new ResultDescriptor(unconditionalResult);
        this.unconditionalResult.setParent(this);

        // set up post-functions - OPTIONAL
        Element post = XMLUtil.getChildElement(action, "post-functions");

        if (post != null) {
            NodeList postFunctions = post.getElementsByTagName("function");

            for (int k = 0; k < postFunctions.getLength(); k++) {
                Element postFunction = (Element) postFunctions.item(k);
                FunctionDescriptor functionDescriptor = new FunctionDescriptor(postFunction);
                functionDescriptor.setParent(this);
                this.postFunctions.add(functionDescriptor);
            }
        }

        // set up restrict-to - OPTIONAL
        Element restrictElement = XMLUtil.getChildElement(action, "restrict-to");

        if (restrictElement != null) {
            restriction = new RestrictionDescriptor(restrictElement);

            if (restriction.getConditions().size() == 0) {
                restriction = null;
            } else {
                restriction.setParent(this);
            }
        }
    }
}
