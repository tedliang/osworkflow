/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.timer;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * DOCUMENT ME!
 *
 */
public class LocalWorkflowJob implements Job {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        long id = data.getLong("entryId");
        int triggerId = data.getInt("triggerId");
        String username = data.getString("username");
        Workflow wf = new BasicWorkflow(username);
        wf.setConfiguration((Configuration) data.get("configuration"));

        try {
            wf.executeTriggerFunction(id, triggerId);
        } catch (WorkflowException e) {
            //this cast is a fairly horrible hack, but it's more due to the fact that quartz is stupid enough to have wrapped exceptions
            //wrap Exception, instead of Throwable.
            throw new JobExecutionException("Error Executing local job", (e.getRootCause() != null) ? (Exception) e.getRootCause() : e, true);
        }
    }
}
