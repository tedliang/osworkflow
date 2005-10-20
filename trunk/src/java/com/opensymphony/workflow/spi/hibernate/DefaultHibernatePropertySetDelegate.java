/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 20-ott-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;

import com.opensymphony.workflow.util.PropertySetDelegate;

import net.sf.hibernate.SessionFactory;

import java.util.HashMap;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class DefaultHibernatePropertySetDelegate implements PropertySetDelegate {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionFactory sessionFactory;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DefaultHibernatePropertySetDelegate() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(getSessionFactory());

        args.put("configurationProvider", configurationProvider);

        return PropertySetManager.getInstance("hibernate", args);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
