/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.util.Validatable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.4 $
 */
public class ResultDescriptor extends AbstractDescriptor implements Validatable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List postFunctions = new ArrayList();
    protected List preFunctions = new ArrayList();
    protected List validators = new ArrayList();
    protected String dueDate;
    protected String oldStatus;
    protected String owner;
    protected String status;
    protected boolean hasStep = false;
    protected int join;
    protected int split;
    protected int step = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ResultDescriptor() {
    }

    public ResultDescriptor(Element result) {
        init(result);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getDueDate() {
        return dueDate;
    }

    public void setJoin(int join) {
        this.join = join;
    }

    public int getJoin() {
        return join;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public List getPostFunctions() {
        return postFunctions;
    }

    public List getPreFunctions() {
        return preFunctions;
    }

    public void setSplit(int split) {
        this.split = split;
    }

    public int getSplit() {
        return split;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStep(int step) {
        this.step = step;
        hasStep = true;
    }

    public int getStep() {
        return step;
    }

    public List getValidators() {
        return validators;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(preFunctions);
        ValidationHelper.validate(postFunctions);
        ValidationHelper.validate(validators);

        //if it's not a split or a join, then we require a next step
        if ((split == 0) && (join == 0)) {
            StringBuffer error = new StringBuffer("Result ");

            if (getId() > 0) {
                error.append("#").append(getId());
            }

            error.append(" is not a split or join, and has no ");

            if (!hasStep) {
                throw new InvalidWorkflowDescriptorException(error.append("next step").toString());
            }

            if (status == null || status.length() == 0) {
                throw new InvalidWorkflowDescriptorException(error.append("status").toString());
            }
        }

        //taken out for now
        //if ((split != 0) && ((join != 0) || (oldStatus.length() > 0) || (step != 0) || (status.length() > 0) || (owner.length() != 0))) {
        //    throw new InvalidWorkflowDescriptorException("Result " + id + " has a split attribute, so should not any other attributes");
        //} else if ((join != 0) && ((split != 0) || (oldStatus.length() > 0) || (step != 0) || (status.length() > 0) || (owner.length() != 0))) {
        //    throw new InvalidWorkflowDescriptorException("Result has a join attribute, should thus not any other attributes");
        //} else if ((oldStatus.length() == 0) || (step == 0) || (status.length() == 0)) {
        //    throw new InvalidWorkflowDescriptorException("old-status, step, status and owner attributes are required if no split or join");
        //}
    }

    public void writeXML(PrintWriter out, int indent) {
        XMLUtil.printIndent(out, indent++);

        StringBuffer buf = new StringBuffer();
        buf.append("<unconditional-result");

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

        if ((preFunctions.size() == 0) && (postFunctions.size() == 0)) {
            buf.append("/>");
            out.println(buf.toString());
        } else {
            buf.append(">");
            out.println(buf.toString());
            printPreFunctions(out, indent);
            printPostFunctions(out, indent);
            XMLUtil.printIndent(out, --indent);
            out.println("</unconditional-result>");
        }
    }

    protected void init(Element result) {
        oldStatus = result.getAttribute("old-status");
        status = result.getAttribute("status");

        try {
            setId(Integer.parseInt(result.getAttribute("id")));
        } catch (NumberFormatException e) {
        }

        dueDate = result.getAttribute("due-date");

        try {
            split = Integer.parseInt(result.getAttribute("split"));
        } catch (Exception ex) {
        }

        try {
            join = Integer.parseInt(result.getAttribute("join"));
        } catch (Exception ex) {
        }

        try {
            step = Integer.parseInt(result.getAttribute("step"));
            hasStep = true;
        } catch (Exception ex) {
        }

        owner = result.getAttribute("owner");

        // set up validators -- OPTIONAL
        Element v = XMLUtil.getChildElement(result, "validators");

        if (v != null) {
            NodeList validators = v.getElementsByTagName("validator");

            for (int k = 0; k < validators.getLength(); k++) {
                Element validator = (Element) validators.item(k);
                ValidatorDescriptor validatorDescriptor = new ValidatorDescriptor(validator);
                validatorDescriptor.setParent(this);
                this.validators.add(validatorDescriptor);
            }
        }

        // set up pre-functions -- OPTIONAL
        Element pre = XMLUtil.getChildElement(result, "pre-functions");

        if (pre != null) {
            NodeList preFunctions = pre.getElementsByTagName("function");

            for (int k = 0; k < preFunctions.getLength(); k++) {
                Element preFunction = (Element) preFunctions.item(k);
                FunctionDescriptor functionDescriptor = new FunctionDescriptor(preFunction);
                functionDescriptor.setParent(this);
                this.preFunctions.add(functionDescriptor);
            }
        }

        // set up post-functions - OPTIONAL
        Element post = XMLUtil.getChildElement(result, "post-functions");

        if (post != null) {
            NodeList postFunctions = post.getElementsByTagName("function");

            for (int k = 0; k < postFunctions.getLength(); k++) {
                Element postFunction = (Element) postFunctions.item(k);
                FunctionDescriptor functionDescriptor = new FunctionDescriptor(postFunction);
                functionDescriptor.setParent(this);
                this.postFunctions.add(functionDescriptor);
            }
        }
    }

    protected void printPostFunctions(PrintWriter out, int indent) {
        if (postFunctions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<post-functions>");

            Iterator iter = postFunctions.iterator();

            while (iter.hasNext()) {
                FunctionDescriptor function = (FunctionDescriptor) iter.next();
                function.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</post-functions>");
        }
    }

    protected void printPreFunctions(PrintWriter out, int indent) {
        if (preFunctions.size() > 0) {
            XMLUtil.printIndent(out, indent++);
            out.println("<pre-functions>");

            Iterator iter = preFunctions.iterator();

            while (iter.hasNext()) {
                FunctionDescriptor function = (FunctionDescriptor) iter.next();
                function.writeXML(out, indent);
            }

            XMLUtil.printIndent(out, --indent);
            out.println("</pre-functions>");
        }
    }
}
