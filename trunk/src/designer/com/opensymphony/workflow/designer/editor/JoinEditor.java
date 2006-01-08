package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.beanutils.BeanConnector;
import com.opensymphony.workflow.designer.model.ConditionsTableModel;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.ConditionsDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

public class JoinEditor extends DetailPanel implements ActionListener
{
  private JTextField id = UIFactory.createReadOnlyTextField(12);

  private JComboBox conditionTypes = new JComboBox(new String[]{"OR", "AND"});

  private ConditionsTableModel conditionsModel = new ConditionsTableModel();
  private JTable conditionsTable;

  private BeanConnector connector = new BeanConnector();

  public JoinEditor()
  {
  }

  protected void initComponents()
  {
    String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
    String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";

    JTabbedPane tabbedPane = new JTabbedPane();
    //FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 2dlu, pref, 3dlu, pref, 2dlu, 60dlu, pref, 2dlu");
    FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    // Tab1 (info)
    FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
    JPanel panelInfo = new JPanel();
    PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);
    //builder.addSeparator(ResourceManager.getString("info"), cc.xywh(2, 1, 3, 1));
    builderInfo.addLabel(ResourceManager.getString("id"), cc.xy(2, 2));
    builderInfo.add(id, cc.xy(4, 2));
    connector.connect(id, "id");

    builderInfo.addLabel(ResourceManager.getString("condition.type"), cc.xy(2, 4));

    builderInfo.add(conditionTypes, cc.xy(4, 4));
    connector.connect(conditionTypes, "conditionType");

    tabbedPane.add(ResourceManager.getString("info"), panelInfo);

    /////////////////////////////////////////
    // Tab2 (Conditions)
    /////////////////////////////////////////
    FormLayout layoutCond = new FormLayout(colLayout, rowLayout);
    JPanel panelCond = new JPanel();
    PanelBuilder builderCond = new PanelBuilder(panelCond, layoutCond);
    //builder.addSeparator(ResourceManager.getString("conditions"), cc.xywh(2, 7, 3, 1));

    conditionsModel.setGraphModel(getModel());
    conditionsModel.setType(ConditionsTableModel.JOIN);
    conditionsTable = new JTable(conditionsModel);
    conditionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    builderCond.add(UIFactory.createTablePanel(conditionsTable), cc.xywh(2, 2, 3, 1));
    builderCond.add(UIFactory.getAddRemovePropertiesBar(this, "cond", new String[]{"add", "remove", "edit"}), cc.xywh(2, 4, 3, 1));

    tabbedPane.add(ResourceManager.getString("conditions"), panelCond);

    builder.add(tabbedPane, cc.xy(2,2));
  }

  public String getTitle()
  {
    return ResourceManager.getString("title.join", new Object[]{id.getText()});
  }

  protected void updateView()
  {
     JoinDescriptor descriptor = (JoinDescriptor)getDescriptor();
    if(descriptor.getConditions().size() == 0)
    {
      ConditionsDescriptor conditions = DescriptorFactory.getFactory().createConditionsDescriptor();
      descriptor.getConditions().add(conditions);
    }
    conditionsModel.setList(((ConditionsDescriptor)descriptor.getConditions().get(0)).getConditions());
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
    JoinConditionEditor editor = new JoinConditionEditor((JoinDescriptor)this.getDescriptor());
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
    JoinConditionEditor editor = new JoinConditionEditor((JoinDescriptor)this.getDescriptor());
    editor.setModel(getModel());
    ConditionDescriptor cond = (ConditionDescriptor)conditionsModel.get(selected);
    editor.modify(cond);
    conditionsModel.fireTableDataChanged();

  }
}