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
import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.loader.*;

public class ResultEditor extends DetailPanel implements ActionListener
{
  private static final String[] BUTTONS = new String[]{"add", "remove", "edit"};

  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private JTextField owner = new JTextField(12);
  private JComboBox status = new JComboBox();
  private JComboBox oldStatus = new JComboBox();
  private FunctionsTableModel preFunctionsModel = new FunctionsTableModel();
  private JTable preFunctionsTable;
  private FunctionsTableModel postFunctionsModel = new FunctionsTableModel();
  private JTable postFunctionsTable;
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
    String colLayout = "2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu";

    String rowLayout = "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 4dlu,pref, 2dlu, 40dlu:grow, pref, 2dlu, pref, 2dlu, 40dlu:grow, pref, 2dlu";

    //		ResultEdge edge = (ResultEdge)getEdge();
    //		ResultDescriptor descriptor = edge.getDescriptor();
    //		if(isConditional()){
    rowLayout += ", pref, 2dlu, pref, 2dlu, 40dlu:grow, pref, 2dlu";
    //		}

    FormLayout layout = new FormLayout(colLayout, rowLayout);

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 3, 1));

    builder.addLabel(ResourceManager.getString("id"), cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    connector.connect(id, "id");

    builder.addLabel(ResourceManager.getString("owner"), cc.xy(2, 5));
    connector.connect(owner, "owner");
    builder.add(owner, cc.xy(4, 5));

    builder.addLabel(ResourceManager.getString("status"), cc.xy(2, 7));
    builder.add(status, cc.xy(4, 7));
    connector.connect(status, "status");

    builder.addLabel(ResourceManager.getString("status.old"), cc.xy(2, 9));
    builder.add(oldStatus, cc.xy(4, 9));
    connector.connect(oldStatus, "oldStatus");

    builder.addSeparator(ResourceManager.getString("prefunctions"), cc.xywh(2, 11, 3, 1));
    preFunctionsTable = new JTable(preFunctionsModel);
    builder.add(UIFactory.createTablePanel(preFunctionsTable), cc.xywh(2, 13, 3, 1));
    builder.add(UIFactory.getButtonBar(this, "pre", BUTTONS), cc.xywh(2, 14, 3, 1));

    builder.addSeparator(ResourceManager.getString("postfunctions"), cc.xywh(2, 16, 3, 1));
    postFunctionsTable = new JTable(postFunctionsModel);
    builder.add(UIFactory.createTablePanel(postFunctionsTable), cc.xywh(2, 18, 3, 1));
    builder.add(UIFactory.getButtonBar(this, "post", BUTTONS), cc.xywh(2, 19, 3, 1));

    //		if(isConditional()){
    builder.addSeparator(ResourceManager.getString("conditions"), cc.xywh(2, 21, 3, 1));

    builder.addLabel(ResourceManager.getString("type"), cc.xy(2, 23));
    connector.connect(type, "conditionType");
    builder.add(type, cc.xy(4, 23));

    conditionsTable = new JTable(conditionsModel);
    builder.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 25, 3, 1));
    panel = UIFactory.getButtonBar(this, "condition", BUTTONS);
    builder.add(panel, cc.xywh(2, 26, 3, 1));
    //		}
  }

  public String getTitle()
  {
    return ResourceManager.getString("title.result", new Object[]{id.getText()});
  }

  protected void updateView()
  {
    WorkflowConfigDescriptor palette = getModel().getPalette();
    status.setModel(new DefaultComboBoxModel(palette.getStatusNames()));
    oldStatus.setModel(new DefaultComboBoxModel(palette.getStatusNames()));
    ResultEdge result = (ResultEdge)getEdge();
    ResultDescriptor descriptor = result.getDescriptor();

    preFunctionsModel.setList(descriptor.getPreFunctions());
    preFunctionsTable.getSelectionModel().clearSelection();

    postFunctionsModel.setList(descriptor.getPostFunctions());
    postFunctionsTable.getSelectionModel().clearSelection();

    if(isConditional())
    {
      setConditional(true);
      ConditionalResultDescriptor cond = (ConditionalResultDescriptor)descriptor;
      conditionsModel.setList(cond.getConditions());
      conditionsTable.getSelectionModel().clearSelection();
    }
    else
    {
      setConditional(false);
    }
    connector.setSource(descriptor);
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
  }

  private void preadd()
  {
    ResultFunctionEditor editor = new ResultFunctionEditor((ResultEdge)getEdge());
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
      preFunctionsModel.remove(i);
    }
  }

  private void premodify()
  {
    int[] rows = preFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      premodify(i);
    }
  }

  private void premodify(int selected)
  {
    FunctionDescriptor func = (FunctionDescriptor)preFunctionsModel.get(selected);

    ResultFunctionEditor editor = new ResultFunctionEditor((ResultEdge)getEdge());
    editor.setModel(getModel());
    editor.modify(func);

    preFunctionsModel.fireTableDataChanged();
  }

  private void postadd()
  {
    ResultFunctionEditor editor = new ResultFunctionEditor((ResultEdge)getEdge());
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
      postFunctionsModel.remove(i);
    }
  }

  private void postmodify()
  {
    int[] rows = postFunctionsTable.getSelectedRows();
    for(int i = 0; i < rows.length; i++)
    {
      postmodify(i);
    }
  }

  private void postmodify(int selected)
  {
    FunctionDescriptor func = (FunctionDescriptor)postFunctionsModel.get(selected);

    ResultFunctionEditor editor = new ResultFunctionEditor((ResultEdge)getEdge());
    editor.setModel(getModel());
    editor.modify(func);

    postFunctionsModel.fireTableDataChanged();
  }

  private boolean isConditional()
  {
    ResultEdge edge = (ResultEdge)getEdge();
    ResultDescriptor result = edge.getDescriptor();

    return (result instanceof ConditionalResultDescriptor);
  }

  private void add()
  {
    ResultConditionEditor editor = new ResultConditionEditor((ResultEdge)getEdge());
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

    ResultConditionEditor editor = new ResultConditionEditor((ResultEdge)getEdge());
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
}