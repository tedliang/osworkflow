package com.opensymphony.workflow.designer.dialogs;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import com.opensymphony.workflow.designer.swing.BannerPanel;
import com.opensymphony.workflow.designer.ResourceManager;
import com.jgoodies.forms.factories.ButtonBarFactory;

public class BaseDialog extends JDialog
{
  private BannerPanel banner;
  private JPanel contentPane;
  private boolean cancelClicked;

  private Action okAction = new AbstractAction()
  {
    public void actionPerformed(ActionEvent e)
    {
      ok();
    }
  };

  private Action cancelOrCloseAction = new AbstractAction()
  {
    public void actionPerformed(ActionEvent e)
    {
      cancel();
    }
  };

  public BaseDialog() throws HeadlessException
  {
    super();
    buildUI();
  }

  public BaseDialog(Dialog owner) throws HeadlessException
  {
    super(owner);
    buildUI();
  }

  public BaseDialog(Dialog owner, boolean modal) throws HeadlessException
  {
    super(owner, modal);
    buildUI();
  }

  public BaseDialog(Frame owner) throws HeadlessException
  {
    super(owner);
    buildUI();
  }

  public BaseDialog(Frame owner, boolean modal) throws HeadlessException
  {
    super(owner, modal);
    buildUI();
  }

  public BaseDialog(Dialog owner, String title) throws HeadlessException
  {
    super(owner, title);
    buildUI();
  }

  public BaseDialog(Dialog owner, String title, boolean modal) throws HeadlessException
  {
    super(owner, title, modal);
    buildUI();
  }

  public BaseDialog(Frame owner, String title) throws HeadlessException
  {
    super(owner, title);
    buildUI();
  }

  public BaseDialog(Frame owner, String title, boolean modal) throws HeadlessException
  {
    super(owner, title, modal);
    buildUI();
  }

  public final BannerPanel getBanner()
  {
    return banner;
  }

  public final Container getContentPane()
  {
    return contentPane;
  }

  /**
   * Returns true if OK was clicked, false if CANCEL or CLOSE was clicked
   */
  public boolean ask()
  {
    show();
    return !cancelClicked;
  }

  protected void ok()
  {
    cancelClicked = false;
    setVisible(false);
  }

  protected void cancel()
  {
    cancelClicked = true;
    setVisible(false);
  }

  private void buildUI()
  {
    Container container = super.getContentPane();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

    banner = new BannerPanel();
    container.add(banner);

    contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout(3, 3));
    contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    container.add(contentPane);

    container.add(new JSeparator(JSeparator.HORIZONTAL));
    JButton ok = new JButton(ResourceManager.getString("ok"));
    ok.setDefaultCapable(true);
    getRootPane().setDefaultButton(ok);
    ok.addActionListener(okAction);
    JButton cancel = new JButton(ResourceManager.getString("cancel"));
    cancel.addActionListener(cancelOrCloseAction);
    container.add(ButtonBarFactory.buildOKCancelBar(ok, cancel));

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    ((JComponent)container).registerKeyboardAction(cancelOrCloseAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        cancel();
      }
    });
  }

}
