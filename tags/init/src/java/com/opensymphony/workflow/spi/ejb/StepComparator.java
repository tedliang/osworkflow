/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.ejb;

import com.opensymphony.workflow.spi.Step;

import java.util.Comparator;


/**
 * Utility class to order steps in a descending order.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class StepComparator implements Comparator {
    //~ Methods ////////////////////////////////////////////////////////////////

    public int compare(Object o1, Object o2) {
        Step step1 = (Step) o1;
        Step step2 = (Step) o2;

        if (step1.getId() > step2.getId()) {
            return -1;
        } else if (step1.getId() < step2.getId()) {
            return 1;
        } else {
            return 0;
        }
    }
}
