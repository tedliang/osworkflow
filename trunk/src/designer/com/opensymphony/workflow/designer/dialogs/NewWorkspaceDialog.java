package com.opensymphony.workflow.designer.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.opensymphony.workflow.designer.swing.FileField;
import com.opensymphony.workflow.designer.*;
import com.opensymphony.workflow.loader.Workspace;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 28, 2003
 *         Time: 4:39:11 PM
 */
public class NewWorkspaceDialog extends BaseDialog
{
  private JRadioButton create = new JRadioButton(ResourceManager.getString("workspace.create"));
  private JRadioButton load = new JRadioButton(ResourceManager.getString("workspace.load"));
  private FileField loadField = new FileField(JFileChooser.FILES_AND_DIRECTORIES, false, WorkflowDesigner.WORKSPACE_SUFFIX, ResourceManager.getString("workspace.files"));
  private FileField createField = new FileField(JFileChooser.FILES_AND_DIRECTORIES, true, WorkflowDesigner.WORKSPACE_SUFFIX, ResourceManager.getString("workspace.files"));

  public NewWorkspaceDialog(Frame owner, String title, boolean modal) throws HeadlessException
  {
    super(owner, title, modal);
    //getContentPane().setLayout(new BorderLayout());
    FormLayout layout = new FormLayout("2dlu, left:max(40dlu;pref), 3dlu, 110dlu:grow, 7dlu");
    DefaultFormBuilder builder = new DefaultFormBuilder(layout, ResourceManager.getBundle());
    builder.setLeadingColumnOffset(1);

    builder.appendI15dSeparator("workspace.createload");
    builder.append(load, loadField);
    loadField.addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent e)
      {
        load.setSelected(true);
      }
    });
    loadField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        load.setSelected(true);
      }
    });
    builder.nextLine();
    ButtonGroup group = new ButtonGroup();
    group.add(create);
    group.add(load);
    builder.append(create, createField);
    builder.nextLine();
    createField.getTextField().addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent e)
      {
        create.setSelected(true);
      }
    });
    createField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        create.setSelected(true);
      }
    });

    getContentPane().add(builder.getPanel());
  }

	public void ok()
	{
		if(load.isSelected())
		{
		  File file = loadField.getFile();
		  if(file==null || file.isDirectory())
		  {
		    JOptionPane.showMessageDialog(this, ResourceManager.getString("error.file.invalid"), ResourceManager.getString("error"), JOptionPane.ERROR_MESSAGE);
		    return;
		  }
			try
			{
				WorkflowDesigner.INSTANCE.openWorkspace(loadField.getFile().toURL());
			}
			catch(MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
		  File file = createField.getFile();
		  if(file == null || file.isDirectory())
		  {
		    JOptionPane.showMessageDialog(this, ResourceManager.getString("error.file.invalid"), ResourceManager.getString("error"), JOptionPane.ERROR_MESSAGE);
		    return;
		  }
		  Workspace space = WorkflowDesigner.INSTANCE.newLocalWorkspace();
		  space.setLocation(file);
		  Prefs.INSTANCE.put(Prefs.LAST_WORKSPACE, file.toString());
		  WorkflowDesigner.INSTANCE.navigator().setWorkspace(space);
		}
		super.ok();
	}
}
