package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.InitialActionCell;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.RestrictionDescriptor;

/**
 * @author jackflit
 * Date: 2003-11-18
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

  public InitialActionEditor()
  {
  }

  protected void initComponents()
  {
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

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 3, 1));

    builder.addLabel(ResourceManager.getString("id"), cc.xy(2, 3));
    connector.connect(id, "id");
    builder.add(id, cc.xy(4, 3));

    builder.addLabel(ResourceManager.getString("name"), cc.xy(2, 5));
    connector.connect(name, "name");
    builder.add(name, cc.xy(4, 5));

    builder.addLabel(ResourceManager.getString("view"), cc.xy(2, 7));
    connector.connect(view, "view");
    builder.add(view, cc.xy(4, 7));

    builder.addSeparator(ResourceManager.getString("permissions"), cc.xywh(2, 9, 3, 1));

    builder.addLabel(ResourceManager.getString("type"), cc.xy(2, 11));
    connector.connect(restrict, "restriction.conditionType");
    builder.add(restrict, cc.xy(4, 11));

    conditionsTable = new JTable(conditionsModel);
    builder.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 13, 3, 1));
    builder.add(UIFactory.getButtonBar(this, "permission", BUTTONS), cc.xywh(2, 14, 3, 1));

    builder.addSeparator(ResourceManager.getString("prefunctions"), cc.xywh(2, 16, 3, 1));

    pre = new JTable(preModel);
    builder.add(UIFactory.createTablePanel(pre), cc.xywh(2, 18, 3, 1));
    builder.add(UIFactory.getButtonBar(this, "pre", BUTTONS), cc.xywh(2, 19, 3, 1));

    builder.addSeparator(ResourceManager.getString("postfunctions"), cc.xywh(2, 21, 3, 1));

    post = new JTable(postModel);
    builder.add(UIFactory.createTablePanel(post), cc.xywh(2, 23, 3, 1));
    builder.add(UIFactory.getButtonBar(this, "post", BUTTONS), cc.xywh(2, 24, 3, 1));
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
    ActionDescriptor descriptor = cell.getActionDescriptor();

    RestrictionDescriptor rd = descriptor.getRestriction();
    conditionsModel.setList(rd != null ? descriptor.getRestriction().getConditions() : new ArrayList());
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
      conditionsModel.remove(i);
    }
  }

  private void modify()
  {
    int[] rows = conditionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      modify(i);
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
      preModel.remove(i);
    }
  }

  private void premodify()
  {
    int[] rows = pre.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      premodify(i);
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
      postModel.remove(i);
    }
  }

  private void postmodify()
  {
    int[] rows = post.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      postmodify(i);
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
