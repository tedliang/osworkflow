package com.opensymphony.workflow.designer.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.swing.FileField;
import com.jgoodies.forms.builder.DefaultFormBuilder;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 25, 2003
 *         Time: 1:25:28 AM
 */
public class ImportWorkflowDialog extends BaseDialog
{
  private JRadioButton web = new JRadioButton(ResourceManager.getString("import.web.long"));
  private JRadioButton file = new JRadioButton(ResourceManager.getString("import.file.long"));
  private FileField fileField = new FileField(JFileChooser.FILES_AND_DIRECTORIES, false, ".xml", ResourceManager.getString("descriptor.files"));
  private JTextField webField = new JTextField();
  private URL url;

  public ImportWorkflowDialog(Frame owner, String title, boolean modal) throws HeadlessException
  {
    super(owner, title, modal);
    DefaultFormBuilder builder = UIFactory.getDialogBuilder(null, getContentPane());
    builder.append(web, webField);
	  webField.addFocusListener(new FocusAdapter()
	  {
		  public void focusGained(FocusEvent e)
		  {
			  web.setSelected(true);
		  }
	  });
    builder.nextLine();
    ButtonGroup group = new ButtonGroup();
    group.add(web);
    group.add(file);
    builder.append(file, fileField);
	  fileField.getTextField().addFocusListener(new FocusAdapter()
	  {
		  public void focusGained(FocusEvent e)
		  {
			  file.setSelected(true);
		  }
	  });
    fileField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        file.setSelected(true);
      }
    });
  }

	public void ok()
	{
		if(web.isSelected())
		{
		  try
		  {
		    url = new URL(webField.getText());
		    super.ok();
			  return;
		  }
		  catch(MalformedURLException e1)
		  {
		    JOptionPane.showMessageDialog(this, ResourceManager.getString("import.url.invalid", new Object[]{e1.getMessage()}));
		  }
		}
		else if(file.isSelected())
		{
		  try
		  {
		    File f = new File(fileField.getText());
		    if(!f.exists() || f.isDirectory())
		    {
		      JOptionPane.showMessageDialog(this, ResourceManager.getString("import.url.file.invalid"));
		      return;
		    }
		    url = f.toURL();
			  super.ok();
				return;
		  }
		  catch(MalformedURLException e1)
		  {
		    JOptionPane.showMessageDialog(this, ResourceManager.getString("import.url.invalid", new Object[]{e1.getMessage()}),
		      ResourceManager.getString("error"), JOptionPane.ERROR_MESSAGE);
		  }
		}
	}

  public URL getImportURL()
  {
    return url;
  }
}
