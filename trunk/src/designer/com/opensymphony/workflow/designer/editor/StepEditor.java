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
import com.opensymphony.workflow.designer.listener.RestrictTypeListener;
import com.opensymphony.workflow.designer.listener.TextFieldListener;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.loader.*;

public class StepEditor extends DetailPanel implements ActionListener
{
  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private JTextField name = new JTextField(12);
  private JTextField view = new JTextField(12);
  private JCheckBox auto = new JCheckBox();
  private RestrictTypeListener restrictListener = new RestrictTypeListener();
  private JComboBox restrict = new JComboBox(new String[]{"AND", "OR"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private FunctionsTableModel preModel = new FunctionsTableModel();
  private JTable pre;

  private FunctionsTableModel postModel = new FunctionsTableModel();
  private JTable post;
  private ActionDescriptor firstAction = null;
  private StepDescriptor stepDescriptor;

  public StepEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, " + // 1 Info
      "pref, 2dlu, " + // 3 ID
      "pref, 2dlu, " + // 5 Name
      "pref, 2dlu, " + // 7 View
      "pref, 2dlu, " + // 9 auto
      "pref, 2dlu, " + // 11 permission
      "pref, 2dlu, " + // 13 type
      "40dlu:grow, " + // 15 table
      "pref, 2dlu, " + // 16 buttons
      "pref, 2dlu, " + // 18 pre-function
      "40dlu:grow, " + // 20 table
      "pref, 2dlu, " + // 21 buttons
      "pref, 2dlu, " + // 23 post-function
      "40dlu:grow, " + // 25 table
      "pref, 2dlu");	 // 26 buttons

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));

    builder.addLabel("ID", cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));

    builder.addLabel("Name", cc.xy(2, 5));
    name.getDocument().addDocumentListener(new TextFieldListener()
    {
      protected void valueChanged(String msg)
      {
        stepDescriptor.setName(msg);
      }
    });
    builder.add(name, cc.xy(4, 5));

    builder.addLabel("View", cc.xy(2, 7));
    view.getDocument().addDocumentListener(new TextFieldListener()
    {
      protected void valueChanged(String msg)
      {
        if(firstAction!=null) firstAction.setView(msg);
      }
    });
    builder.add(view, cc.xy(4, 7));

    builder.addLabel("Auto", cc.xy(2, 9));
    auto.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(firstAction!=null) firstAction.setAutoExecute(auto.isSelected());
      }
    });
    builder.add(auto, cc.xy(4, 9));

    builder.addSeparator("Permissions", cc.xywh(2, 11, 3, 1));

    builder.addLabel("Type", cc.xy(2, 13));
    restrict.addActionListener(restrictListener);
    builder.add(restrict, cc.xy(4, 13));

    conditionsTable = new JTable(conditionsModel);
    conditionsTable.setShowGrid(true);
    builder.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 15, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "permission", new String[]{"add", "remove", "edit"}), cc.xywh(2, 16, 3, 1));

    builder.addSeparator("Pre-funtions", cc.xywh(2, 18, 3, 1));

    pre = new JTable(preModel);
    builder.add(UIFactory.createTablePanel(pre), cc.xywh(2, 20, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "pre", new String[]{"add", "remove", "edit"}), cc.xywh(2, 21, 3, 1));

    builder.addSeparator("Post-functions", cc.xywh(2, 23, 3, 1));

    post = new JTable(postModel);
    builder.add(UIFactory.createTablePanel(post), cc.xywh(2, 25, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "post", new String[]{"add", "remove", "edit"}), cc.xywh(2, 26, 3, 1));

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

  public String getTitle()
  {
    return "Step #" + id.getText();
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
    stepDescriptor = cell.getDescriptor();
    id.setText(String.valueOf(cell.getId()));

    name.setText(stepDescriptor.getName());

    if(stepDescriptor.getActions().size()>0)
    {
      firstAction = (ActionDescriptor)stepDescriptor.getActions().get(0);
    }
    else
    {
      firstAction = null;
    }

    view.setText(firstAction==null ? "" : firstAction.getView());

    if(firstAction!=null && firstAction.getAutoExecute())
    {
      auto.setSelected(true);
    }
    else
    {
      auto.setSelected(false);
    }

    if(firstAction!=null)
    {
      RestrictionDescriptor rd = firstAction.getRestriction();
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
      conditionsModel.setList(rd!=null ? rd.getConditions() : new ArrayList());
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
  }

  private void add()
  {
    StepPermissionEditor editor = new StepPermissionEditor((StepCell)getCell());
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

    StepPermissionEditor editor = new StepPermissionEditor((StepCell)getCell());
    editor.modify(cond);

    conditionsModel.fireTableDataChanged();
  }

  private void preadd()
  {
    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
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

    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
    editor.modify(func);

    preModel.fireTableDataChanged();
  }

  private void postadd()
  {
    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
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

    StepFunctionEditor editor = new StepFunctionEditor((StepCell)getCell());
    editor.modify(func);

    postModel.fireTableDataChanged();
  }

}