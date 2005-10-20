/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.spi.hibernate;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.util.PropertySetDelegate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import java.util.Map;


/**
 * @author masini
 *
 * New Refactored Hibernate Store.
 * Look at @link NewHibernateFunctionalWorkflowTestCase for a use case.
 */
public class NewHibernateWorkflowStore extends AbstractHibernateWorkflowStore {
    //~ Instance fields ////////////////////////////////////////////////////////

    Session session;

    //~ Constructors ///////////////////////////////////////////////////////////

    public NewHibernateWorkflowStore() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // Now session management is delegated to user
    public void init(Map props) throws StoreException {
        session = (Session) props.get("session");

        setPropertySetDelegate((PropertySetDelegate) props.get("propertySetDelegate"));
    }

    protected Object execute(InternalCallback action) throws StoreException {
        try {
            return action.doInHibernate(session);
        } catch (HibernateException e) {
            throw new StoreException(e);
        }
    }
}
