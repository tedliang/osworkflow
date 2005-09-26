/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;
import com.opensymphony.workflow.StoreException;

/**
 * @author masini
 *
 * New Refactored Hibernate Store.
 * Look at @link NewHibernateFunctionalWorkflowTestCase for a use case.
 */
public class NewHibernateWorkflowStore extends AbstractHibernateWorkflowStore {

    Session session;
    
    public NewHibernateWorkflowStore() {
        super();
    }

    protected Object execute(InternalCallback action) throws StoreException {
        try {
            return action.doInHibernate(session);            
        } catch (HibernateException e) {
            throw new StoreException(e);
        }
    }

    public PropertySet getPropertySet(long entryId) throws StoreException {
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(session.getSessionFactory());

        args.put("configurationProvider", configurationProvider);

        return PropertySetManager.getInstance("hibernate", args);
    }

    // Now session management is delegated to user
    public void init(Map props) throws StoreException {
        session = (Session) props.get("session");
    }

}
