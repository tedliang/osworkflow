package com.opensymphony.workflow.designer.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.designer.swing.DefaultFormBuilder;
import com.opensymphony.workflow.designer.swing.FileField;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 25, 2003
 *         Time: 1:25:28 AM
 */
public class ImportWorkflowDialog extends JDialog implements ActionListener
{
  private JRadioButton web = new JRadioButton(ResourceManager.getString("import.web.long"));
  private JRadioButton file = new JRadioButton(ResourceManager.getString("import.file.long"));
  private FileField fileField = new FileField(JFileChooser.FILES_AND_DIRECTORIES, false, ".xml", ResourceManager.getString("descriptor.files"));
  private JTextField webField = new JTextField();
  private URL url;

  public ImportWorkflowDialog(Frame owner, String title, boolean modal) throws HeadlessException
  {
    super(owner, title, modal);
    getContentPane().setLayout(new BorderLayout());
    FormLayout layout = new FormLayout("left:max(40dlu;pref), 3dlu, 110dlu:grow, 7dlu");
    DefaultFormBuilder builder = new DefaultFormBuilder(layout, ResourceManager.getBundle());
    builder.setDefaultDialogBorder();
    builder.append(web, webField);
    builder.nextLine();
    ButtonGroup group = new ButtonGroup();
    group.add(web);
    group.add(file);
    builder.append(file, fileField);
    fileField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        file.setSelected(true);
      }
    });
    getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
    getContentPane().add(UIFactory.getTableButtonBar(this, "", new String[]{"ok", "cancel"}), BorderLayout.SOUTH);
  }

  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();
    if("ok".equals(command))
    {
      if(web.isSelected())
      {
        try
        {
          url = new URL(webField.getText());
          dispose();
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
            dispose();
            return;
          }
          url = f.toURL();
          dispose();
          return;
        }
        catch(MalformedURLException e1)
        {
          JOptionPane.showMessageDialog(this, ResourceManager.getString("import.url.invalid", new Object[]{e1.getMessage()}));
        }
      }
    }
    else
    {
      dispose();
    }
  }

  public URL getImportURL()
  {
    return url;
  }
}
