package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.loader.*;

public class StepEditor extends DetailPanel implements ActionListener
{
  private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};

  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private JTextField name = new JTextField(12);
  private JTextField view = new JTextField(12);
  private JCheckBox auto = new JCheckBox();
  private JComboBox restrict = new JComboBox(new String[]{"AND", "OR"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private FunctionsTableModel preModel = new FunctionsTableModel();
  private JTable pre;

  private FunctionsTableModel postModel = new FunctionsTableModel();
  private JTable post;

  private BeanConnector connector = new BeanConnector();

  public StepEditor()
  {
  }

  protected void initComponents()
  {
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";

    StepCell cell = (StepCell)getCell();

		//FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
		FormLayout layout = new FormLayout("2dlu, pref:grow, 2dlu", "2dlu, pref, 2dlu");
		PanelBuilder builder = new PanelBuilder(this, layout);

		JTabbedPane tabbedPane = new JTabbedPane();

    CellConstraints cc = new CellConstraints();

    // Tab1 (Info)
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);

    builderInfo.addLabel(ResourceManager.getString("id"), cc.xy(2, 2));
    connector.connect(id, "id");
    builderInfo.add(id, cc.xy(4, 2));

    builderInfo.addLabel(ResourceManager.getString("name"), cc.xy(2, 4));
    connector.connect(name, "name");
    builderInfo.add(name, cc.xy(4, 4));

    builderInfo.addLabel(ResourceManager.getString("view"), cc.xy(2, 6));
    connector.connect(view, "actions[0].view");
    builderInfo.add(view, cc.xy(4, 6));

    builderInfo.addLabel(ResourceManager.getString("auto"), cc.xy(2, 8));
    connector.connect(auto, "actions[0].autoExecute");
    builderInfo.add(auto, cc.xy(4, 8));

		tabbedPane.add(ResourceManager.getString("info"), panelInfo);

		// Tab2 (Permissions)
		FormLayout layoutPerm = new FormLayout(colLayout, rowLayout);
		JPanel panelPerm = new JPanel();
		PanelBuilder builderPerm = new PanelBuilder(panelPerm, layoutPerm);

    builderPerm.addLabel(ResourceManager.getString("type"), cc.xy(2, 2));
    connector.connect(restrict, "restriction/conditionType");
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

    pre = new JTable(preModel);
    preModel.setGraphModel(getModel());
    builderPrefunc.add(UIFactory.createTablePanel(pre), cc.xywh(2, 2, 3, 1));
    builderPrefunc.add(UIFactory.getAddRemovePropertiesBar(this, "pre", BUTTONS), cc.xywh(2, 4, 3, 1));

		tabbedPane.add(ResourceManager.getString("prefunctions"), panelPrefunc);

		// Tab4 (post-functions)
		FormLayout layoutPostfunc = new FormLayout(colLayout, rowLayout);
		JPanel panelPostfunc = new JPanel();
		PanelBuilder builderPostfunc = new PanelBuilder(panelPostfunc, layoutPostfunc);

    post = new JTable(postModel);
    postModel.setGraphModel(getModel());
    builderPostfunc.add(UIFactory.createTablePanel(post), cc.xywh(2, 2, 3, 1));
    builderPostfunc.add(UIFactory.getAddRemovePropertiesBar(this, "post", BUTTONS), cc.xywh(2, 4, 3, 1));

    tabbedPane.add(ResourceManager.getString("postfunctions"), panelPostfunc);

		builder.add(tabbedPane, cc.xy(2,2));
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand().toLowerCase();
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

  public String getTitle()
  {
    return ResourceManager.getString("title.step", new Object[]{id.getText()});
  }

  protected void viewClosed()
  {
    if(conditionsTable.isEditing())
    {
      conditionsTable.getCellEditor().stopCellEditing();
    }
  }

  protected void updateView()
  {
    StepCell cell = (StepCell)getCell();
    StepDescriptor stepDescriptor = cell.getDescriptor();

    ActionDescriptor firstAction;

    //todo need to do this in some way that handles multiple actions
    if(stepDescriptor.getActions().size()>0)
    {
      firstAction = (ActionDescriptor)stepDescriptor.getActions().get(0);
    }
    else
    {
      firstAction = null;
    }

    if(firstAction!=null)
    {
      RestrictionDescriptor rd = firstAction.getRestriction();
      if(rd==null)
      {
        rd = new RestrictionDescriptor();
        rd.setParent(firstAction);
        ConditionsDescriptor conditions = new ConditionsDescriptor();
        rd.setConditionsDescriptor(conditions);
        conditions.setParent(rd);
        conditions.setType((String)restrict.getSelectedItem());
        firstAction.setRestriction(rd);
      }
      //todo no nested conditions allowed
      conditionsModel.setList(rd.getConditionsDescriptor().getConditions());
    }
    else
    {
      conditionsModel.setList(new ArrayList());
    }
    conditionsTable.getSelectionModel().clearSelection();

    preModel.setList(firstAction==null ? new ArrayList() : firstAction.getPreFunctions());
    pre.getSelectionModel().clearSelection();

    postModel.setList(firstAction==null ? new ArrayList() : firstAction.getPostFunctions());
    post.getSelectionModel().clearSelection();

    connector.setSource(stepDescriptor);
  }

  private void add()
  {
    StepPermissionEditor editor = new StepPermissionEditor((StepCell)getCell());
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

    StepPermissionEditor editor = new StepPermissionEditor((StepCell)getCell());
    editor.setModel(getModel());
    editor.modify(cond);

    conditionsModel.fireTableDataChanged();
  }

  private void preadd()
  {
    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
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

    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
    editor.setModel(getModel());
    editor.modify(func);

    preModel.fireTableDataChanged();
  }

  private void postadd()
  {
    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
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

    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
    editor.setModel(getModel());
    editor.modify(func);

    postModel.fireTableDataChanged();
  }
}