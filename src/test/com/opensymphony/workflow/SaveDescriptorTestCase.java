/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.WorkflowDescriptor;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.XMLTestCase;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.net.URL;


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
    //~ Constructors ///////////////////////////////////////////////////////////

    public SaveDescriptorTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *Test if a saved descriptor is identical to the original
     */
    public void testSave() throws Exception {
        URL url = getClass().getResource("/samples/saved.xml");
        WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(url.toString());
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE workflow PUBLIC \"-//OpenSymphony Group//DTD OSWorkflow 2.5//EN\" \"http://www.opensymphony.com/osworkflow/workflow_2_5.dtd\">");
        descriptor.writeXML(new PrintWriter(out), 0);

        String inputString = getFileAsString(url.getFile());
        String outputString = out.toString();

        Diff diff = new Diff(inputString, outputString);
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue(diff.similar());
        assertXMLEqual("But they are equal when an ElementQualifier controls which test element is compared with each control element", diff, true);

        assertTrue(diff.similar());
    }

    private static String getFileAsString(String scriptFile) throws Exception {
        File file = new File(scriptFile);
        Assert.assertTrue(file.exists());

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        return readTextStream(bis);
    }

    private static String readTextStream(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        StringBuffer sb = new StringBuffer(100);
        int c = 0;

        while (c > -1) {
            c = reader.read();

            if (c > -1) {
                sb.append((char) c);
            }
        }

        return sb.toString();
    }
}
