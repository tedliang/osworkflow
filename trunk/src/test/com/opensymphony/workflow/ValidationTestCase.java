/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import junit.framework.TestCase;

import java.util.HashMap;


/**
 * Test Case for AbstractWorkflow.
 *
 * @author <a href="mailto:vorburger@users.sourceforge.net">Michael Vorburger</a>
 * @version $Id: ValidationTestCase.java,v 1.6 2004-03-25 06:05:17 hani Exp $ (Created on Feb 11, 2003 at 7:48:39 PM)
 */
public class ValidationTestCase extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    public ValidationTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Test whether an invalid unconditional-result in an initial-actions is indeed rejected as it should.
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-130">Jira issue WF-130</a>
     * @throws Exception If error while executing testing
     */
    public void testCheckResultInitialActionUnconditionalResult() throws Exception {
        try {
            WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("samples/unconditional-result.xml").toString());
            descriptor.validate();
            fail("descriptor loaded successfully, even though unconditional-result element is incorrect");
        } catch (InvalidWorkflowDescriptorException e) {
            //the descriptor is invalid, which is correct
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
        }
    }

    /**
     * Tests whether common-actions are implemented correctly.
     *
     * @throws Exception If error while executing testing
     */
    public void testCommonActions() throws Exception {
        try {
            WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("samples/common-actions.xml").toString());
            descriptor.validate();
        } catch (InvalidWorkflowDescriptorException e) {
            e.printStackTrace();
            fail("Descriptor did not recognized common-actions!");
        }

        try {
            WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("samples/common-actions-bad.xml").toString());
            descriptor.validate();
            fail("Invalid common-actions not detected correctly");
        } catch (InvalidWorkflowDescriptorException e) {
        }
    }

    /**
     * Test whether a duplicate action is correctly marked as invalid
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-192">Jira issue WF-192</a>
     * @throws Exception If error while executing testing
     */
    public void testDuplicateActionID() throws Exception {
        try {
            WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("samples/duplicate-action.xml").toString());
            descriptor.validate();
            fail("descriptor loaded successfully, even though duplicate action exists");
        } catch (InvalidWorkflowDescriptorException e) {
            //the descriptor is invalid, which is correct
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
        }
    }

    /**
     * Test validator
     */
    public void testValidator() throws Exception {
        Workflow workflow = new BasicWorkflow("testuser");

        try {
            long id = workflow.initialize(getClass().getClassLoader().getResource("samples/validator.xml").toString(), 1, new HashMap());
        } catch (InvalidInputException e) {
            //the descriptor is invalid, which is correct
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
        }

        fail("Did not get expected InvalidInputException");
    }
}
