/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * User: Hani Suleiman (hani@formicary.net)
 * Date: Feb 12, 2003
 * Time: 6:49:46 PM
 */
public class DescriptorLoader {
    //~ Methods ////////////////////////////////////////////////////////////////

    public static WorkflowDescriptor getDescriptor(String url) throws Exception {
        File file = new File(new URL(url).getFile());

        if (!file.exists()) {
            throw new IllegalArgumentException("file " + file + " does not exist");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DTDEntityResolver());
        db.setErrorHandler(new WorkflowLoader.WorkflowErrorHandler(new URL(url)));

        Document doc = db.parse(new FileInputStream(file));
        Element root = (Element) doc.getElementsByTagName("workflow").item(0);

        WorkflowDescriptor descriptor = DescriptorFactory.getFactory().createWorkflowDescriptor(root);

        return descriptor;
    }
}
