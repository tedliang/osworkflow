/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import net.sf.hibernate.dialect.MckoiDialect;


/**
 * @author Hani Suleiman
 *         Date: Jul 11, 2005
 *         Time: 1:52:46 AM
 */
public class PatchedMckoiDialect extends MckoiDialect {
    //~ Methods ////////////////////////////////////////////////////////////////

    public String getDropSequenceString(String string) {
        return "drop table " + string + " if exists";
    }
}
