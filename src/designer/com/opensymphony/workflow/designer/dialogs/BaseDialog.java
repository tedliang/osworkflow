package com.opensymphony.workflow.designer.dialogs;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.opensymphony.workflow.designer.swing.BannerPanel;
import com.opensymphony.workflow.designer.Utils;
import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Jan 11, 2004
 *         Time: 5:28:16 PM
 */
public class BaseDialog extends JDialog
{
  private BannerPanel banner;
  private JPanel contentPane;
  private boolean cancelClicked;
  public final static Border WINDOW_BORDER = BorderFactory.createEmptyBorder(4, 10, 10, 10);

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
  private static final Border CONTENT_BORDER = BorderFactory.createEmptyBorder(3, 3, 3, 3);

  public BaseDialog() throws HeadlessException
  {
    super();
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

  /**
   * Returns true if OK was clicked, false if CANCEL or CLOSE was clicked
   */
  public boolean ask(Component parent)
  {
    show(parent);
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
    JPanel container = (JPanel)super.getContentPane();
    container.setLayout(new BorderLayout(0, 0));
    banner = new BannerPanel();
    container.add(banner, BorderLayout.NORTH);

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(WINDOW_BORDER);
    mainPanel.setLayout(new BorderLayout());
    container.add(mainPanel, BorderLayout.CENTER);

    contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout(3, 3));
    contentPane.setBorder(CONTENT_BORDER);
    mainPanel.add(contentPane, BorderLayout.CENTER);

    JButton ok = new JButton(ResourceManager.getString("ok"));
    ok.setDefaultCapable(true);
    getRootPane().setDefaultButton(ok);
    ok.addActionListener(okAction);
    JButton cancel = new JButton(ResourceManager.getString("cancel"));
    cancel.addActionListener(cancelOrCloseAction);
    mainPanel.add(ButtonBarFactory.buildOKCancelBar(ok, cancel), BorderLayout.SOUTH);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    container.getActionMap().put("cancel", cancelOrCloseAction);
    container.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        cancel();
      }
    });
  }

	/**
	 * Show the dialog.
	 * This method will pack the dialog and center it
	 * relative to the specified parent.
	 * @param parent
	 */
	public void show(Component parent)
	{
		pack();
    if(parent == null)
    {
      Utils.centerComponent(this);
    }
    else
    {
  		Utils.centerComponent(parent, this);
    }
		super.show();
	}

}
