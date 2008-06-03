/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.workflow.spi.hibernate3;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.hibernate3.DefaultHibernateConfigurationProvider;
import com.opensymphony.module.propertyset.hibernate3.HibernatePropertySet;

import com.opensymphony.workflow.util.PropertySetDelegate;

import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENTME
 * @author $author$
 * @version $Revision: 1.2 $
 *
 */
public class DefaultHibernatePropertySetDelegate implements PropertySetDelegate {
    //~ Instance fields ////////////////////////////////////////////////////////

    // ~ Instance fields
    // ////////////////////////////////////////////////////////
    private SessionFactory sessionFactory;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ~ Constructors
    // ///////////////////////////////////////////////////////////
    public DefaultHibernatePropertySetDelegate() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // ~ Methods
    // ////////////////////////////////////////////////////////////////
    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(getSessionFactory());

        args.put("configurationProvider", configurationProvider);

        Map config = new HashMap();
        PropertySet properties = new HibernatePropertySet();
        properties.init(config, args);

        return properties;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
