/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 4:05:53 PM
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.timer.LocalWorkflowJob;
import com.opensymphony.workflow.timer.WorkflowJob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.quartz.*;

import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.Map;


/**
 * Schedules a job in the Quartz job scheduler to be executed one or more times in the future.
 * The following arguments are required:
 *
 * <ul>
 *  <li> triggerId - the id of the trigger function defined in the XML workflow
 *  <li> jobName - the name to be given to the job
 *  <li> triggerName - the name to be given to the trigger
 *  <li> groupName - the group given to both the job and the trigger
 * </ul>
 *
 * The following arguments are optional:
 * <ul>
 *  <li> username - the system account's username that will execute the function in the future.
 * If this is not specified value from WorkflowContext.getCaller() is used
 *  <li> password - the system account's password
 *  <li> local - if set to the true, a LocalWorkflowJob is used, bypassing the need for SOAP support.
 * Will be ignored if "workflowClass" is specified.
 * <li> jobClass - the class implementing 'Job' to run, defaults to WorkflowJob. If not specified,
 * defaults to either a WorkflowJob or a LocalWorkflowJob if "local" is set to true.
 *  <li>schedulerName - the name of an existing scheduler to use</li>
 *  <li>schdulerStart - if "true", start the scheduler if it hasn't been started already</li>
 *  <li>txHack - set this to true if you are getting lockups while running with transactions (defaults to false)</li>
 *  <li>addArguments - set to true if you want the contents of the arguments Map to be available in the JobDataMap of the Job</li>
 * </ul>
 *
 * If you are using a cron trigger, the following is required:
 * <ul>
 *  <li> cronExpression - the Cron expression
 * </ul>
 *
 * If you are using a simple trigger, the follow are all optional:
 * <ul>
 *  <li> startOffset - the offset, in milliseconds, from the current time. (default is 0)
 *  <li> endOffset - the offset, in milliseconds, from the current time. (default is infinity)
 *  <li> repeat - the repeat count (default is 0). Can also be REPEAT_INDEFINITELY
 *  <li> repeatDelay - the time delay, in milliseconds, between repeats (default is 0)
 * </ul>
 *
 * @author <a href="mike.g.slack@usahq.unitedspacealliance.com ">Michael G. Slack</a>
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class ScheduleJob implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(ScheduleJob.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) {
        try {
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
            WorkflowContext context = (WorkflowContext) transientVars.get("context");

            log.info("Starting to schdule job for WF #" + entry.getId());

            int triggerId = TextUtils.parseInt((String) args.get("triggerId"));
            String jobName = (String) args.get("jobName");
            String triggerName = (String) args.get("triggerName");
            String groupName = (String) args.get("groupName");

            String username = (String) args.get("username");
            String password = (String) args.get("password");

            boolean txHack = TextUtils.parseBoolean((String) args.get("txHack"));

            if (username == null) {
                username = context.getCaller();
            }

            String cronExpression = (String) args.get("cronExpression");

            jobName = jobName + ":" + entry.getId();
            triggerName = triggerName + ":" + entry.getId();
            groupName = groupName + ":" + entry.getId();

            String schedulerName = (String) args.get("schedulerName");
            Scheduler s;

            SchedulerFactory factory = new StdSchedulerFactory();

            if ((schedulerName == null) || "".equals(schedulerName.trim())) {
                s = factory.getScheduler();
            } else {
                s = factory.getScheduler(schedulerName);
            }

            if (TextUtils.parseBoolean((String) args.get("schedulerStart"))) {
                log.info("Starting Quartz Job Scheduler");
                s.start();
            }

            Class jobClass;
            String jobClassArg = (String) args.get("jobClass");

            if (jobClassArg != null) {
                jobClass = Class.forName(jobClassArg);
            } else if (TextUtils.parseBoolean((String) args.get("local"))) {
                jobClass = LocalWorkflowJob.class;
            } else {
                jobClass = WorkflowJob.class;
            }

            JobDetail jobDetail = new JobDetail(jobName, groupName, jobClass);
            Trigger trigger;

            if (cronExpression == null) {
                long now = System.currentTimeMillis();

                // get start date - default is now
                Date startDate = null;

                try {
                    String start = (String) args.get("startOffset");

                    if (s != null) {
                        startDate = new Date(now + Long.parseLong(start));
                    }
                } catch (NumberFormatException e) {
                }

                if (startDate == null) {
                    startDate = new Date(now);
                }

                // get end date - default is null
                Date endDate = null;

                try {
                    String end = (String) args.get("endOffset");

                    if (s != null) {
                        startDate = new Date(now + Long.parseLong(end));
                    }
                } catch (NumberFormatException e) {
                }

                // get the repeat amount - default is 0
                int repeat = 0;

                try {
                    String r = (String) args.get("repeat");

                    if (r != null) {
                        if (r.equalsIgnoreCase("REPEAT_INDEFINITELY")) {
                            repeat = SimpleTrigger.REPEAT_INDEFINITELY;
                        } else {
                            repeat = TextUtils.parseInt(r);
                        }
                    }
                } catch (NumberFormatException e) {
                }

                // get repeat delay - default is 0
                long delay = 0;

                try {
                    String rd = (String) args.get("repeatDelay");

                    if (rd != null) {
                        delay = Long.parseLong(rd);
                    }
                } catch (NumberFormatException e) {
                }

                trigger = new SimpleTrigger(triggerName, groupName, jobName, groupName, startDate, endDate, repeat, delay);
            } else {
                trigger = new CronTrigger(triggerName, groupName, jobName, groupName, cronExpression);
            }

            JobDataMap dataMap = new JobDataMap();

            if (TextUtils.parseBoolean((String) args.get("addArguments"))) {
                dataMap.putAll(args);
            }

            dataMap.put("triggerId", triggerId);
            dataMap.put("entryId", entry.getId());
            dataMap.put("username", username);
            dataMap.put("password", password);

            if (TextUtils.parseBoolean((String) args.get("local"))) {
                dataMap.put("configuration", transientVars.get("configuration"));
            }

            jobDetail.setJobDataMap(dataMap);
            jobDetail.setDurability(true);

            trigger.setJobName(jobDetail.getName());
            trigger.setJobGroup(jobDetail.getGroup());

            if (txHack && !s.isPaused() && !s.isShutdown()) {
                s.pause();

                try {
                    s.addJob(jobDetail, true);
                    s.scheduleJob(trigger);
                } catch (SchedulerException e) {
                    throw e;
                } finally {
                    s.start();
                }
            } else {
                s.addJob(jobDetail, true);
                s.scheduleJob(trigger);
            }

            log.info("Job scheduled");
        } catch (Exception e) {
            log.error("Error scheduling job", e);
        }
    }
}
