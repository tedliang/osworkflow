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
import com.opensymphony.workflow.designer.listener.RestrictTypeListener;
import com.opensymphony.workflow.designer.listener.TextFieldListener;
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
  private JTextField id = new JTextField(12);

  private JTextField name = new JTextField(12);

  private JTextField view = new JTextField(12);

  private RestrictTypeListener restrictListener = new RestrictTypeListener();
  private JComboBox restrict = new JComboBox(new String[]{"AND", "OR"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private FunctionsTableModel preModel = new FunctionsTableModel();
  private JTable pre;

  private FunctionsTableModel postModel = new FunctionsTableModel();
  private JTable post;
  private ActionDescriptor descriptor;

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
      "50dlu, " + // 13 table
      "pref, 2dlu, " + // 14 buttons
      "pref, 2dlu, " + // 16 pre-function
      "50dlu, " + // 18 table
      "pref, 2dlu, " + // 19 buttons
      "pref, 2dlu, " + // 21 post-function
      "50dlu, " + // 23 table
      "pref, 2dlu");	 // 24 buttons

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));

    builder.addLabel("ID", cc.xy(2, 3));
    id.setEditable(false);
    builder.add(id, cc.xy(4, 3));

    builder.addLabel("Name", cc.xy(2, 5));
    name.getDocument().addDocumentListener(new TextFieldListener()
    {
      protected void valueChanged(String msg)
      {
        descriptor.setName(msg);
      }
    });
    builder.add(name, cc.xy(4, 5));

    builder.addLabel("View", cc.xy(2, 7));
    view.getDocument().addDocumentListener(new TextFieldListener()
    {
      protected void valueChanged(String msg)
      {
        descriptor.setView(msg);
      }
    });
    builder.add(view, cc.xy(4, 7));

    builder.addSeparator("Permissions", cc.xywh(2, 9, 3, 1));

    builder.addLabel("Type", cc.xy(2, 11));
    restrict.addActionListener(restrictListener);
    builder.add(restrict, cc.xy(4, 11));

    conditionsTable = new JTable(conditionsModel);
    builder.add(new JScrollPane(conditionsTable), cc.xywh(2, 13, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "permission", new String[]{"add", "remove", "edit"}), cc.xywh(2, 14, 3, 1));

    builder.addSeparator("Pre-funtions", cc.xywh(2, 16, 3, 1));

    pre = new JTable(preModel);
    builder.add(new JScrollPane(pre), cc.xywh(2, 18, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "pre", new String[]{"add", "remove", "edit"}), cc.xywh(2, 19, 3, 1));

    builder.addSeparator("Post-functions", cc.xywh(2, 21, 3, 1));

    post = new JTable(postModel);
    builder.add(new JScrollPane(post), cc.xywh(2, 23, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "post", new String[]{"add", "remove", "edit"}), cc.xywh(2, 24, 3, 1));
  }

  public String getTitle()
  {
    return "Start";
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
    id.setText(String.valueOf(cell.getId()));

    name.setText(descriptor.getName());
    view.setText(descriptor.getView());

    RestrictionDescriptor rd = descriptor.getRestriction();
    if(rd != null)
    {
      restrictListener.setRestrict(rd);
      if(rd.getConditionType() == null)
      {
        restrict.setSelectedIndex(-1);
      }
      else
      {
        restrict.setSelectedItem(rd.getConditionType());
      }
    }
    conditionsModel.setList(rd != null ? descriptor.getRestriction().getConditions() : new ArrayList());
    conditionsTable.getSelectionModel().clearSelection();

    preModel.setList(descriptor.getPreFunctions());
    pre.getSelectionModel().clearSelection();

    postModel.setList(descriptor.getPostFunctions());
    post.getSelectionModel().clearSelection();
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
    editor.modify(cond);

    conditionsModel.fireTableDataChanged();
  }

  private void preadd()
  {
    StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
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
    editor.modify(func);

    preModel.fireTableDataChanged();
  }

  private void postadd()
  {
    StartFunctionEditor editor = new StartFunctionEditor((InitialActionCell)getCell());
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
    editor.modify(func);

    postModel.fireTableDataChanged();
  }

}
