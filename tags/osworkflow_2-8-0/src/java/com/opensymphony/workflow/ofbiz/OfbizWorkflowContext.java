/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: Apr 29, 2002
 * Time: 11:20:06 PM
 */
package com.opensymphony.workflow.ofbiz;

import com.opensymphony.workflow.InternalWorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflowContext;

import org.ofbiz.core.entity.GenericTransactionException;
import org.ofbiz.core.entity.TransactionUtil;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class OfbizWorkflowContext extends BasicWorkflowContext {
    //~ Constructors ///////////////////////////////////////////////////////////

    public OfbizWorkflowContext(String caller) {
        super(caller);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setRollbackOnly() {
        try {
            TransactionUtil.setRollbackOnly();
        } catch (GenericTransactionException e) {
            throw new InternalWorkflowException(e);
        }
    }
}
