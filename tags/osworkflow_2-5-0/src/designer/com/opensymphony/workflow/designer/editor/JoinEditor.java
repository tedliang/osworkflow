package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.designer.JoinCell;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

public class JoinEditor extends DetailPanel
{
  private JTextField id = new JTextField(12);
  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JComboBox conditionTypes = new JComboBox(new String[]{"OR", "AND"});

  public JoinEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 2dlu, pref, 3dlu, pref, 2dlu, 60dlu, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();
    builder.addSeparator("Info", cc.xywh(2, 1, 4, 1));
    builder.addLabel("ID", cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    builder.addLabel("Condition Type", cc.xy(2, 5));
    builder.add(conditionTypes, cc.xy(4, 5));
    builder.addSeparator("Conditions", cc.xywh(2, 7, 3, 1));
    JTable actionsTable = new JTable(conditionsModel);
    builder.add(new JScrollPane(actionsTable), cc.xywh(2, 9, 3, 1));
  }

  public String getTitle()
  {
    return "Join #" + id.getText();
  }

  protected void updateView()
  {
    JoinCell cell = (JoinCell)getCell();
    id.setText(Integer.toString(cell.getId(), 10));
    String type= cell.getJoinDescriptor().getConditionType();
    if(type==null)
    {
      conditionTypes.setSelectedIndex(-1);
    }
    else
    {
      conditionTypes.setSelectedItem(type);
    }
    conditionsModel.setList(cell.getJoinDescriptor().getConditions());
  }
}
