/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.WorkflowDescriptor;

import junit.framework.TestCase;

import java.io.*;

import java.net.URL;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 5, 2003
 * Time: 12:43:08 PM
 */
public class SaveDescriptorTestCase extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    public SaveDescriptorTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *Test if a saved descriptor is identical to the original
     */
    public void testSave() throws Exception {
        URL url = getClass().getClassLoader().getResource("/samples/saved.xml");
        WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(url.toString());
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE workflow PUBLIC \"-//OpenSymphony Group//DTD OSWorkflow 2.5//EN\" \"http://www.opensymphony.com/osworkflow/workflow_2_5.dtd\">");
        descriptor.writeXML(new PrintWriter(out), 0);

        int origLength = (int) new File(url.getFile()).length();
        int savedLength = out.toString().length();
        int diff = Math.abs(origLength - savedLength);
        assertEquals("Difference between saved and original is " + diff, diff < 4, true);
    }
}
