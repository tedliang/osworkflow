/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.workflow.query;

/**
 * Nested expressions are used when constructing a workflow query.
 * A nested expression consists of:
 * <li>one or more expressions: Each of them can again be a NestedExpression.
 * <li>operator: The operator used to evaluate the value of the nested expression
 *     from the specified sub expressions.
 *
 * @author Christine Zimmermann
 * @version $Revision: 1.1 $
 */
public class NestedExpression extends Expression
{
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
	
	/**
     * Create a NestedExpression that consists of multiple expressions.
     * @param expressions an array of expressions for this query.
     * @param operator {@link NestedExpression#AND} or {@link NestedExpression#OR}.
     */
    public NestedExpression(Expression[] expressions, int operator) {
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
     * @return {@link NestedExpression#AND} if all the expressions must match,
     * or {@link NestedExpression#OR} if only one must match.
     */
    public int getOperator() {
        return this.operator;
    }
	
	public boolean isNested() { return true;}

}

