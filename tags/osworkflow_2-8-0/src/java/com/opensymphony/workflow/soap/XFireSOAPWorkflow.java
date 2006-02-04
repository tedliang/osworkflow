/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.soap;

import org.codehaus.xfire.transport.http.XFireServletController;


/**
 * @author Hani Suleiman
 *         Date: Nov 10, 2005
 *         Time: 1:51:55 PM
 */
public class XFireSOAPWorkflow extends BasicSOAPWorkflow {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected String getRemoteUser() {
        String user = XFireServletController.getRequest().getRemoteUser();

        return (user == null) ? "test" : user;
    }
}
