package com.opensymphony.workflow.designer;

import java.awt.*;
import javax.swing.*;

import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
import com.opensymphony.workflow.designer.swing.Splash;

/**
 * @author hani Date: Apr 25, 2004 Time: 2:17:41 AM
 */
public class Launcher
{
  public static void main(String[] args)
  {
    // test - simulate System properties via JNLP
    if(args.length > 0)
    {
      if(args[0].equals("testnew"))
      {
        System.getProperties().put("com.opensymphony.workflow.jws.verb", "new");
        System.getProperties().put("com.opensymphony.workflow.jws.service", "http://localhost:8000/workflow");
        System.getProperties().put("com.opensymphony.workflow.jws.workspace", "DocWay");
      }
      else if(args[0].equals("testmodify"))
      {
        System.getProperties().put("com.opensymphony.workflow.jws.verb", "modify");
        System.getProperties().put("com.opensymphony.workflow.jws.service", "http://localhost:8080/workflow");
        System.getProperties().put("com.opensymphony.workflow.jws.workspace", "DocWay");
        System.getProperties().put("com.opensymphony.workflow.jws.id_1", "MD00000008");
        System.getProperties().put("com.opensymphony.workflow.jws.name_1", "test doc");
      }
    }

    System.getProperties().put("apple.laf.useScreenMenuBar", "true");
    System.getProperties().put("com.apple.mrj.application.apple.menu.about.name", "OSWorkflow Designer");
    String spec = System.getProperty("java.specification.version");
    if(spec.startsWith("1.3") || spec.startsWith("1.2") || spec.startsWith("1.1"))
    {
      System.out.println("Workflow Designer requires JDK 1.4.0 or higher");
      System.exit(1);
    }
    Splash splash = new Splash(new Frame(), new ImageIcon(Splash.class.getResource("/images/splash.gif")).getImage(), "OSWorkflow Designer", true);
    splash.openSplash();
    splash.setProgress(10);

    if(LookUtils.class.getClassLoader() != null)
    {
      UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
    }
    Options.setGlobalFontSizeHints(com.jgoodies.plaf.FontSizeHints.MIXED);
    Options.setDefaultIconSize(new Dimension(18, 18));
    Options.setUseNarrowButtons(true);
    UIManager.put(com.jgoodies.plaf.Options.DEFAULT_ICON_SIZE_KEY, new Dimension(18, 18));
    if(LookUtils.IS_OS_WINDOWS_MODERN)
    {
      try
      {
        UIManager.setLookAndFeel((LookAndFeel)Class.forName("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel", true, com.jgoodies.plaf.windows.ExtWindowsLookAndFeel.class.getClassLoader()).newInstance());
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    //all other platforms except for OSX get the plastic LAF
    else
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
      }
    }

    splash.setProgress(20);

    WorkflowDesigner d = new WorkflowDesigner(splash);
    splash.setProgress(80);
    d.pack();
    splash.setProgress(90);
    d.show();
    splash.setProgress(100);
    splash.closeSplash();
    d.checkWorkspaceExists();
  }

}
