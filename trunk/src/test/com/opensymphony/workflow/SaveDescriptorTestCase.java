package com.opensymphony.workflow;

import java.io.*;

import junit.framework.TestCase;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 5, 2003
 * Time: 12:43:08 PM
 */
public class SaveDescriptorTestCase extends TestCase
{
  public SaveDescriptorTestCase(String s) {
      super(s);
  }

  /**
   *Test if a saved descriptor is identical to the original
   */
  public void testSave() throws Exception
  {
    WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor("file:saved.xml");
    StringWriter out = new StringWriter();
    PrintWriter writer = new PrintWriter(out);
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<!DOCTYPE workflow PUBLIC \"-//OpenSymphony Group//DTD OSWorkflow 2.5//EN\" \"http://www.opensymphony.com/osworkflow/workflow_2_5.dtd\">");
    descriptor.writeXML(new PrintWriter(out), 0);
    int origLength = (int)new File("saved.xml").length();
    int savedLength = out.toString().length();
    int diff = Math.abs(origLength - savedLength);
    assertEquals("Difference between saved and original is " + diff, diff<4, true);
  }
}
