/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import com.opensymphony.workflow.InvalidWorkflowDescriptorException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.*;

import java.io.*;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.*;


/**
 * The WorkflowLoader is responsible for creating a WorkflowDesciptor
 * by loading the XML from various sources.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.7 $
 */
public class WorkflowLoader {
    //~ Methods ////////////////////////////////////////////////////////////////

    public static WorkflowDescriptor load(final InputStream is) throws SAXException, IOException, InvalidWorkflowDescriptorException {
        return load(is, null);
    }

    /**
     * Load a workflow descriptor from a URL
     */
    public static WorkflowDescriptor load(final URL url) throws SAXException, IOException, InvalidWorkflowDescriptorException {
        return load(url.openStream(), url);
    }

    private static WorkflowDescriptor load(InputStream is, URL url) throws SAXException, IOException, InvalidWorkflowDescriptorException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(true);

        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
            db.setEntityResolver(new DTDEntityResolver());
        } catch (ParserConfigurationException e) {
            throw new SAXException("Error creating document builder", e);
        }

        db.setErrorHandler(new WorkflowErrorHandler(url));

        Document doc = db.parse(is);

        Element root = (Element) doc.getElementsByTagName("workflow").item(0);

        WorkflowDescriptor descriptor = DescriptorFactory.getFactory().createWorkflowDescriptor(root);
        descriptor.validate();

        return descriptor;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public static class AllExceptionsErrorHandler implements ErrorHandler {
        private final List exceptions = new ArrayList();

        public List getExceptions() {
            return exceptions;
        }

        public void error(SAXParseException exception) {
            addMessage(exception);
        }

        public void fatalError(SAXParseException exception) {
            addMessage(exception);
        }

        public void warning(SAXParseException exception) {
        }

        private void addMessage(SAXParseException exception) {
            exceptions.add(exception.getMessage() + " (line:" + exception.getLineNumber() + ((exception.getColumnNumber() > -1) ? (" col:" + exception.getColumnNumber()) : "") + ")");
        }
    }

    public static class WorkflowErrorHandler implements ErrorHandler {
        private URL url;

        public WorkflowErrorHandler(final URL url) {
            this.url = url;
        }

        public void error(SAXParseException exception) throws SAXException {
            throw new SAXException(getMessage(exception));
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            throw new SAXException(getMessage(exception));
        }

        public void warning(SAXParseException exception) throws SAXException {
        }

        private String getMessage(SAXParseException exception) {
            return exception.getMessage() + " (" + ((url != null) ? (" url=" + url + " ") : "") + "line:" + exception.getLineNumber() + ((exception.getColumnNumber() > -1) ? (" col:" + exception.getColumnNumber()) : "") + ")";
        }
    }
}
