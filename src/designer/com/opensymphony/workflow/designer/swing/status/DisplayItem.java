package com.opensymphony.workflow.designer.swing.status;

import java.awt.*;
import java.awt.font.FontRenderContext;
import javax.swing.*;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 24, 2003
 *         Time: 3:42:19 PM
 */
public abstract class DisplayItem extends JPanel
{
  private int preferredWidth;

  public abstract String getItemName();

  public DisplayItem()
  {
    setRequestFocusEnabled(false);
    setFocusable(false);
  }

  public void setPreferredWidth(int i)
  {
    preferredWidth = i;
  }

  public int getPreferredWidth()
  {
    return preferredWidth;
  }

  private int getBarHeight()
  {
    return (int)getFont().getLineMetrics("dfghjklpqtABC", new FontRenderContext(null, true, false)).getHeight();
  }

  public Dimension getPreferredSize()
  {
    Dimension dim = new Dimension((getPreferredWidth() + getInsets().left + getInsets().right), (getBarHeight() + getInsets().top + getInsets().bottom + 4));
    return dim;
  }

 }
