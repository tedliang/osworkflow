/*
 * Created on Feb 12, 2004
 *
 *
 */
package com.opensymphony.workflow.spi.prevayler;

import com.opensymphony.workflow.TestWorkflow;
import com.opensymphony.workflow.spi.BaseFunctionalWorkflowTest;


/**
 * 
 * 
 * 
 * @author Christopher Farnham
 **/
public class PrevaylerFunctionalWorkflowTestCase extends BaseFunctionalWorkflowTest {

	/**
	 * @param s
	 */
	public PrevaylerFunctionalWorkflowTestCase(String s) {
		super(s);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		TestWorkflow.configFile = "/osworkflow-prevayler.xml";
		super.setUp();
	}

}
