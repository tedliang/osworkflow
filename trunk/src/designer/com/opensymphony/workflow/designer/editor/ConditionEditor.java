package com.opensymphony.workflow.designer.editor;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.designer.WorkflowCell;
import com.opensymphony.workflow.designer.spi.DefaultConditionPluginImpl;
import com.opensymphony.workflow.designer.spi.IConditionPlugin;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConfigConditionDescriptor;

/**
 * @author baab
 */
public abstract class ConditionEditor {
	protected WorkflowCell cell;

	public ConditionEditor(WorkflowCell cell){
		this.cell = cell;
	}

	public ConditionDescriptor add(){
		String selection = getSelection();
		if(selection == null){
			return null;
		}

		ConfigConditionDescriptor condition = getNewCondition(selection);

		condition = editCondition(condition);

		if(condition != null){
			ConditionDescriptor cond = new ConditionDescriptor();
//			cond.setId(0);
//			cond.setNegate(false);
			cond.setParent(getParent());
			cond.setType(condition.getType());
			cond.setName(condition.getName());
			cond.getArgs().putAll(condition.getArgs());

			return cond;
		}
		else{
			return null;
		}

	}

	public void modify(ConditionDescriptor cond){
		ConfigConditionDescriptor condition = null;

		if(cond.getName() != null){
			condition = getNewCondition(cond.getName());
		}
		else{
			condition = new ConfigConditionDescriptor();
			condition.setType(cond.getType());
		}

		condition.getArgs().putAll(cond.getArgs());


		condition = editCondition(condition);

		if(condition != null){
			cond.getArgs().putAll(condition.getArgs());
		}

	}


	private ConfigConditionDescriptor editCondition(ConfigConditionDescriptor config){
		// get plugin
		String clazz = config.getPlugin();
		if(clazz == null || clazz.length() == 0){
			clazz = "com.opensymphony.workflow.designer.spi.DefaultConditionPluginImpl";
		}
		IConditionPlugin condImpl = null;
		try {
			condImpl = (IConditionPlugin)Class.forName(clazz).newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
			condImpl = new DefaultConditionPluginImpl();
		}

		condImpl.setCondition(config);

		// put the parameter
		Map args = new HashMap();
		args.put("cell", cell);

		if(!condImpl.editCondition(args)){
			// cancel
			return null;
		}
		config = condImpl.getCondition();
		return config;
	}
	abstract protected AbstractDescriptor getParent();

	abstract protected ConfigConditionDescriptor getNewCondition(String selection);

	abstract protected String getSelection();

}
