package com.opensymphony.workflow.designer.editor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.dialogs.AttributeDialog;
import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.Utils; 
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.AttributesTableModel;
import com.opensymphony.workflow.designer.model.RegistersTableModel;
import com.opensymphony.workflow.designer.model.TriggersTableModel;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.RegisterDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;


public class WorkflowEditor extends DetailPanel implements ActionListener
{
	private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};
	private JTextField name = new JTextField();
	
	private AttributesTableModel attributesModel = new AttributesTableModel();
	private JTable attributesTable;

	private RegistersTableModel registersModel = new RegistersTableModel();
	private JTable registersTable;

	private TriggersTableModel triggersModel = new TriggersTableModel();
	private JTable triggersTable;

	private BeanConnector connector = new BeanConnector();
	
  public WorkflowEditor()
  {
  }

  protected void initComponents()
  {
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";
		
		JTabbedPane tabbedPane = new JTabbedPane();
		CellConstraints cc = new CellConstraints();
    
    FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    
		/////////////////////////////
		// Tab1 (Info)
		/////////////////////////////
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);
    

		builderInfo.addLabel(ResourceManager.getString("name"), cc.xy(2, 4));
		connector.connect(name, "name");
		builderInfo.add(name, cc.xy(4, 4));

		tabbedPane.add(ResourceManager.getString("info"), panelInfo);
		
		///////////////////////////
		// Tab2 (meta attributes)
		///////////////////////////
		FormLayout layoutAttrib = new FormLayout(colLayout, rowLayout);
		JPanel panelAttrib = new JPanel();
		PanelBuilder builderAttrib = new PanelBuilder(panelAttrib, layoutAttrib);
		
		attributesTable = new JTable(attributesModel);
		attributesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		
		//attributesModel.setGraphModel(getModel());
		builderAttrib.add(UIFactory.createTablePanel(attributesTable), cc.xywh(2, 2, 3, 1));		// 2, 15, 3, 1
		builderAttrib.add(UIFactory.getAddRemovePropertiesBar(this, "attribute", BUTTONS), cc.xywh(2, 4, 3, 1));	// 2, 16, 3, 1
		
		tabbedPane.add(ResourceManager.getString("attributes"), panelAttrib);
		
		//////////////////////////
		// Tab3 (registers)
		//////////////////////////
		FormLayout layoutRegisters = new FormLayout(colLayout, rowLayout);
		JPanel panelRegisters = new JPanel();
		PanelBuilder builderRegisters = new PanelBuilder(panelRegisters, layoutRegisters);
		
		registersTable = new JTable(registersModel);
		registersTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		registersModel.setGraphModel(getModel());
		builderRegisters.add(UIFactory.createTablePanel(registersTable), cc.xywh(2, 2, 3, 1));		// 2, 20, 3, 1
		builderRegisters.add(UIFactory.getAddRemovePropertiesBar(this, "register", BUTTONS), cc.xywh(2, 4, 3, 1));	// 2, 21, 3, 1
		
		tabbedPane.add(ResourceManager.getString("registers"), panelRegisters);
		
		//////////////////////////
		// Tab4 (trigger-functions)
		//////////////////////////
		FormLayout layoutTriggers = new FormLayout(colLayout, rowLayout);
		JPanel panelTriggers = new JPanel();
		PanelBuilder builderTriggers = new PanelBuilder(panelTriggers, layoutTriggers);
		
		triggersTable = new JTable(triggersModel);
		triggersTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		triggersModel.setGraphModel(getModel());
		builderTriggers.add(UIFactory.createTablePanel(triggersTable), cc.xywh(2, 2, 3, 1));
		builderTriggers.add(UIFactory.getAddRemovePropertiesBar(this, "trigger", BUTTONS), cc.xywh(2, 4, 3, 1));
		
		tabbedPane.add(ResourceManager.getString("triggerfunctions"), panelTriggers);
		
		builder.add(tabbedPane, cc.xy(2,2));
    
  }
	
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand().toLowerCase();
		if(command.equals("attributeadd"))
		{
			attributeadd();
		}
		else if(command.equals("attributeremove"))
		{
			attributeremove();
		}
		else if(command.equals("attributeedit"))
		{
			attributemodify();
		}
		else if(command.equals("registeradd"))
		{
			registeradd();
		}
		else if(command.equals("registerremove"))
		{
			registerremove();
		}
		else if(command.equals("registeredit"))
		{
			registermodify();
		}
		else if(command.equals("triggeradd"))
		{
			triggeradd();
		}
		else if(command.equals("triggerremove"))
		{
			triggerremove();
		}
		else if(command.equals("triggeredit"))
		{
			triggermodify();
		}
	}

	public String getTitle()
	{
		return ResourceManager.getString("title.workflow", new Object[]{((WorkflowDescriptor)getDescriptor()).getName()});
	}

	protected void viewClosed()
	{
		if (registersTable.isEditing())
		{
			registersTable.getCellEditor().stopCellEditing();
		}
		else if (attributesTable.isEditing())
		{
			attributesTable.getCellEditor().stopCellEditing();
		}
		else if (triggersTable.isEditing())
		{
			triggersTable.getCellEditor().stopCellEditing();
		}
	}
	
	protected void updateView()
	{
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor)getDescriptor();
		
		attributesModel.setMap(workflowDescriptor.getMetaAttributes());
		attributesTable.getSelectionModel().clearSelection();
			
		registersModel.setList(workflowDescriptor.getRegisters());
		registersTable.getSelectionModel().clearSelection(); 
		
		triggersModel.setMap(workflowDescriptor.getTriggerFunctions());
		triggersTable.getSelectionModel().clearSelection(); 
		
		connector.setSource(workflowDescriptor);
		connector.setPanel(this);
	}
	
	private void attributeadd()
	{
		AttributeDialog dlg = new AttributeDialog(WorkflowDesigner.INSTANCE, "", "", true);
		if (dlg.ask(WorkflowDesigner.INSTANCE))
		{
			String sKey = dlg.keyField.getText();
			String sValue = dlg.valueField.getText();
			if (sKey.length()>0)
			{
				attributesModel.add(sKey, sValue);
			}
		}
	}

	private void attributeremove()
	{
		int[] rows = attributesTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			String sKey = (String)attributesModel.getValueAt(rows[i], 0);
			attributesModel.remove(sKey);
		}		
	}

	private void attributemodify()
	{
		int[] rows = attributesTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			attributemodify(rows[i]);
		}
	}

	private void attributemodify(int selected)
	{
		String sKey = (String)attributesModel.getValueAt(selected, 0);
		String sValue = (String)attributesModel.getValueAt(selected, 1);
		if ((sKey!=null)&&(sKey.length()>0))
		{
			AttributeDialog dlg = new AttributeDialog(WorkflowDesigner.INSTANCE, sKey, sValue, false);
			if (dlg.ask(WorkflowDesigner.INSTANCE))
			{
				sValue = dlg.valueField.getText();
				attributesModel.add(sKey, sValue);
			}
		}
	}

	private void registeradd()
	{
		RegisterEditor editor = new RegisterEditor(getDescriptor());
		editor.setModel(getModel());
		RegisterDescriptor reg = editor.add();
		if (reg!=null)
		{
			registersModel.add(reg);
		}
	}

	private void registerremove()
	{
		int[] rows = registersTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			registersModel.remove(rows[i]);
		}
	}

	private void registermodify()
	{
		int[] rows = registersTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			registermodify(rows[i]);
		}
	}

	private void registermodify(int selected)
	{
		RegisterDescriptor reg = (RegisterDescriptor)registersModel.get(selected);

		RegisterEditor editor = new RegisterEditor(getDescriptor());
		editor.setModel(getModel());
		editor.modify(reg);
		registersModel.fireTableDataChanged();
	}

	private void triggeradd()
	{
		TriggerFunctionEditor editor = new TriggerFunctionEditor((WorkflowDescriptor)getDescriptor());
		editor.setModel(getModel());
		FunctionDescriptor func = editor.add();
		if (func!=null)
		{
			//Utils.checkId(getModel().getContext(), func);		
			func.setId(Utils.getNextId(getModel().getContext()));
			Utils.checkId(getModel().getContext(), func);
			triggersModel.add(new Integer(func.getId()), func);
		}
	}

	private void triggerremove()
	{
		int[] rows = triggersTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			Integer idKey = new Integer(Integer.parseInt((String)triggersModel.getValueAt(rows[i], 0)));
			triggersModel.remove(idKey);
		}	
	}

	private void triggermodify()
	{
		int[] rows = triggersTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			triggermodify(rows[i]);
		}		
	}

	private void triggermodify(int selected)
	{
		Integer idKey = new Integer(Integer.parseInt((String)triggersModel.getValueAt(selected, 0)));
		FunctionDescriptor func = (FunctionDescriptor)triggersModel.get(idKey);
		if (func!=null)
		{
			TriggerFunctionEditor editor = new TriggerFunctionEditor((WorkflowDescriptor)getDescriptor());
			editor.setModel(getModel());
			editor.modify(func);
			triggersModel.fireTableDataChanged();
		}		
	}
}
