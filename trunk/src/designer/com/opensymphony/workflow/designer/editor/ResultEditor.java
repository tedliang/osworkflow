package com.opensymphony.workflow.designer.editor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.opensymphony.workflow.designer.model.FunctionsTableModel;
import com.opensymphony.workflow.designer.ResultEdge;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.loader.ResultDescriptor;

public class ResultEditor extends DetailPanel implements ActionListener
{
  private JTextField id = new JTextField(12);
  private JTextField owner = new JTextField(12);
  private JTextField status = new JTextField(12);
  private JTextField oldStatus = new JTextField(12);
  private FunctionsTableModel preFunctionsModel = new FunctionsTableModel();
  private FunctionsTableModel postFunctionsModel = new FunctionsTableModel();
  private JTable preFunctionsTable;
  private JTable postFunctionsTable;

  public ResultEditor()
  {
  }

  protected void initComponents()
  {
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 4dlu, " + //next is 11 - actions separator
                                                                                       "pref, 2dlu, 50dlu, pref, 2dlu, pref, 2dlu, 50dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();
    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));
    builder.addLabel("ID", cc.xy(2, 3));
    builder.add(id, cc.xy(4, 3));
    builder.addLabel("Owner", cc.xy(2, 5));
    builder.add(owner, cc.xy(4, 5));
    builder.addLabel("Status", cc.xy(2, 7));
    builder.add(status, cc.xy(4, 7));
    builder.addLabel("Old Status", cc.xy(2, 9));
    builder.add(oldStatus, cc.xy(4, 9));

    builder.addSeparator("Pre-Functions", cc.xywh(2, 11, 3, 1));
    preFunctionsTable = new JTable(preFunctionsModel);
    builder.add(new JScrollPane(preFunctionsTable), cc.xywh(2, 13, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "pre"), cc.xywh(2, 14, 3, 1));

    builder.addSeparator("Post-Functions", cc.xywh(2, 16, 3, 1));
    postFunctionsTable = new JTable(postFunctionsModel);
    builder.add(new JScrollPane(postFunctionsTable), cc.xywh(2, 18, 3, 1));
    builder.add(UIFactory.getTableButtonBar(this, "post"), cc.xywh(2, 19, 3, 1));
  }

  public String getTitle()
  {
    return "Result" + (id.getText() != null && id.getText().length()>0 ? (" #" + id.getText()) : "");
  }

  protected void updateView()
  {
    ResultEdge result = (ResultEdge)getEdge();
    ResultDescriptor descriptor = result.getDescriptor();
    preFunctionsModel.setList(descriptor.getPreFunctions());
    postFunctionsModel.setList(descriptor.getPostFunctions());
    id.setText(descriptor.hasId() ? Integer.toString(descriptor.getId()) : "");
    owner.setText(descriptor.getOwner()!=null ? descriptor.getOwner() : "");
    status.setText(descriptor.getStatus()!=null ? descriptor.getStatus() : "");
    oldStatus.setText(descriptor.getOldStatus()!=null ? descriptor.getOldStatus() : "");
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();
    if("preadd".equals(command))
    {

    }
    else if("preremove".equals(command))
    {
      int[] selected = preFunctionsTable.getSelectedRows();
      for(int i=0;i<selected.length;i++)
      {
        preFunctionsModel.remove(selected[i]);
      }
    }
    else if("preproperties".equals(command))
    {

    }
    else if("postadd".equals(command))
    {

    }
    else if("postremove".equals(command))
    {
      int[] selected = postFunctionsTable.getSelectedRows();
      for(int i=0;i<selected.length;i++)
      {
        postFunctionsModel.remove(selected[i]);
      }
    }
    else if("postproperties".equals(command))
    {

    }
  }
}
