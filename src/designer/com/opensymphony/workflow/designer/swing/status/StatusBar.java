package com.opensymphony.workflow.designer.swing.status;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 24, 2003
 *         Time: 3:27:25 PM
 */
public class StatusBar extends JPanel
{
  private Border border;

  public StatusBar()
  {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setRequestFocusEnabled(false);
    setFocusable(false);
    border = BorderFactory.createEmptyBorder(1, 4, 1, 4);
  }

  protected void addImpl(Component comp, Object constraints, int index)
  {
    super.addImpl(comp, constraints, index);
    if(comp instanceof DisplayItem)
    {
      DisplayItem item = (DisplayItem)comp;
      item.setBorder(border);
    }
  }

  public DisplayItem getItemByName(String name)
  {
    Component[] components = getComponents();
    for(int i = 0; i < components.length; i++)
    {
      Component c = components[i];
      if(c instanceof DisplayItem)
      {
        DisplayItem item = (DisplayItem)c;
        if(name.equals(item.getItemName()))
          return item;
      }
    }
    return null;
  }

  public void setItemVisible(String string, boolean bool)
  {
    DisplayItem item = getItemByName(string);
    if(item != null)
      item.setVisible(bool);
    validate();
    repaint();
  }

  public int getHeight()
  {
    Component[] components = getComponents();
    int height = 0;
    for(int i = 0; i < components.length; i++)
    {
      Component c = components[i];
      if(c instanceof DisplayItem)
      {
        int itemHeight = c.getPreferredSize().height;
        if(itemHeight > height) height = itemHeight;
      }
    }
    return height + 5;
  }

  public Dimension getPreferredSize()
  {
    return new Dimension(0, getHeight());
  }

  public void removeNotify()
  {
    super.removeNotify();
    removeAll();
  }

  public static void main(String[] args) throws InterruptedException
  {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new BorderLayout());
    StatusBar bar = new StatusBar();
    StatusDisplay progress = new StatusDisplay();
    bar.add(progress);
    bar.add(Box.createHorizontalGlue());
    bar.add(new MemoryDisplay());
    frame.setSize(800, 600);
    frame.setLocation(100, 100);
    frame.getContentPane().add(bar, BorderLayout.SOUTH);
    frame.show();
    Thread.sleep(1000);
    progress.setIndeterminate(true);
    progress.setProgressStatus("blahblah");
    Thread.sleep(2000);
    progress.setStatus("This is a status");
    Thread.sleep(1000);
    progress.setIndeterminate(false);
    for(int i=0;i<100;i++)
    {
      Thread.sleep(20L);
      progress.setProgress(i);
    }
  }
}
