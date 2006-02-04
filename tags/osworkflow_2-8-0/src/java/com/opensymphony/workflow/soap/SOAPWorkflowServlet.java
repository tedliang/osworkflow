/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.soap;

import com.opensymphony.workflow.Workflow;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BeanInvoker;
import org.codehaus.xfire.service.binding.BindingProvider;
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
        TypeMappingRegistry typeRegistry = new DefaultTypeMappingRegistry(true);
        BindingProvider binding = new AegisBindingProvider(typeRegistry);
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(), binding);
        Service service = factory.create(Workflow.class);
        service.setInvoker(new BeanInvoker(new XFireSOAPWorkflow()));
        registry.register(service);

        //        TypeMapping mapping = typeRegistry.getTypeMapping("http://workflow.opensymphony.com");
        //        BeanTypeInfo info = new BeanTypeInfo(FieldExpression.class, false);
        //        BeanType beanType = new BeanType(info);
        //
        //        beanType.setSchemaType(createQName(FieldExpression.class));
        //        mapping.register(Expression.class, createQName(Expression.class), new ObjectType(Collections.singleton(beanType), true));
    }

    //    protected QName createQName(Class javaType) {
    //        String clsName = javaType.getName();
    //
    //        String ns = NamespaceHelper.makeNamespaceFromClassName(clsName, "http");
    //        String localName = ServiceUtils.makeServiceNameFromClassName(javaType);
    //
    //        return new QName(ns, localName);
    //    }
}
