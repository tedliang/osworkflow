/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 2:05:40 PM
 */
package com.opensymphony.workflow.timer;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.quartz.impl.StdSchedulerFactory;


/**
 * To run:
 *
 */
public class QuartzRunner {
    //~ Methods ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        System.out.println("foo!");

        try {
            Scheduler s = new StdSchedulerFactory().getScheduler();
            s.start();
            Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown(s)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("foo!");
    }
}


class Shutdown implements Runnable {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Scheduler s;

    //~ Constructors ///////////////////////////////////////////////////////////

    public Shutdown(Scheduler s) {
        this.s = s;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void run() {
        try {
            System.out.println("Shutting down...");
            s.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
