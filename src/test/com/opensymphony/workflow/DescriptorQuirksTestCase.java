/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;

import junit.framework.TestCase;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENT ME!
 */
public class DescriptorQuirksTestCase extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    public int counter = 0;
    private AbstractWorkflow workflow;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DescriptorQuirksTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Test if comments in args are correctly ignored.
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-178">Jira issue WF-178</a>
     * @throws Exception If error while executing testing
     */
    public void testArgComment() throws Exception {
        counter = 0;

        Map inputs = new HashMap();
        inputs.put("test", this);

        URL resource = getClass().getResource("/samples/comment-arg.xml");
        long id = workflow.initialize(resource.toString(), 1, inputs);
        assertEquals("beanshell script not parsed correctly", 2, counter);
    }

    /**
     * Test if comments in args are correctly ignored.
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-178">Jira issue WF-178</a>
     * @throws Exception If error while executing testing
     */
    public void testArgMultiText() throws Exception {
        counter = 0;

        Map inputs = new HashMap();
        inputs.put("test", this);

        URL resource = getClass().getResource("/samples/multitext-arg.xml");
        long id = workflow.initialize(resource.toString(), 1, inputs);
        assertEquals("beanshell script not parsed correctly", 2, counter);
    }

    /**
     * Test if functions are executed once in an unconditional-result
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-118">Jira issue WF-118</a>
     * @throws Exception If error while executing testing
     */
    public void testDoubleFunctionExecution() throws Exception {
        counter = 0;

        long id = workflow.initialize(getClass().getResource("/samples/double.xml").toString(), 1, new HashMap());
        Map inputs = new HashMap();
        inputs.put("test", this);
        workflow.doAction(id, 3, inputs);
        assertEquals("function executed unexpected number of times", 2, counter);
    }

    public void testVariableMutability() throws Exception {
        long id = workflow.initialize(getClass().getResource("/samples/variable-modify.xml").toString(), 100, null);
    }

    protected void setUp() throws Exception {
        workflow = new BasicWorkflow("testuser");
    }
}
