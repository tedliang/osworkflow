package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.EventListenerList;

import com.opensymphony.workflow.designer.Utils;

public class FileField extends JPanel
{
  private final JTextField field = new JTextField();
  private final FixedButton button = new FixedButton(field);
  private String suffix;
  private String description;
  private final int type;
  private boolean isSave;
  private boolean buttonEnabled;
  private File file;
  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Only one <code>ActionEvent</code> is needed per field instance since the event's only state is the source property.
   * The source of events generated is always "this".
   */
  protected transient ActionEvent actionEvent;

  public void actionPerformed(ActionEvent e)
  {
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
    button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        File file = Utils.promptUserForFile(FileField.this, FileField.this.type, FileField.this.isSave,
                                            FileField.this.suffix, FileField.this.description);
        if(file != null)
        {
          field.setText(file.getAbsolutePath());
          FileField.this.file = file;
          fireStateChanged();
        }
      }
    });
  }

  /**
   * Adds an <code>ActionListener</code> to the field.
   *
   * @param l the <code>ActionListener</code> to be added
   */
  public void addActionListener(ActionListener l)
  {
    listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created.
   *
   * @see EventListenerList
   */
  protected void fireStateChanged()
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for(int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if(listeners[i] == ActionListener.class)
      {
        // Lazily create the event:
        if(actionEvent == null)
          actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "fileselected");
        ((ActionListener)listeners[i + 1]).actionPerformed(actionEvent);
      }
    }
  }

  /**
   * Removes an <code>ActionListener</code> from the field.
   *
   * @param l the listener to be removed
   */
  public void removeActionListener(ActionListener l)
  {
    listenerList.remove(ActionListener.class, l);
  }

  /**
   * Returns an array of all the <code>ActionListener</code>s added
   * to this FileField with addActionListener().
   *
   * @return all of the <code>ActionListener</code>s added or an empty
   *         array if no listeners have been added
   */
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])(listenerList.getListeners(ActionListener.class));
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
