package com.opensymphony.workflow.designer.editor;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
//import com.opensymphony.workflow.designer.UIFactory;
//import com.opensymphony.workflow.designer.ResourceManager;
//import com.opensymphony.workflow.designer.model.ResultsTableModel;

public class GenericEditor extends DetailPanel
{
	protected JLabel label = new JLabel();
	  
	private boolean componentsInited = false;  
	  
  public GenericEditor()
  {
  }

  protected void initComponents()
  {
		String colLayout = "2dlu, max(32dlu;pref), 2dlu, pref:grow, 4dlu";
		String rowLayout = "4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref";
		
		JTabbedPane tabbedPane = new JTabbedPane();
		CellConstraints cc = new CellConstraints();
    
    FormLayout layout = new FormLayout("2dlu, 50dlu:grow, 2dlu", "2dlu, pref, 2dlu");
    PanelBuilder builder = new PanelBuilder(this, layout);
    
    /////////////////////////////////////////
    // Tab1 (Info)
    /////////////////////////////////////////
		FormLayout layoutInfo = new FormLayout(colLayout, rowLayout);
		JPanel panelInfo = new JPanel();
		PanelBuilder builderInfo = new PanelBuilder(panelInfo, layoutInfo);
    builderInfo.add(label, cc.xy(2, 2));
    //builderInfo.addLabel(label, cc.xy(2, 2));
    
    tabbedPane.add("Generic", panelInfo);
    
    builder.add(tabbedPane, cc.xy(2,2));
    
  }

  public String getTitle()
  {
    return "Generic";
  }
	
	public final void setLabel(String text)
	{
		if(!componentsInited)
		{
			initComponents();
			componentsInited = true;
		}
		label.setText(text);
		updateView();
	}
	
	public String getLabel()
	{
		return label.getText();
	}
	
  protected void updateView()
  {
  }
}
