package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.loader.*;

/**
 * @author jackflit
 *         Date: 2003-11-18
 */
public class InitialActionEditor extends DetailPanel implements ActionListener
{
	private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};

	private JTextField id = UIFactory.createReadOnlyTextField(12);
	private JTextField name = new JTextField(12);
	private JTextField view = new JTextField(12);
	private JComboBox restrict = new JComboBox(new String[]{"AND", "OR"});

	private ConditionsTableModel conditionsModel = new ConditionsTableModel();
	private JTable conditionsTable;

	private FunctionsTableModel preModel = new FunctionsTableModel();
	private JTable pre;

	private FunctionsTableModel postModel = new FunctionsTableModel();
	private JTable post;

	private BeanConnector connector = new BeanConnector();
	private ActionDescriptor descriptor;

	public InitialActionEditor()
	{
	}

	protected void initComponents()
	{
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";
		/*
		FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, " + // 1 Info
		  "pref, 2dlu, " + // 3 ID
		  "pref, 2dlu, " + // 5 Name
		  "pref, 2dlu, " + // 7 View
		  //				"pref, 2dlu, " + // 9 auto
		  "pref, 2dlu, " + // 9 permission
		  "pref, 2dlu, " + // 11 type
		  "50dlu:grow, " + // 13 table
		  "pref, 2dlu, " + // 14 buttons
		  "pref, 2dlu, " + // 16 pre-function
		  "50dlu:grow, " + // 18 table
		  "pref, 2dlu, " + // 19 buttons
		  "pref, 2dlu, " + // 21 post-function
		  "50dlu:grow, " + // 23 table
		  "pref, 2dlu");	 // 24 buttons
		*/

		JTabbedPane tabbedPane = new JTabbedPane();
		FormLayout layout = new FormLayout("2dlu, pref:grow, 2dlu", "2dlu, pref, 2dlu");
		PanelBuilder builder = new PanelBuilder(this, layout);
		CellConstraints cc = new CellConstraints();

		////////////////////////////////////
		// Tab1 (info)
		////////////////////////////////////
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);
		//builder.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 3, 1));

		builderInfo.addLabel(ResourceManager.getString("id"), cc.xy(2, 2));
		connector.connect(id, "id");
		builderInfo.add(id, cc.xy(4, 2));

		builderInfo.addLabel(ResourceManager.getString("name"), cc.xy(2, 4));
		connector.connect(name, "name");
		builderInfo.add(name, cc.xy(4, 4));

		builderInfo.addLabel(ResourceManager.getString("view"), cc.xy(2, 6));
		connector.connect(view, "view");
		builderInfo.add(view, cc.xy(4, 6));

		tabbedPane.add(ResourceManager.getString("info"), panelInfo);

		// Tab2 (permissions)
		FormLayout layoutPerm = new FormLayout(colLayout, rowLayout);
		JPanel panelPerm = new JPanel();
		PanelBuilder builderPerm = new PanelBuilder(panelPerm, layoutPerm);
		//builderInfo.addSeparator(ResourceManager.getString("permissions"), cc.xywh(2, 8, 3, 1));

		builderPerm.addLabel(ResourceManager.getString("type"), cc.xy(2, 2));
		connector.connect(restrict, "restriction.conditionType");
		builderPerm.add(restrict, cc.xy(4, 2));

		conditionsTable = new JTable(conditionsModel);
    conditionsModel.setType(ConditionsTableModel.PERMISSION);
    conditionsModel.setGraphModel(getModel());
		builderPerm.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 4, 3, 1));
		builderPerm.add(UIFactory.getAddRemovePropertiesBar(this, "permission", BUTTONS), cc.xywh(2, 6, 3, 1));

		tabbedPane.add(ResourceManager.getString("permissions"), panelPerm);

		// Tab3 (pre-functions)
		FormLayout layoutPrefunc = new FormLayout(colLayout, rowLayout);
		JPanel panelPrefunc = new JPanel();
		PanelBuilder builderPrefunc = new PanelBuilder(panelPrefunc, layoutPrefunc);
		//builder.addSeparator(ResourceManager.getString("prefunctions"), cc.xywh(2, 16, 3, 1));

		pre = new JTable(preModel);
    preModel.setGraphModel(getModel());
		builderPrefunc.add(UIFactory.createTablePanel(pre), cc.xywh(2, 2, 3, 1));
		builderPrefunc.add(UIFactory.getAddRemovePropertiesBar(this, "pre", BUTTONS), cc.xywh(2, 4, 3, 1));

		tabbedPane.add(ResourceManager.getString("prefunctions"), panelPrefunc);

		// Tab4 (post-functions)
		FormLayout layoutPostfunc = new FormLayout(colLayout, rowLayout);
		JPanel panelPostfunc = new JPanel();
		PanelBuilder builderPostfunc = new PanelBuilder(panelPostfunc, layoutPostfunc);
		//builder.addSeparator(ResourceManager.getString("postfunctions"), cc.xywh(2, 21, 3, 1));

		post = new JTable(postModel);
    postModel.setGraphModel(getModel());
		builderPostfunc.add(UIFactory.createTablePanel(post), cc.xywh(2, 2, 3, 1));
		builderPostfunc.add(UIFactory.getAddRemovePropertiesBar(this, "post", BUTTONS), cc.xywh(2, 4, 3, 1));

		tabbedPane.add(ResourceManager.getString("postfunctions"), panelPostfunc);

    builder.add(tabbedPane, cc.xy(2,2));
	}

	public String getTitle()
	{
		return ResourceManager.getString("title.start");
	}

	protected void viewClosed()
	{
		if(conditionsTable.isEditing())
		{
			conditionsTable.getCellEditor().stopCellEditing();
		}

		if(pre.isEditing())
		{
			pre.getCellEditor().stopCellEditing();
		}

		if(post.isEditing())
		{
			post.getCellEditor().stopCellEditing();
		}

	}

	protected void updateView()
	{
		InitialActionCell cell = (InitialActionCell)getCell();
		descriptor = cell.getActionDescriptor();
		RestrictionDescriptor restriction = descriptor.getRestriction();
		if(restriction == null)
		{
      restriction = new RestrictionDescriptor();
      restriction.setParent(descriptor);
      ConditionsDescriptor conditions = DescriptorFactory.getFactory().createConditionsDescriptor();
      restriction.setConditionsDescriptor(conditions);
      conditions.setParent(restriction);
      conditions.setType((String)restrict.getSelectedItem());
			descriptor.setRestriction(restriction);
		}
		conditionsModel.setList(restriction.getConditionsDescriptor().getConditions());
		conditionsTable.getSelectionModel().clearSelection();

		preModel.setList(descriptor.getPreFunctions());
		pre.getSelectionModel().clearSelection();

		postModel.setList(descriptor.getPostFunctions());
		post.getSelectionModel().clearSelection();

		connector.setSource(descriptor);
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if(command.equals("permissionadd"))
		{
			add();
		}
		else if(command.equals("permissionremove"))
		{
			remove();
		}
		else if(command.equals("permissionedit"))
		{
			modify();
		}
		else if(command.equals("preadd"))
		{
			preadd();
		}
		else if(command.equals("preremove"))
		{
			preremove();
		}
		else if(command.equals("preedit"))
		{
			premodify();
		}
		else if(command.equals("postadd"))
		{
			postadd();
		}
		else if(command.equals("postremove"))
		{
			postremove();
		}
		else if(command.equals("postedit"))
		{
			postmodify();
		}
	}

	private void add()
	{
		StartPermissionEditor editor = new StartPermissionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		ConditionDescriptor cond = editor.add();
		if(cond != null)
		{
			conditionsModel.add(cond);
		}
	}

	private void remove()
	{
		int[] rows = conditionsTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			conditionsModel.remove(rows[i]);
		}
	}

	private void modify()
	{
		int[] rows = conditionsTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			modify(rows[i]);
		}

	}

	private void modify(int selected)
	{
		ConditionDescriptor cond = (ConditionDescriptor)conditionsModel.get(selected);

		StartPermissionEditor editor = new StartPermissionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		editor.modify(cond);

		conditionsModel.fireTableDataChanged();
	}

	private void preadd()
	{
		StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		FunctionDescriptor func = editor.add();
		if(func != null)
		{
			preModel.add(func);
		}
	}

	private void preremove()
	{
		int[] rows = pre.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			preModel.remove(rows[i]);
		}
	}

	private void premodify()
	{
		int[] rows = pre.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			premodify(rows[i]);
		}

	}

	private void premodify(int selected)
	{
		FunctionDescriptor func = (FunctionDescriptor)preModel.get(selected);

		StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		editor.modify(func);

		preModel.fireTableDataChanged();
	}

	private void postadd()
	{
		StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		FunctionDescriptor func = editor.add();
		if(func != null)
		{
			postModel.add(func);
		}
	}

	private void postremove()
	{
		int[] rows = post.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			postModel.remove(rows[i]);
		}
	}

	private void postmodify()
	{
		int[] rows = post.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			postmodify(rows[i]);
		}

	}

	private void postmodify(int selected)
	{
		FunctionDescriptor func = (FunctionDescriptor)postModel.get(selected);

		StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
		editor.setModel(getModel());
		editor.modify(func);

		postModel.fireTableDataChanged();
	}

}
