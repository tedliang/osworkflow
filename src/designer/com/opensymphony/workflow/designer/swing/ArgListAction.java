/*
 * Created on 22-dic-2004
 *
 */
package com.opensymphony.workflow.designer.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import com.opensymphony.workflow.designer.dialogs.DialogUtils;
import com.opensymphony.workflow.loader.ArgType;

/**
 * @author acapitani
 *
 */
public class ArgListAction implements ActionListener 
{
	protected JButton command = null;
	protected JTextField edit = null;
	protected ArgType argType = null;
	
  public ArgListAction(JButton command, 
  												JTextField edit, 
  												ArgType argType)
  {
  	this.command = command;
  	this.edit = edit;
  	this.argType = argType;
  }
  
  public void actionPerformed(ActionEvent arg0) 
  {
 		if (argType!=null)
 		{
 			List args = argType.getArgList();   	
			
			String[] aArgs = new String[args.size()];
			args.toArray(aArgs);
			String sValue = (String)DialogUtils.getUserSelection(aArgs, "messaggio", "titolo", this.command);
 			if (sValue!=null)
 				this.edit.setText(sValue);
 		}
  }
}
