package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import com.opensymphony.workflow.designer.UIFactory;

public class BannerPanel extends JPanel
{
  private JLabel titleLabel;
  private JTextArea subtitleLabel;
  private JLabel iconLabel;

  public BannerPanel()
  {
    setBorder(new CompoundBorder(new EtchedBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)));

    setOpaque(true);
    setBackground(UIManager.getColor("Table.background"));

    titleLabel = new JLabel();
    titleLabel.setOpaque(false);

    subtitleLabel = new JTextArea("<html>");
    subtitleLabel.setFont(titleLabel.getFont());
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));

    subtitleLabel.setWrapStyleWord(true);
    subtitleLabel.setEditable(false);
    subtitleLabel.setOpaque(false);
    subtitleLabel.setLineWrap(true);

    UIFactory.htmlize(subtitleLabel);

    iconLabel = new JLabel();
    iconLabel.setPreferredSize(new Dimension(50, 50));

    setLayout(new BorderLayout());

    JPanel nestedPane = new JPanel(new BorderLayout());
    nestedPane.setOpaque(false);
    nestedPane.add(titleLabel, BorderLayout.NORTH);
    nestedPane.add(subtitleLabel, BorderLayout.CENTER);
    add(nestedPane, BorderLayout.CENTER);
    add(iconLabel, BorderLayout.EAST);
  }

  public void setTitleColor(Color color)
  {
    titleLabel.setForeground(color);
  }

  public void setSubtitleColor(Color color)
  {
    subtitleLabel.setForeground(color);
  }

  public void setTitle(String title)
  {
    titleLabel.setText(title);
  }

  public void setSubtitle(String subtitle)
  {
    subtitleLabel.setText(subtitle);
  }

  public void setIcon(Icon icon)
  {
    iconLabel.setIcon(icon);
  }
}
