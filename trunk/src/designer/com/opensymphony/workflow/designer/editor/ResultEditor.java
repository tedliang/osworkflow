package com.opensymphony.workflow.designer.editor;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.designer.model.ValidatorsTableModel;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.loader.*;

public class ResultEditor extends DetailPanel implements ActionListener
{
  private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};

  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private JTextField displayName = new JTextField(12);
  private JTextField owner = new JTextField(12);
  private JComboBox status = new JComboBox();
  private JComboBox oldStatus = new JComboBox();
  private FunctionsTableModel preFunctionsModel = new FunctionsTableModel();
  private JTable preFunctionsTable;
  private FunctionsTableModel postFunctionsModel = new FunctionsTableModel();
  private JTable postFunctionsTable;
	private ValidatorsTableModel validatorsModel = new ValidatorsTableModel();
	private JTable validatorsTable;	
  private JComboBox type = new JComboBox(new String[]{"AND", "OR"});
  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;
  private JPanel panel;
  private BeanConnector connector = new BeanConnector();

  public ResultEditor()
  {
  }

  protected void initComponents()
  {
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";

		FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
		PanelBuilder builder = new PanelBuilder(this, layout);

		JTabbedPane tabbedPane = new JTabbedPane();

		CellConstraints cc = new CellConstraints();

		// Tab1 (info)
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);

		builderInfo.addLabel(ResourceManager.getString("id"), cc.xy(2, 2));
		builderInfo.add(id, cc.xy(4, 2));
		connector.connect(id, "id");

		builderInfo.addLabel(ResourceManager.getString("display.name"), cc.xy(2, 4));
		connector.connect(displayName, "displayName");
		builderInfo.add(displayName, cc.xy(4, 4));

		builderInfo.addLabel(ResourceManager.getString("owner"), cc.xy(2, 6));
		connector.connect(owner, "owner");
		builderInfo.add(owner, cc.xy(4, 6));

		builderInfo.addLabel(ResourceManager.getString("status"), cc.xy(2, 8));
		builderInfo.add(status, cc.xy(4, 8));
		connector.connect(status, "status");

		builderInfo.addLabel(ResourceManager.getString("status.old"), cc.xy(2, 10));
		builderInfo.add(oldStatus, cc.xy(4, 10));
		connector.connect(oldStatus, "oldStatus");

		tabbedPane.add(ResourceManager.getString("info"), panelInfo);

		/////////////////////////////
		// Tab4 (validators)
		/////////////////////////////
		FormLayout layoutValid = new FormLayout(colLayout, rowLayout);
		JPanel panelValid = new JPanel();
		PanelBuilder builderValid = new PanelBuilder(panelValid, layoutValid);
	
		validatorsTable = new JTable(validatorsModel);
		validatorsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		builderValid.add(UIFactory.createTablePanel(validatorsTable), cc.xywh(2, 2, 3, 1));
		builderValid.add(UIFactory.getAddRemovePropertiesBar(this, "validator", BUTTONS), cc.xywh(2, 4, 3, 1));

		tabbedPane.add(ResourceManager.getString("validators"), panelValid);

		
		///////////////////////////
		// Tab2 (pre-functions)
		FormLayout layoutPrefunc = new FormLayout(colLayout, rowLayout);
		JPanel panelPrefunc = new JPanel();
		PanelBuilder builderPrefunc = new PanelBuilder(panelPrefunc, layoutPrefunc);

		preFunctionsTable = new JTable(preFunctionsModel);
		preFunctionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		preFunctionsModel.setGraphModel(getModel());
		builderPrefunc.add(UIFactory.createTablePanel(preFunctionsTable), cc.xywh(2, 2, 3, 1));		// 2, 15, 3, 1
		builderPrefunc.add(UIFactory.getAddRemovePropertiesBar(this, "pre", BUTTONS), cc.xywh(2, 4, 3, 1));	// 2, 16, 3, 1

		tabbedPane.add(ResourceManager.getString("prefunctions"), panelPrefunc);

		// Tab3 (post-functions)
		FormLayout layoutPostfunc = new FormLayout(colLayout, rowLayout);
		JPanel panelPostfunc = new JPanel();
		PanelBuilder builderPostfunc = new PanelBuilder(panelPostfunc, layoutPostfunc);

		postFunctionsTable = new JTable(postFunctionsModel);
		postFunctionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		postFunctionsModel.setGraphModel(getModel());
		builderPostfunc.add(UIFactory.createTablePanel(postFunctionsTable), cc.xywh(2, 2, 3, 1));		// 2, 20, 3, 1
		builderPostfunc.add(UIFactory.getAddRemovePropertiesBar(this, "post", BUTTONS), cc.xywh(2, 4, 3, 1));	// 2, 21, 3, 1

		tabbedPane.add(ResourceManager.getString("postfunctions"), panelPostfunc);

		// Tab4 (conditions)
		FormLayout layoutCond = new FormLayout(colLayout, rowLayout);
		JPanel panelCond = new JPanel();
		PanelBuilder builderCond = new PanelBuilder(panelCond, layoutCond);

		builderCond.addLabel(ResourceManager.getString("type"), cc.xy(2, 2));	// 2, 25
		connector.connect(type, "conditions[0].type");
		builderCond.add(type, cc.xy(4, 2));		// 4, 25

		conditionsTable = new JTable(conditionsModel);
		conditionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		conditionsModel.setGraphModel(getModel());
		conditionsModel.setType(ConditionsTableModel.RESULT);
		builderCond.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 4, 3, 1));	// 2, 27, 3, 1
		panel = UIFactory.getAddRemovePropertiesBar(this, "condition", BUTTONS);
		builderCond.add(panel, cc.xywh(2, 6, 3, 1));		// 2, 28, 3, 1

		tabbedPane.add(ResourceManager.getString("conditions"), panelCond);

		builder.add(tabbedPane, cc.xy(2,2));
  }

  public String getTitle()
  {
    return ResourceManager.getString("title.result", new Object[]{id.getText()});
  }

  protected void updateView()
  {
    PaletteDescriptor palette = getModel().getPalette();
    status.setModel(new DefaultComboBoxModel(palette.getStatusNames()));
    oldStatus.setModel(new DefaultComboBoxModel(palette.getStatusNames()));
    //ResultEdge result = (ResultEdge)getEdge();
    ResultDescriptor descriptor = (ResultDescriptor)getDescriptor();

    preFunctionsModel.setList(descriptor.getPreFunctions());
    preFunctionsTable.getSelectionModel().clearSelection();

    postFunctionsModel.setList(descriptor.getPostFunctions());
    postFunctionsTable.getSelectionModel().clearSelection();

		validatorsModel.setList(descriptor.getValidators());
		validatorsTable.getSelectionModel().clearSelection(); 
		
    if(isConditional())
    {
      setConditional(true);
      ConditionalResultDescriptor cond = (ConditionalResultDescriptor)descriptor;
      if(cond.getConditions().size() == 0)
      {
        ConditionsDescriptor conditions = DescriptorFactory.getFactory().createConditionsDescriptor();
        conditions.setParent(cond);
        cond.getConditions().add(conditions);
      }
      conditionsModel.setList(((ConditionsDescriptor)cond.getConditions().get(0)).getConditions());
      conditionsTable.getSelectionModel().clearSelection();
    }
    else
    {
      setConditional(false);
    }
    connector.setSource(descriptor);
    connector.setPanel(this);
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand().toLowerCase();
    if("preadd".equals(command))
    {
      preadd();
    }
    else if("preremove".equals(command))
    {
      preremove();
    }
    else if("preedit".equals(command))
    {
      premodify();
    }
    else if("postadd".equals(command))
    {
      postadd();
    }
    else if("postremove".equals(command))
    {
      postremove();
    }
    else if("postedit".equals(command))
    {
      postmodify();
    }
    else if("conditionadd".equals(command))
    {
      add();
    }
    else if("conditionremove".equals(command))
    {
      remove();
    }
    else if("conditionedit".equals(command))
    {
      modify();
    }
		else if (command.equals("validatoradd"))
		{
			validatoradd();
		}
		else if (command.equals("validatoredit"))
		{
			validatormodify();
		}
		else if (command.equals("validatorremove"))
		{
			validatorremove();
		}
  }

  private void preadd()
  {
    ResultFunctionEditor editor = new ResultFunctionEditor((ResultDescriptor)getDescriptor());
    editor.setModel(getModel());
    FunctionDescriptor func = editor.add();
    if(func != null)
    {
      this.preFunctionsModel.add(func);
    }
  }

  private void preremove()
  {
    int[] rows = preFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      preFunctionsModel.remove(rows[i]);
    }
  }

  private void premodify()
  {
    int[] rows = preFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      premodify(rows[i]);
    }
  }

  private void premodify(int selected)
  {
    FunctionDescriptor func = (FunctionDescriptor)preFunctionsModel.get(selected);

    ResultFunctionEditor editor = new ResultFunctionEditor((ResultDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(func);

    preFunctionsModel.fireTableDataChanged();
  }

  private void postadd()
  {
    ResultFunctionEditor editor = new ResultFunctionEditor((ResultDescriptor)getDescriptor());
    editor.setModel(getModel());
    FunctionDescriptor func = editor.add();
    if(func != null)
    {
      postFunctionsModel.add(func);
    }
  }

  private void postremove()
  {
    int[] rows = postFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      postFunctionsModel.remove(rows[i]);
    }
  }

  private void postmodify()
  {
    int[] rows = postFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      postmodify(rows[i]);
    }
  }

  private void postmodify(int selected)
  {
    FunctionDescriptor func = (FunctionDescriptor)postFunctionsModel.get(selected);

    ResultFunctionEditor editor = new ResultFunctionEditor((ResultDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(func);

    postFunctionsModel.fireTableDataChanged();
  }

  private boolean isConditional()
  {
    //ResultEdge edge = (ResultDescriptor)getEdge();
    ResultDescriptor result = (ResultDescriptor)getDescriptor();

    return (result instanceof ConditionalResultDescriptor);
  }

  private void add()
  {
    ResultConditionEditor editor = new ResultConditionEditor((ResultDescriptor)getDescriptor());
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

    ResultConditionEditor editor = new ResultConditionEditor((ResultDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(cond);

    conditionsModel.fireTableDataChanged();
  }

  private void setConditional(boolean enabled)
  {
    type.setEnabled(enabled);
    conditionsTable.setEnabled(enabled);
    Component[] comps = panel.getComponents();
    for(int i = 0; i < comps.length; i++)
    {
      comps[i].setEnabled(enabled);
    }
  }
  
	private void validatoradd()
	{
		ValidatorEditor editor = new ValidatorEditor(getDescriptor());
		editor.setModel(getModel());
		ValidatorDescriptor val = editor.add();
		if (val!=null)
		{
			validatorsModel.add(val);
		}
	}

	private void validatorremove()
	{
		int[] rows = validatorsTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			validatorsModel.remove(rows[i]);
		}
	}

	private void validatormodify()
	{
		int[] rows = validatorsTable.getSelectedRows();
		for(int i = 0; i < rows.length; i++)
		{
			validatormodify(rows[i]);
		}
	}

	private void validatormodify(int selected)
	{
		ValidatorDescriptor val = (ValidatorDescriptor)validatorsModel.get(selected);

		ValidatorEditor editor = new ValidatorEditor(getDescriptor());
		editor.setModel(getModel());
		editor.modify(val);

		validatorsModel.fireTableDataChanged();
	}
}