/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.WorkflowEntry;


/*
 * @author Hani Suleiman
 * @version $Revision: 1.2 $
 * Date: Apr 6, 2002
 * Time: 11:48:14 PM
 */
public interface WorkflowLocalListener {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void stateChanged(WorkflowEntry entry) throws WorkflowException;
}
