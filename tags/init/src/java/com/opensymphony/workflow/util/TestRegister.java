/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 13, 2002
 * Time: 12:22:56 PM
 */
package com.opensymphony.workflow.util;

import com.opensymphony.workflow.Register;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.WorkflowEntry;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1.1.1 $
 */
public class TestRegister implements Register {
    //~ Methods ////////////////////////////////////////////////////////////////

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args) {
        return "****************************************";
    }
}
