/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: May 22, 2002
 * Time: 3:29:26 PM
 */
package com.opensymphony.workflow.soap;

import com.opensymphony.workflow.Workflow;

import electric.registry.Registry;

import electric.util.Context;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class TestClient {
    //~ Methods ////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        Context context = new Context();
        context.setProperty("authUser", "test");
        context.setProperty("authPassword", "test");

        Workflow wf = (Workflow) Registry.bind("http://localhost/example/glue/oswf.wsdl", Workflow.class, context);

        //...
    }
}
