/*
 * Created on 2003-11-24
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.opensymphony.workflow.designer.editor;

import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConfigFunctionDescriptor;

/**
 * @author baab
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResultFunctionEditor extends FunctionEditor {
	
	protected ResultEdge edge;
	/**
	 * @param cell
	 */
	public ResultFunctionEditor(ResultEdge cell) {
		super(null);
		edge = cell;
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.editor.FunctionEditor#getParent()
	 */
	protected AbstractDescriptor getParent() {
		return edge.getDescriptor();
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.editor.FunctionEditor#getNewFunction(java.lang.String)
	 */
	protected ConfigFunctionDescriptor getNewFunction(String selection) {
		return new ConfigFunctionDescriptor(
						WorkflowDesigner.config.getPrefunction(selection));	
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.designer.editor.FunctionEditor#getSelection()
	 */
	protected String getSelection() {
		return DialogUtils.getUserSelection(
			WorkflowDesigner.config.getPreNames(),
			"请选择动作",
			"选择动作",
			null);
	}

}
