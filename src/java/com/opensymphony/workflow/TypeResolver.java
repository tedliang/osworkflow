/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import java.util.HashMap;
import java.util.Map;


/**
 * Date: Aug 3, 2004
 * Time: 11:04:43 PM
 *
 * @author hani
 */
public class TypeResolver {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Map conditions = new HashMap();

    static {
        conditions.put("remote-ejb", "com.opensymphony.workflow.util.ejb.remote.RemoteEJBCondition");
        conditions.put("local-ejb", "com.opensymphony.workflow.util.ejb.local.LocalEJBCondition");
        conditions.put("jndi", "com.opensymphony.workflow.util.jndi.JNDICondition");
        conditions.put("beanshell", "com.opensymphony.workflow.util.beanshell.BeanShellCondition");
        conditions.put("bsf", "com.opensymphony.workflow.util.bsf.BSFCondition");
    }

    private static final Map registers = new HashMap();

    static {
        registers.put("remote-ejb", "com.opensymphony.workflow.util.ejb.remote.RemoteEJBRegister");
        registers.put("local-ejb", "com.opensymphony.workflow.util.ejb.local.LocalEJBRegister");
        registers.put("jndi", "com.opensymphony.workflow.util.jndi.JNDIRegister");
        registers.put("beanshell", "com.opensymphony.workflow.util.beanshell.BeanShellRegister");
        registers.put("bsf", "com.opensymphony.workflow.util.bsf.BSFRegister");
    }

    private static final Map validators = new HashMap();

    static {
        validators.put("remote-ejb", "com.opensymphony.workflow.util.ejb.remote.RemoteEJBValidator");
        validators.put("local-ejb", "com.opensymphony.workflow.util.ejb.local.LocalEJBValidator");
        validators.put("jndi", "com.opensymphony.workflow.util.jndi.JNDIValidator");
        validators.put("beanshell", "com.opensymphony.workflow.util.beanshell.BeanShellValidator");
        validators.put("bsf", "com.opensymphony.workflow.util.bsf.BSFValidator");
    }

    private static final Map functions = new HashMap();

    static {
        functions.put("remote-ejb", "com.opensymphony.workflow.util.ejb.remote.RemoteEJBFunctionProvider");
        functions.put("local-ejb", "com.opensymphony.workflow.util.ejb.local.LocalEJBFunctionProvider");
        functions.put("jndi", "com.opensymphony.workflow.util.jndi.JNDIFunctionProvider");
        functions.put("beanshell", "com.opensymphony.workflow.util.beanshell.BeanShellFunctionProvider");
        functions.put("bsf", "com.opensymphony.workflow.util.bsf.BSFFunctionProvider");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String getCondition(String name) {
        return (String) conditions.get(name);
    }

    public static String getFunction(String name) {
        return (String) functions.get(name);
    }

    public static String getRegister(String name) {
        return (String) registers.get(name);
    }

    public static String getValidator(String name) {
        return (String) validators.get(name);
    }
}
