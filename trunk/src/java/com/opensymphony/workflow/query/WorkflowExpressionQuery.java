/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.query;


/**
 * Workflow Query.
 * A workflow expression-based query is constructed by specifying a number of expressions in the query.
 * Currently queries can only have one operator act on them. Either the expressions are either evaluated
 * with an OR, whereby the first expression that passes results in inclusion of a result, or with an AND,
 * whereby all expressions must return true for a result to be included.
 *
 * @author Christine Zimmermann
 * @version $Revision: 1.5 $
 */
public class WorkflowExpressionQuery {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Expression expression = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a WorkflowExpressionQuery that consists of one expression.
     */
    public WorkflowExpressionQuery(Expression expression) {
        this.expression = expression;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Expression getExpression() {
        return expression;
    }
}
