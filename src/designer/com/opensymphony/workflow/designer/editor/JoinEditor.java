package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.JoinCell;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.model.ResultsTableModel;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.JoinDescriptor;

public class JoinEditor extends DetailPanel implements ActionListener
{
  private JTextField id = UIFactory.createReadOnlyTextField(12);

  private JComboBox conditionTypes = new JComboBox(new String[]{"OR", "AND"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private ResultsTableModel resultsModel = new ResultsTableModel();
  private JTable resultsTable;
  private BeanConnector connector = new BeanConnector();

  public JoinEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 2dlu, pref, 3dlu, pref, 2dlu, 60dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();
    builder.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 3, 1));
    builder.addLabel(ResourceManager.getString("id"), cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    connector.connect(id, "id");

    builder.addLabel(ResourceManager.getString("condition.type"), cc.xy(2, 5));

    builder.add(conditionTypes, cc.xy(4, 5));
    connector.connect(conditionTypes, "conditionType");
    builder.addSeparator(ResourceManager.getString("conditions"), cc.xywh(2, 7, 3, 1));

    conditionsTable = new JTable(conditionsModel);
    builder.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 9, 3, 1));

    builder.add(UIFactory.getTableButtonBar(this, "cond", new String[]{"add", "remove", "edit"}), cc.xywh(2, 10, 3, 1));

  }

  public String getTitle()
  {
    return ResourceManager.getString("title.join", new Object[]{id.getText()});
  }

  protected void updateView()
  {
    JoinCell cell = (JoinCell)getCell();

    JoinDescriptor descriptor = cell.getJoinDescriptor();

    conditionsModel.setList(descriptor.getConditions());
    connector.setSource(descriptor);

    //		if (cell.getJoinDescriptor().getResult() != null) {
    //			List list = new ArrayList();
    //			list.add(cell.getJoinDescriptor().getResult());
    //			resultsModel.setList(list);
    //		}
    //		else{
    //			resultsModel.setList(new ArrayList());
    //		}
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand().toLowerCase();
    if(command.equals("condadd"))
    {
      add();
    }
    else if(command.equals("condremove"))
    {
      remove();
    }
    else if(command.equals("condedit"))
    {
      modify();
    }

  }

  private void add()
  {
    JoinConditionEditor editor = new JoinConditionEditor((JoinCell)this.getCell());
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
    JoinConditionEditor editor = new JoinConditionEditor((JoinCell)getCell());
    editor.setModel(getModel());
    ConditionDescriptor cond = (ConditionDescriptor)conditionsModel.get(selected);
    editor.modify(cond);
    conditionsModel.fireTableDataChanged();

  }
}