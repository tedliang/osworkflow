/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 23, 2002
 * Time: 2:33:59 AM
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;


/**
 * Unschedules a job that was scheduled previously. Accepts the following arguments:
 *
 * <ul>
 *  <li>triggerName - the name of the trigger previously scheduled</li>
 *  <li>groupName - the name of the group previously scheduled</li>
 *  <li>schedulerName - the name of an existing scheduler to use (optional)</li>
 *  <li>txHack - set this to true if you are getting lockups while running with transactions (optional, defaults to false)</li>
 * </ul>
 *
 * @author <a href="mike.g.slack@usahq.unitedspacealliance.com ">Michael G. Slack</a>
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @version $Revision: 1.3 $
 */
public class UnscheduleJob implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(UnscheduleJob.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) {
        try {
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

            log.info("Starting to unschedule job for WF #" + entry.getId());

            String schedulerName = (String) args.get("schedulerName");
            Scheduler s = null;

            StdSchedulerFactory factory = new StdSchedulerFactory();

            if ((schedulerName == null) || ("".equals(schedulerName.trim()))) {
                s = factory.getScheduler();
            } else {
                s = factory.getScheduler(schedulerName);
            }

            boolean txHack = TextUtils.parseBoolean((String) args.get("txHack"));

            String triggerName = (String) args.get("triggerName");
            String groupName = (String) args.get("groupName");
            triggerName = triggerName + ":" + entry.getId();
            groupName = groupName + ":" + entry.getId();

            if (txHack && !s.isPaused() && !s.isShutdown()) {
                s.pause();

                try {
                    s.unscheduleJob(triggerName, groupName);
                } catch (SchedulerException e) {
                    throw e;
                } finally {
                    s.start();
                }
            } else {
                s.unscheduleJob(triggerName, groupName);
            }

            log.info("Job unscheduled");
        } catch (Exception e) {
            log.error("Could not unschedule job", e);
        }
    }
}
