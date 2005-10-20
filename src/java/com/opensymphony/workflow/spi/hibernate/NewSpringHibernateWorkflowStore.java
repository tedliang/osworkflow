/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.StoreException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateTemplate;

import java.sql.SQLException;

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
