package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.SplitCell;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.model.ResultsTableModel;

public class SplitEditor extends DetailPanel
{
  private JTextField id = new JTextField(12);
  private ResultsTableModel resultsModel = new ResultsTableModel();

  public SplitEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 3dlu, pref, 2dlu, 60dlu, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();
    builder.addSeparator("Info", cc.xywh(2, 1, 4, 1));
    builder.addLabel("ID", cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    builder.addSeparator("Results", cc.xywh(2, 5, 3, 1));
    JTable actionsTable = new JTable(resultsModel);
    builder.add(UIFactory.createTablePanel(actionsTable), cc.xywh(2, 7, 3, 1));
  }

  public String getTitle()
  {
    return "Split #" + id.getText();
  }

  protected void updateView()
  {
    SplitCell cell = (SplitCell)getCell();
    id.setText(Integer.toString(cell.getId(), 10));
    resultsModel.setList(cell.getSplitDescriptor().getResults());
  }
}
