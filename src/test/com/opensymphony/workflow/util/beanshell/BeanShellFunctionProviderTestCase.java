/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util.beanshell;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.database.JDBCPropertySet;

import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.TestWorkflow;

import junit.framework.TestCase;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test to prove that the BeanShellFunctionProvider eats the key exception
 * you need.  It tells you what line the script fails, but not the underlying
 * solution.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class BeanShellFunctionProviderTestCase extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    public BeanShellFunctionProviderTestCase(String s) {
        super(s);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Test that an exception that is thrown while processing a script is properly
     * dealt with.
     *
     * In this example, by not starting up a JDBC DataSource, the BeanShell script fails.
     * It should record the underlying lcoation of the NullPointerError JDBC error, but instead you get a null pointer
     * exception location of the script instead.
     *
     * What I want somewhere:
     * java.lang.NullPointerException
    at com.opensymphony.module.propertyset.database.JDBCPropertySet.setImpl(JDBCPropertySet.java:267)
    at com.opensymphony.module.propertyset.AbstractPropertySet.set(AbstractPropertySet.java:565)
    at com.opensymphony.module.propertyset.AbstractPropertySet.setString(AbstractPropertySet.java:363)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:324)
    at bsh.Reflect.invokeMethod(Unknown Source)
    at bsh.Reflect.invokeObjectMethod(Unknown Source)
    at bsh.Name.invokeMethod(Unknown Source)
    at bsh.BSHMethodInvocation.eval(Unknown Source)
     *
     */
    public void testThrowingException() throws Exception {
        TestWorkflow.configFile = "/osworkflow-jdbc.xml";

        TestWorkflow workflow = new TestWorkflow("test");

        // long workflowId = workflow.initialize(getClass().getResource("/example.xml").toString(), 1, new HashMap());
        BeanShellFunctionProvider function = new BeanShellFunctionProvider();
        PropertySet ps = new JDBCPropertySet();
        Map transientVars = new HashMap();
        Map args = new HashMap();
        args.put(AbstractWorkflow.BSH_SCRIPT, "String caller = \"testcaller\"; propertySet.setString(\"caller\", caller);");

        try {
            function.execute(transientVars, args, ps);
        } catch (Exception e) {
            String fullStackTrace = ExceptionUtils.getFullStackTrace(e);
            assertTrue("Make sure our stack trace records the error in JDBC:" + fullStackTrace, fullStackTrace.indexOf("at com.opensymphony.module.propertyset.database.JDBCPropertySet.setImpl") > -1);
        }
    }
}
