package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.model.ResultsTableModel;
import com.opensymphony.workflow.loader.SplitDescriptor;

public class SplitEditor extends DetailPanel
{
  private JTextField id = UIFactory.createReadOnlyTextField(12);
  private ResultsTableModel resultsModel = new ResultsTableModel();

  public SplitEditor()
  {
  }

  protected void initComponents()
  {
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";

		JTabbedPane tabbedPane = new JTabbedPane();
		CellConstraints cc = new CellConstraints();

    //FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 3dlu, pref, 2dlu, 60dlu, 2dlu");
		FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);

    // Tab1 (Info)
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);
    //builderInfo.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 4, 1));
    builderInfo.addLabel(ResourceManager.getString("id"), cc.xy(2, 2));
    builderInfo.add(id, cc.xy(4, 2));

    tabbedPane.add(ResourceManager.getString("info"), panelInfo);

    // Tab2 (Results)
		FormLayout layoutResult = new FormLayout(colLayout, rowLayout);
		JPanel panelResult = new JPanel();
		PanelBuilder builderResult = new PanelBuilder(panelResult, layoutResult);

    //builder.addSeparator(ResourceManager.getString("results"), cc.xywh(2, 5, 3, 1));
    JTable actionsTable = new JTable(resultsModel);
    actionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    builderResult.add(UIFactory.createTablePanel(actionsTable), cc.xywh(2, 2, 3, 1));

    tabbedPane.add(ResourceManager.getString("results"), panelResult);

    builder.add(tabbedPane, cc.xy(2,2));

  }

  public String getTitle()
  {
    return ResourceManager.getString("title.split", new Object[]{id.getText()});
  }

  protected void updateView()
  {
    SplitDescriptor desc = (SplitDescriptor)getDescriptor();
    id.setText(Integer.toString(desc.getId(), 10));
    resultsModel.setList(desc.getResults());
  }
}
