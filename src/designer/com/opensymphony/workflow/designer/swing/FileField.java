package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

import com.opensymphony.workflow.designer.Utils;

public class FileField extends JPanel implements ActionListener
{
  private final JTextField field = new JTextField();
  private final FixedButton button = new FixedButton(field);
  private String suffix;
  private String description;
  private int type;
  private boolean isSave;
  private boolean buttonEnabled;
  private File file;

  public void actionPerformed(ActionEvent e)
  {
    File file = Utils.promptUserForFile(this, type, isSave, suffix, description);
    //todo really need a way to notify container that we've done selecting a file
    if(file != null)
    {
      field.setText(file.getAbsolutePath());
      this.file = file;
    }
  }

  public FileField(int type, boolean save, String suffix, String description)
  {
    super(new BorderLayout());
    this.type = type;
    this.isSave = save;
    this.suffix = suffix;
    this.description = description;
    buttonEnabled = true;
    add(field, BorderLayout.CENTER);
    add(button, BorderLayout.EAST);
    button.addActionListener(this);
  }

  public void setButtonIcon(Icon icon)
  {
    button.setIcon(icon);
  }

  public void setButtonEnabled(boolean flag)
  {
    buttonEnabled = flag;
    setEnabled(isEnabled());
  }

  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    button.setEnabled(enabled && buttonEnabled);
    field.setEnabled(enabled);
  }

  public void addActionListener(ActionListener actionlistener)
  {
    button.addActionListener(actionlistener);
  }

  public FixedButton getButton()
  {
    return button;
  }

  public JTextField getTextField()
  {
    return field;
  }

  public String getText()
  {
    return field.getText().trim();
  }

  public void setText(String s)
  {
    field.setText(s);
  }

  public File getFile()
  {
    return file;
  }

  public void setTextFieldPreferredWidth(int i)
  {
    Dimension dimension = field.getPreferredSize();
    FontMetrics fontmetrics = field.getFontMetrics(field.getFont());
    dimension.width = fontmetrics.charWidth('a') * i;
    field.setPreferredSize(dimension);
  }

  public boolean isEditable()
  {
    return field.isEditable();
  }

  public void setEditable(boolean flag)
  {
    field.setEditable(flag);
  }
}
