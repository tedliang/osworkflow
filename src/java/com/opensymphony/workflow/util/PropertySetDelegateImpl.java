/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.util;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.HashMap;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class PropertySetDelegateImpl implements PropertySetDelegate {
    //~ Methods ////////////////////////////////////////////////////////////////

    public PropertySet getPropertySet(long entryId) {
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

        //        DefaultHibernateConfigurationProvider configurationProvider = new DefaultHibernateConfigurationProvider();
        //        //configurationProvider.setSessionFactory(sessionFactory);
        //
        //        args.put("configurationProvider", configurationProvider);
        return PropertySetManager.getInstance("gioia", args);
    }
}
