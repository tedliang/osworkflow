/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.workflow.*;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


/**
 * @author hani Date: Dec 8, 2004 Time: 7:24:55 PM
 */
public class SpringTypeResolver extends TypeResolver implements ApplicationContextAware {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String BEANNAME = "bean.name";
    private static final String SPRING = "spring";

    //~ Instance fields ////////////////////////////////////////////////////////

    ApplicationContext applicationContext;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Condition getCondition(String type, Map args) throws WorkflowException {
        if (SPRING.equals(type)) {
            return (Condition) getApplicationContext().getBean((String) args.get(BEANNAME));
        }

        return super.getCondition(type, args);
    }

    public FunctionProvider getFunction(String type, Map args) throws WorkflowException {
        if (SPRING.equals(type)) {
            return (FunctionProvider) getApplicationContext().getBean((String) args.get(BEANNAME));
        }

        return super.getFunction(type, args);
    }

    public Register getRegister(String type, Map args) throws WorkflowException {
        if (SPRING.equals(type)) {
            return (Register) getApplicationContext().getBean((String) args.get(BEANNAME));
        }

        return super.getRegister(type, args);
    }

    public Validator getValidator(String type, Map args) throws WorkflowException {
        if (SPRING.equals(type)) {
            return (Validator) getApplicationContext().getBean((String) args.get(BEANNAME));
        }

        return super.getValidator(type, args);
    }

    private ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
