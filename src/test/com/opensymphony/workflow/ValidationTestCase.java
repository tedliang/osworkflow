package com.opensymphony.workflow;

import junit.framework.TestCase;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * Test Case for AbstractWorkflow.
 *
 * @author <a href="mailto:vorburger@users.sourceforge.net">Michael Vorburger</a>
 * @version $Id: ValidationTestCase.java,v 1.2 2003-08-07 00:53:26 hani Exp $ (Created on Feb 11, 2003 at 7:48:39 PM)
 */
public class ValidationTestCase extends TestCase {

    public ValidationTestCase(String s) {
        super(s);
    }

    /**
     * Test whether an invalid unconditional-result in an initial-actions is indeed rejected as it should.
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-130">Jira issue WF-130</a>
     * @throws Exception If error while executing testing
     */
    public void testCheckResultInitialActionUnconditionalResult() throws Exception {

      try
      {
        WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("/unconditional-result.xml").toString());
        descriptor.validate();
        fail("descriptor loaded successfully, even though unconditional-result element is incorrect");
      }
      catch(InvalidWorkflowDescriptorException e)
      {
        //the descriptor is invalid, which is correct
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
        fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
      }
    }

  /**
   * Test whether a duplicate action is correctly marked as invalid
   *
   * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-192">Jira issue WF-192</a>
   * @throws Exception If error while executing testing
   */
  public void testDuplicateActionID() throws Exception
  {
    try
    {
      WorkflowDescriptor descriptor = DescriptorLoader.getDescriptor(getClass().getClassLoader().getResource("/duplicate-action.xml").toString());
      descriptor.validate();
      fail("descriptor loaded successfully, even though duplicate action exists");
    }
    catch(InvalidWorkflowDescriptorException e)
    {
      //the descriptor is invalid, which is correct
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of InvalidWorkflowDescriptorException");
    }

  }
}
