package com.opensymphony.workflow.designer.swing.status;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.opensymphony.workflow.designer.ResourceManager;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 24, 2003
 *         Time: 5:05:32 PM
 */
public class StatusDisplay extends DisplayItem
{
  private JLabel status;
  private JLabel progressStatus;
  private JProgressBar progress;
  private JButton cancel;
  private CancelListener cancelListener;

  public static interface CancelListener
  {
    public void cancelPerformed();
  }

  public StatusDisplay()
  {
    setLayout(new CardLayout());
    add(createStatus(), "Status");
    add(createProgressBar(), "Progress");
    showStatus();
  }

  public void showStatus()
  {
    ((CardLayout)getLayout()).show(this, "Status");
  }

  public void showProgress()
  {
    ((CardLayout)getLayout()).show(this, "Progress");
  }

  private Component createProgressBar()
  {
    JPanel mainPanel = new JPanel();
    mainPanel.setOpaque(false);
    cancelListener = null;
    mainPanel.setLayout(new BorderLayout(3, 0));
    progressStatus = new JLabel("");
    progressStatus.setVerticalAlignment(1);
    progressStatus.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
    progress = new JProgressBar();
    progress.setMaximum(100);
    cancel = new JButton(ResourceManager.getString("cancel"));
    cancel.setOpaque(false);
    cancel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
    cancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent actionevent)
      {
        if(StatusDisplay.this.cancelListener != null)
          StatusDisplay.this.cancelListener.cancelPerformed();
      }
    });
    cancel.setRequestFocusEnabled(false);
    cancel.setFocusable(false);
    JPanel barPanel = new JPanel(new BorderLayout(0, 0));
    barPanel.setOpaque(false);
    barPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
    barPanel.add(progress, BorderLayout.CENTER);
    JPanel progressPanel = new JPanel(new BorderLayout(3, 0));
    progressPanel.setOpaque(false);
    progressPanel.add(progressStatus, BorderLayout.LINE_START);
    progressPanel.add(barPanel, BorderLayout.CENTER);
    mainPanel.add(progressPanel, BorderLayout.CENTER);
    mainPanel.add(cancel, BorderLayout.LINE_END);
    setCancelListener(null);
    return mainPanel;
  }

  private Component createStatus()
  {
    status = new JLabel();
    status.setVerticalAlignment(1);
    status.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
    return status;
  }

  public void setStatus(final String string)
  {
    if(SwingUtilities.isEventDispatchThread())
    {
      status.setText(string);
      showStatus();
      return;
    }
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        StatusDisplay.this.status.setText(string);
        showStatus();
      }
    };
    SwingUtilities.invokeLater(runnable);
  }

  public void setStatusIcon(final Icon icon)
  {
    if(SwingUtilities.isEventDispatchThread())
    {
      status.setIcon(icon);
      showStatus();
      return;
    }
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        StatusDisplay.this.status.setIcon(icon);
        showStatus();
      }
    };
    SwingUtilities.invokeLater(runnable);
  }

  public void setProgressStatus(final String string)
  {
    if(SwingUtilities.isEventDispatchThread())
    {
      showProgress();
      progressStatus.setText(string);
      return;
    }
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        showProgress();
        StatusDisplay.this.progressStatus.setText(string);
      }
    };
    SwingUtilities.invokeLater(runnable);
  }

  /**
   * @param amount A value between 0 and 100 (inclusive)
   */
  public void setProgress(final int amount)
  {
    if(SwingUtilities.isEventDispatchThread())
    {
      showProgress();
      progress.setValue(amount);
      return;
    }
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        showProgress();
        StatusDisplay.this.progress.setValue(amount);
      }
    };
    SwingUtilities.invokeLater(runnable);
  }


  public void setCancelListener(CancelListener c)
  {
    if(c == null)
    {
      cancel.setVisible(false);
    }
    else
    {
      cancel.setVisible(true);
    }
    cancelListener = c;
  }

  public String getItemName()
  {
    return "Status";
  }

  public Dimension getPreferredSize()
  {
    return new Dimension(200, super.getPreferredSize().height);
  }

  public void setIndeterminate(boolean bool)
  {
    progress.setIndeterminate(bool);
  }

  public void setProgressBarWidth(int i)
  {
    progress.setMaximumSize(new Dimension(i, progress.getPreferredSize().height));
  }
}
