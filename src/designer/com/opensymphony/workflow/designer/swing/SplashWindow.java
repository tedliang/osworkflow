package com.opensymphony.workflow.designer.swing;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * @author Gulei
 */
public class SplashWindow extends JWindow
{

  public void init()
  {
    ImageIcon logoIcon = ImageLoader.getIcon("splash.gif");
    JLabel lab = new Splash(logoIcon);

    getContentPane().add(lab, BorderLayout.CENTER);
    lab.setBorder(BorderFactory.createRaisedBevelBorder());
    pack();
    ScreenUtils.center(this);
  }

}
