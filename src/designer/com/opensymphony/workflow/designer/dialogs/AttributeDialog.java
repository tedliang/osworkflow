package com.opensymphony.workflow.designer.dialogs;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import com.opensymphony.workflow.designer.*;

/**
 * @author acapitani
 */
public class AttributeDialog extends BaseDialog 
{
	public JTextField keyField;
	public JTextField valueField;
 	
	public AttributeDialog(Frame owner, String attrName, String attrValue, boolean IsNew) throws HeadlessException
	{
		super(owner, ResourceManager.getString("attribute.edit"), true);
		
		getBanner().setTitle(ResourceManager.getString("attribute.dialog.title"));
		getBanner().setSubtitle(ResourceManager.getString("attribute.dialog.subtitle"));
		DefaultFormBuilder builder = UIFactory.getDialogBuilder(null, getContentPane());
		
		if (IsNew)
		{
			keyField = new JTextField();
			valueField = new JTextField();
			keyField.requestDefaultFocus(); 
		}
		else
		{
			keyField = UIFactory.createReadOnlyTextField();
			valueField = new JTextField(); 
			keyField.setText(attrName);
			valueField.setText(attrValue);
		}
		builder.append(ResourceManager.getString("attribute.key.long"), keyField);
		builder.append(ResourceManager.getString("attribute.value.long"), valueField);
	
		builder.appendRow(builder.getLineGapSpec());
		builder.nextLine();
	}
}
