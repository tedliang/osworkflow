package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.dialogs.AttributeDialog;
import com.opensymphony.workflow.designer.WorkflowDesigner;
//import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.designer.model.AttributesTableModel;
import com.opensymphony.workflow.designer.model.ValidatorsTableModel;
import com.opensymphony.workflow.loader.*;

/**
 * @author jackflit
 *         Date: 2003-11-18
 */
public class ActionEditor extends DetailPanel implements ActionListener
{
  private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};

  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private JTextField name = new JTextField(12);
  private JTextField view = new JTextField(12);
  private JCheckBox auto = new JCheckBox();
  private JCheckBox finish = new JCheckBox();
  private JComboBox restrict = new JComboBox(new String[]{"AND", "OR"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private FunctionsTableModel preModel = new FunctionsTableModel();
  private JTable pre;

  private FunctionsTableModel postModel = new FunctionsTableModel();
  private JTable post;

  private AttributesTableModel attributesModel = new AttributesTableModel();
  private JTable attributesTable;

  private ValidatorsTableModel validatorsModel = new ValidatorsTableModel();
  private JTable validatorsTable;

  private BeanConnector connector = new BeanConnector();
  private ActionDescriptor descriptor;

  public ActionEditor()
  {
  }

  protected void initComponents()
  {
    String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
    String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";

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

    builderInfo.addLabel(ResourceManager.getString("auto"), cc.xy(2, 8));
    connector.connect(auto, "autoExecute");
    builderInfo.add(auto, cc.xy(4, 8));

    builderInfo.addLabel(ResourceManager.getString("finish"), cc.xy(2, 10));
    connector.connect(finish, "finish");
    builderInfo.add(finish, cc.xy(4, 10));

    tabbedPane.add(ResourceManager.getString("info"), panelInfo);

    ///////////////////////////
    // Tab2 (meta attributes)
    ///////////////////////////
    FormLayout layoutAttrib = new FormLayout(colLayout, rowLayout);
    JPanel panelAttrib = new JPanel();
    PanelBuilder builderAttrib = new PanelBuilder(panelAttrib, layoutAttrib);

    attributesTable = new JTable(attributesModel);
    attributesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    builderAttrib.add(UIFactory.createTablePanel(attributesTable), cc.xywh(2, 2, 3, 1));		// 2, 15, 3, 1
    builderAttrib.add(UIFactory.getAddRemovePropertiesBar(this, "attribute", BUTTONS), cc.xywh(2, 4, 3, 1));	// 2, 16, 3, 1

    tabbedPane.add(ResourceManager.getString("attributes"), panelAttrib);


    /////////////////////////////
    // Tab3 (permissions)
    /////////////////////////////
    FormLayout layoutPerm = new FormLayout(colLayout, rowLayout);
    JPanel panelPerm = new JPanel();
    PanelBuilder builderPerm = new PanelBuilder(panelPerm, layoutPerm);
    //builderInfo.addSeparator(ResourceManager.getString("permissions"), cc.xywh(2, 8, 3, 1));

    builderPerm.addLabel(ResourceManager.getString("type"), cc.xy(2, 2));
    connector.connect(restrict, "restriction.conditionsDescriptor.type");
    builderPerm.add(restrict, cc.xy(4, 2));

    conditionsTable = new JTable(conditionsModel);
    conditionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    conditionsModel.setType(ConditionsTableModel.PERMISSION);
    conditionsModel.setGraphModel(getModel());
    builderPerm.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 4, 3, 1));
    builderPerm.add(UIFactory.getAddRemovePropertiesBar(this, "permission", BUTTONS), cc.xywh(2, 6, 3, 1));

    tabbedPane.add(ResourceManager.getString("permissions"), panelPerm);

    /////////////////////////////
    // Tab4 (validators)
    /////////////////////////////
    FormLayout layoutValid = new FormLayout(colLayout, rowLayout);
    JPanel panelValid = new JPanel();
    PanelBuilder builderValid = new PanelBuilder(panelValid, layoutValid);

    validatorsTable = new JTable(validatorsModel);
    validatorsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //validatorsModel.setGraphModel(getModel());
    builderValid.add(UIFactory.createTablePanel(validatorsTable), cc.xywh(2, 2, 3, 1));
    builderValid.add(UIFactory.getAddRemovePropertiesBar(this, "validator", BUTTONS), cc.xywh(2, 4, 3, 1));

    tabbedPane.add(ResourceManager.getString("validators"), panelValid);


    /////////////////////////////
    // Tab5 (pre-functions)
    /////////////////////////////
    FormLayout layoutPrefunc = new FormLayout(colLayout, rowLayout);
    JPanel panelPrefunc = new JPanel();
    PanelBuilder builderPrefunc = new PanelBuilder(panelPrefunc, layoutPrefunc);

    pre = new JTable(preModel);
    pre.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    preModel.setGraphModel(getModel());
    builderPrefunc.add(UIFactory.createTablePanel(pre), cc.xywh(2, 2, 3, 1));
    builderPrefunc.add(UIFactory.getAddRemovePropertiesBar(this, "pre", BUTTONS), cc.xywh(2, 4, 3, 1));

    tabbedPane.add(ResourceManager.getString("prefunctions"), panelPrefunc);

    /////////////////////////////
    // Tab6 (post-functions)
    /////////////////////////////
    FormLayout layoutPostfunc = new FormLayout(colLayout, rowLayout);
    JPanel panelPostfunc = new JPanel();
    PanelBuilder builderPostfunc = new PanelBuilder(panelPostfunc, layoutPostfunc);

    post = new JTable(postModel);
    post.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    postModel.setGraphModel(getModel());
    builderPostfunc.add(UIFactory.createTablePanel(post), cc.xywh(2, 2, 3, 1));
    builderPostfunc.add(UIFactory.getAddRemovePropertiesBar(this, "post", BUTTONS), cc.xywh(2, 4, 3, 1));

    tabbedPane.add(ResourceManager.getString("postfunctions"), panelPostfunc);

    builder.add(tabbedPane, cc.xy(2, 2));
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

    if(attributesTable.isEditing())
    {
      attributesTable.getCellEditor().stopCellEditing();
    }

    if(validatorsTable.isEditing())
    {
      validatorsTable.getCellEditor().stopCellEditing();
    }
  }

  protected void updateView()
  {
    descriptor = (ActionDescriptor)getDescriptor();
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

    attributesModel.setMap(descriptor.getMetaAttributes());
    attributesTable.getSelectionModel().clearSelection();

    validatorsModel.setList(descriptor.getValidators());
    validatorsTable.getSelectionModel().clearSelection();

    connector.setSource(descriptor);
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();

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
    else if(command.equals("permissionadd"))
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
    else if(command.equals("validatoradd"))
    {
      validatoradd();
    }
    else if(command.equals("validatoredit"))
    {
      validatormodify();
    }
    else if(command.equals("validatorremove"))
    {
      validatorremove();
    }
  }

  private void attributeadd()
  {
    AttributeDialog dlg = new AttributeDialog(WorkflowDesigner.INSTANCE, "", "", true);
    if(dlg.ask(WorkflowDesigner.INSTANCE))
    {
      String sKey = dlg.keyField.getText();
      String sValue = dlg.valueField.getText();
      if(sKey.length() > 0)
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
    if((sKey != null) && (sKey.length() > 0))
    {
      AttributeDialog dlg = new AttributeDialog(WorkflowDesigner.INSTANCE, sKey, sValue, false);
      if(dlg.ask(WorkflowDesigner.INSTANCE))
      {
        sValue = dlg.valueField.getText();
        attributesModel.add(sKey, sValue);
      }
    }
  }

  private void add()
  {
    StartPermissionEditor editor = new StartPermissionEditor((ActionDescriptor)getDescriptor());
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

    StartPermissionEditor editor = new StartPermissionEditor((ActionDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(cond);

    conditionsModel.fireTableDataChanged();
  }

  private void preadd()
  {
    StartFunctionEditor editor = new StartFunctionEditor((ActionDescriptor)getDescriptor());
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

    StartFunctionEditor editor = new StartFunctionEditor((ActionDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(func);

    preModel.fireTableDataChanged();
  }

  private void postadd()
  {
    StartFunctionEditor editor = new StartFunctionEditor((ActionDescriptor)getDescriptor());
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

    StartFunctionEditor editor = new StartFunctionEditor((ActionDescriptor)getDescriptor());
    editor.setModel(getModel());
    editor.modify(func);

    postModel.fireTableDataChanged();
  }

  private void validatoradd()
  {
    ValidatorEditor editor = new ValidatorEditor(getDescriptor());
    editor.setModel(getModel());
    ValidatorDescriptor val = editor.add();
    if(val != null)
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
