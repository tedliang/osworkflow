package com.opensymphony.workflow.designer.swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * @author Gulei
 */
public class ScreenUtils
{
  public static void center(Window frame)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    frame.setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
  }

}
