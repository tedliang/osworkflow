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
 * @version $Revision: 1.3 $
 */
public class WorkflowExpressionQuery {
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Constant to specify that all the expressions specified must evaluate to true for
     * an item to be included in the search results.
     */
    public final static int AND = 6;

    /**
     * Constant to specify that at least one of the expressions specified must evaluate to true
     * for an item to be included in the search results.
     */
    public final static int OR = 7;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Expression[] expressions = null;
    private int operator = AND;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a WorkflowExpressionQuery that consists of one expression.
     */
    public WorkflowExpressionQuery(Expression expression) {
        expressions = new Expression[] {expression};
    }

    /**
     * Create a WorkflowExpressionQuery that consists of multiple expressions.
     * @param expressions an array of expressions for this query.
     * @param operator {@link WorkflowExpressionQuery#AND} or {@link WorkflowExpressionQuery#OR}.
     */
    public WorkflowExpressionQuery(Expression[] expressions, int operator) {
        this.expressions = expressions;
        this.operator = operator;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Expression getExpression(int index) {
        return expressions[index];
    }

    /**
     * Get the number of expressions in this query.
     */
    public int getExpressionCount() {
        return expressions.length;
    }

    /**
     * @return {@link WorkflowExpressionQuery#AND} if all the expressions must match,
     * or {@link WorkflowExpressionQuery#OR} if only one must match.
     */
    public int getOperator() {
        return this.operator;
    }
}
