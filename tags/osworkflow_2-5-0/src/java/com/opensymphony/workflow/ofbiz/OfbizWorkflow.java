/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created by IntelliJ IDEA.
 * User: plightbo
 * Date: Apr 29, 2002
 * Time: 11:22:48 PM
 */
package com.opensymphony.workflow.ofbiz;

import com.opensymphony.workflow.*;

import org.ofbiz.core.entity.GenericTransactionException;
import org.ofbiz.core.entity.TransactionUtil;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1.1.1 $
 */
public class OfbizWorkflow extends AbstractWorkflow {
    //~ Constructors ///////////////////////////////////////////////////////////

    public OfbizWorkflow(String caller) throws FactoryException {
        super.context = new OfbizWorkflowContext(caller);
        loadConfig();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void doAction(long id, int actionId, Map inputs) throws WorkflowException {
        try {
            TransactionUtil.begin();
        } catch (GenericTransactionException e) {
            throw new WorkflowException(e);
        }

        super.doAction(id, actionId, inputs);

        try {
            TransactionUtil.commit();
        } catch (GenericTransactionException e) {
            throw new WorkflowException(e);
        }
    }

    public long initialize(String workflowName, int initialState, Map inputs) throws WorkflowException {
        try {
            TransactionUtil.begin();
        } catch (GenericTransactionException e) {
            throw new WorkflowException(e);
        }

        long id = super.initialize(workflowName, initialState, inputs);

        try {
            TransactionUtil.commit();

            return id;
        } catch (GenericTransactionException e) {
            throw new WorkflowException(e);
        }
    }
}
