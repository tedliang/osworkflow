package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.ResourceManager;

public class FixedButton extends JButton
{
  private final int length;
  private final Component attachedComponent;

  private FixedButton(int i, Component c)
  {
    super(ResourceManager.getIcon("ellipsis"));
    length = i;
    attachedComponent = c;
    setMargin(new Insets(0, 0, 0, 0));
    setDefaultCapable(false);
    setFocusable(false);
  }

  public FixedButton(int i)
  {
    this(i, null);
    if(i <= 0)
      throw new IllegalArgumentException("wrong size: " + i);
    else
      return;
  }

  public FixedButton(JComponent jcomponent)
  {
    this(-1, jcomponent);
    if(jcomponent == null)
      throw new IllegalArgumentException("component cannot be null");
    else
      return;
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public Dimension getMaximumSize()
  {
    return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    if(attachedComponent != null)
    {
      int i = attachedComponent.getPreferredSize().height;
      return new Dimension(i, i);
    }
    return new Dimension(length, length);
  }

  public Component getAttachedComponent()
  {
    return attachedComponent;
  }
}
