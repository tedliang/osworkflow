/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.WorkflowDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        db.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId == null) {
                        return null;
                    }

                    URL url = new URL(systemId);
                    String file = url.getFile();

                    if ((file != null) && (file.indexOf('/') > -1)) {
                        file = file.substring(file.lastIndexOf('/') + 1);
                    }

                    if ("www.opensymphony.com".equals(url.getHost())) {
                        InputStream is = getClass().getResourceAsStream("/META-INF/" + file);

                        if (is == null) {
                            is = getClass().getResourceAsStream("/" + file);
                        }

                        if (is != null) {
                            return new InputSource(is);
                        }
                    }

                    return null;
                }
            });

        Document doc = db.parse(new FileInputStream(file));
        Element root = (Element) doc.getElementsByTagName("workflow").item(0);

        WorkflowDescriptor descriptor = new WorkflowDescriptor(root);

        return descriptor;
    }
}
