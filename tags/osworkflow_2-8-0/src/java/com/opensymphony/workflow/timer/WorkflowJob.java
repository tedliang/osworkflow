/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 4:10:43 PM
 */
package com.opensymphony.workflow.timer;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;

import electric.registry.Registry;
import electric.registry.RegistryException;

import electric.util.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * DOCUMENT ME!
 *
 */
public class WorkflowJob implements Job {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(WorkflowJob.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
            long id = data.getLong("entryId");
            int triggerId = data.getInt("triggerId");
            String username = data.getString("username");
            String password = data.getString("password");
            Context context = new Context();
            context.setProperty("authUser", username);
            context.setProperty("authPassword", password);

            Workflow wf = (Workflow) Registry.bind(System.getProperty("osworkflow.soap.url", "http://localhost/example/glue/oswf.wsdl"), Workflow.class, context);
            wf.executeTriggerFunction(id, triggerId);
        } catch (RegistryException e) {
            log.error("Error using GLUE to make remote call", e);
            throw new JobExecutionException("Error using GLUE to make remote call", e, true);
        } catch (WorkflowException e) {
            log.error("Error Executing trigger", e);

            //this cast is a fairly horrible hack, but it's more due to the fact that quartz is stupid enough to have wrapped exceptions
            //wrap Exception, instead of Throwable.
            throw new JobExecutionException("Error Executing trigger", (e.getRootCause() != null) ? (Exception) e.getRootCause() : e, true);
        }
    }
}
