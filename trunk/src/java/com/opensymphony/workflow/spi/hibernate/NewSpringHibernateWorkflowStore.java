/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;

import com.opensymphony.workflow.StoreException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateTemplate;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;


/**
 * @author masini
 *
 * New Refactored Spring Managed Hibernate Store.
 * Look at @link NewSpringHibernateFunctionalWorkflowTestCase for a use case.
 */
public class NewSpringHibernateWorkflowStore extends AbstractHibernateWorkflowStore {
    //~ Instance fields ////////////////////////////////////////////////////////

    private SessionFactory sessionFactory;

    //~ Constructors ///////////////////////////////////////////////////////////

    public NewSpringHibernateWorkflowStore() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public PropertySet getPropertySet(long entryId) throws StoreException {
        if (getPropertySetDelegate() != null) {
            return getPropertySetDelegate().getPropertySet(entryId);
        }

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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void init(Map props) throws StoreException {
    }

    protected Object execute(final InternalCallback action) throws StoreException {
        HibernateTemplate template = new HibernateTemplate(getSessionFactory());

        return template.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    try {
                        return action.doInHibernate(session);
                    } catch (StoreException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    }
}
