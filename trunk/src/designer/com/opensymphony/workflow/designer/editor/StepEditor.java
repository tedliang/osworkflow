package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.StepCell;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.model.ActionsTableModel;
import com.opensymphony.workflow.designer.model.PermissionsTableModel;
import com.opensymphony.workflow.loader.StepDescriptor;

public class StepEditor extends DetailPanel implements ActionListener
{
  private JTextField id = new JTextField(12);
  private JTextField name = new JTextField(12);
  private ActionsTableModel actionsModel = new ActionsTableModel();
  private PermissionsTableModel permissionsModel = new PermissionsTableModel();
  private JTable permissionsTable;
  private JTable actionsTable;

  public StepEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 2dlu, pref, 4dlu, " + //next is 7 - actions separator
                                                                                  "pref, 2dlu, 50dlu, pref, 2dlu, pref, 2dlu, 50dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();
    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));
    builder.addLabel("ID", cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    id.setEditable(false);
    builder.addLabel("Name", cc.xy(2, 5));
    builder.add(name, cc.xy(4, 5));

    builder.addSeparator("Actions", cc.xywh(2, 7, 3, 1));
    actionsTable = new JTable(actionsModel);
    builder.add(new JScrollPane(actionsTable), cc.xywh(2, 9, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "action"), cc.xywh(2, 10, 3, 1));

    builder.addSeparator("Permissions", cc.xywh(2, 12, 3, 1));
    permissionsTable = new JTable(permissionsModel);
    builder.add(new JScrollPane(permissionsTable), cc.xywh(2, 14, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "permission"), cc.xywh(2, 15, 3, 1));

    name.addActionListener(this);
    id.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource()==name)
    {
      StepCell cell = (StepCell)getCell();
      cell.setName(name.getText());
      return;
    }

    String command = e.getActionCommand();
    if(command.equals("actionadd"))
    {

    }
    else if(command.equals("actionremove"))
    {
      int[] selected = actionsTable.getSelectedRows();
      for(int i=0;i<selected.length;i++)
      {
        actionsModel.remove(selected[i]);
      }
    }
    else if(command.equals("actionproperties"))
    {

    }
    else if(command.equals("permissionadd"))
    {
      //PermissionDescriptor descriptor = new PermissionDescriptor();
      //descriptor.setId("0");
      //permissionsModel.add(descriptor);
    }
    else if(command.equals("permissionremove"))
    {
      int[] selected = permissionsTable.getSelectedRows();
      for(int i=0;i<selected.length;i++)
      {
        permissionsModel.remove(selected[i]);
      }
    }
    else if(command.equals("permissionproperties"))
    {

    }
  }

  public String getTitle()
  {
    return "Step #" + id.getText();
  }

  protected void viewClosed()
  {
    if(permissionsTable.isEditing())
    {
      permissionsTable.getCellEditor().stopCellEditing();
    }
    if(actionsTable.isEditing())
    {
      actionsTable.getCellEditor().stopCellEditing();
    }
  }

  protected void updateView()
  {
    StepCell cell = (StepCell)getCell();
    StepDescriptor descriptor = cell.getDescriptor();
    name.setText(cell.getName());
    id.setText(String.valueOf(cell.getId()));
    actionsModel.setList(descriptor.getActions());
    permissionsModel.setList(descriptor.getPermissions());
    actionsTable.getSelectionModel().clearSelection();
    permissionsTable.getSelectionModel().clearSelection();
  }
}
