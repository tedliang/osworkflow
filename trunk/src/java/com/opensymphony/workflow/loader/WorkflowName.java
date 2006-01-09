/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 30-nov-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.workflow.loader;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class WorkflowName {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String workflowName;
    private WorkflowDescriptor workflowDescriptor;

    //~ Constructors ///////////////////////////////////////////////////////////

    public WorkflowName() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setWorkflowDescriptor(WorkflowDescriptor workflowDescriptor) {
        this.workflowDescriptor = workflowDescriptor;
    }

    public WorkflowDescriptor getWorkflowDescriptor() {
        return workflowDescriptor;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }
}
