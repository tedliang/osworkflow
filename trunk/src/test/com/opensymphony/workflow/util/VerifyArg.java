package com.opensymphony.workflow.util;

import java.util.Map;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.module.propertyset.PropertySet;
import junit.framework.TestCase;

/**
 * @author hani Date: Apr 4, 2005 Time: 8:56:36 PM
 */
public class VerifyArg implements FunctionProvider
{
  public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException
  {
    Object expected = args.get("expected");
    Object actual = args.get("actual");
    TestCase.assertEquals(expected, actual);
  }
}
