/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.timer;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;

import org.quartz.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1.1.1 $
 */
public class LocalWorkflowJob implements Job {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        long id = data.getLong("entryId");
        int triggerId = data.getInt("triggerId");
        String username = data.getString("username");
        Workflow wf = new BasicWorkflow(username);

        try {
            wf.executeTriggerFunction(id, triggerId);
        } catch (WorkflowException e) {
            throw new JobExecutionException("Error Executing local job", (e.getRootCause() != null) ? e.getRootCause() : e, true);
        }
    }
}
