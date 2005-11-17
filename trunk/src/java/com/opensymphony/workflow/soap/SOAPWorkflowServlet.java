/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.soap;

import com.opensymphony.workflow.Workflow;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.XFireServlet;

import javax.servlet.ServletException;


/**
 * @author Hani Suleiman
 *         Date: Nov 10, 2005
 *         Time: 1:58:35 PM
 */
public class SOAPWorkflowServlet extends XFireServlet {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void init() throws ServletException {
        super.init();

        ServiceRegistry registry = getXFire().getServiceRegistry();
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager());
        Service service = factory.create(Workflow.class);
        service.getBinding().setInvoker(new BeanInvoker(new XFireSOAPWorkflow()));
        registry.register(service);
    }
}
