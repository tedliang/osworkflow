/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.DTDEntityResolver;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.XMLTestCase;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Test that when you save a descriptor, it is identical to the original.
 * Identical means with the same xml syntax, not the exact same text wise.
 *
 * @author Hani Suleiman (hani@formicary.net)
 * @author Eric Pugh (epugh@upstate.com)
 * Date: May 5, 2003
 * Time: 12:43:08 PM
 */
public class SaveDescriptorTestCase extends XMLTestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private DocumentBuilder documentBuilder;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SaveDescriptorTestCase(String s) throws ParserConfigurationException {
        super(s);
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        documentBuilder.setEntityResolver(new DTDEntityResolver());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *Test if a saved descriptor is identical to the original
     */
    public void testSave() throws Exception {
        URL url = getClass().getResource("/samples/saved.xml");
        Document inputDocument = documentBuilder.parse(url.toString());
        WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(url.toString());
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println(WorkflowDescriptor.XML_HEADER);
        writer.println(WorkflowDescriptor.DOCTYPE_DECL);
        descriptor.writeXML(new PrintWriter(out), 0);

        //        PrintWriter blah = new PrintWriter(new FileWriter("foo.xml"));
        //        descriptor.writeXML(blah, 0);
        //        blah.flush();
        Document outputDocument = getDocument(out.toString());

        Diff diff = new Diff(inputDocument, outputDocument);
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue(diff.toString(), diff.similar());
        assertXMLEqual("But they are equal when an ElementQualifier controls which test element is compared with each control element", diff, true);
        assertTrue(diff.toString(), diff.similar());
    }

    private Document getDocument(String xml) throws IOException, SAXException {
        InputSource source = new InputSource(new StringReader(xml));

        return documentBuilder.parse(source);
    }
}
