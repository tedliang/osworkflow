package com.opensymphony.workflow.designer.swing;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class FramePanel extends JPanel
{
  private JLabel titleLabel;
  private GradientPanel gradientPanel;
  private JPanel headerPanel;
  private boolean isSelected;
  private boolean closeable = false;

  /**
   * Constructs a <code>FramePanel</code> for the specified
   * icon, title, tool bar, and content panel.
   */
  public FramePanel(Icon frameIcon, String title, JToolBar bar, JComponent content, boolean closeable)
  {
    super(new BorderLayout());
    this.isSelected = false;
    this.closeable = closeable;
    this.titleLabel = new JLabel(title, frameIcon, SwingConstants.LEADING);
    JPanel top = buildHeader(titleLabel, bar);

    add(top, BorderLayout.NORTH);
    if(content != null)
    {
      setContent(content);
    }
    setBorder(new ShadowBorder());
    setSelected(true);
    updateHeader();
  }

  /**
   * Constructs a <code>FramePanel</code> for the specified
   * title, tool bar, and content panel.
   */
  public FramePanel(String title, JToolBar bar, JComponent c, boolean closeable)
  {
    this(null, title, bar, c, closeable);
  }

  /**
   * Constructs a <code>FramePanel</code> for the specified
   * icon, and title.
   */
  public FramePanel(Icon icon, String title, boolean closeable)
  {
    this(icon, title, null, null, closeable);
  }

  /**
   * Constructs a <code>FramePanel</code> for the specified title.
   */
  public FramePanel(String title, boolean closeable)
  {
    this(null, title, null, null, closeable);
  }


  public Icon getFrameIcon()
  {
    return titleLabel.getIcon();
  }

  public void setFrameIcon(Icon icon)
  {
    titleLabel.setIcon(icon);
  }

  public String getTitle()
  {
    return titleLabel.getText();
  }

  public void setTitle(String text)
  {
    titleLabel.setText(text);
  }

  public void setToolBar(JToolBar bar)
  {
    if(bar != null)
    {
      bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      headerPanel.add(bar, BorderLayout.EAST);
    }
  }

  public Component getContent()
  {
    return hasContent() ? getComponent(1) : null;
  }

  /**
   * Set the component to show in the panel.
   */
  public void setContent(Component content)
  {
    if(hasContent())
    {
      remove(getContent());
    }
    add(content, BorderLayout.CENTER);
  }

  public boolean isSelected()
  {
    return isSelected;
  }

  public void setSelected(boolean selected)
  {
    isSelected = selected;
    updateHeader();
  }

  private JPanel buildHeader(JLabel label, JToolBar bar)
  {
    gradientPanel = new GradientPanel(new BorderLayout(), getHeaderBackground());
    label.setOpaque(false);

    gradientPanel.add(label, BorderLayout.WEST);
    gradientPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 1));

    headerPanel = new JPanel(new BorderLayout());
    headerPanel.add(gradientPanel, BorderLayout.CENTER);
    setToolBar(bar);
    headerPanel.setBorder(new RaisedHeaderBorder());
    headerPanel.setOpaque(false);
    return headerPanel;
  }

  private void updateHeader()
  {
    gradientPanel.setBackground(getHeaderBackground());
    gradientPanel.setOpaque(isSelected());
    if(closeable)
    {
      JButton button = new JButton(new ImageIcon(getClass().getResource("/images/close.gif")));
      button.setBorderPainted(false);
      button.setOpaque(false);
      button.setMargin(new Insets(0, 0, 0, 0));
      button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          close();
        }
      });
      gradientPanel.add(button, BorderLayout.EAST);
    }
    titleLabel.setForeground(getTextForeground(isSelected()));
    headerPanel.repaint();
  };

  public void close()
  {
    if(closeable)
    {
      JComponent parent = (JComponent)getParent();
      //parent.remove(SimpleInternalFrame.this);
      setVisible(false);
      parent.revalidate();
    }
  }

  public void updateUI()
  {
    super.updateUI();
    if(titleLabel != null)
    {
      updateHeader();
    }
  }

  private boolean hasContent()
  {
    return getComponentCount() > 1;
  }

  protected Color getTextForeground(boolean selected)
  {
    Color c = UIManager.getColor(selected ? "FramePanel.activeTitleForeground" : "FramePanel.inactiveTitleForeground");
    if(c != null)
    {
      return c;
    }
    return UIManager.getColor(selected ? "InternalFrame.activeTitleForeground" : "Label.foreground");
  }

  protected Color getHeaderBackground()
  {
    Color c = UIManager.getColor("FramePanel.activeTitleBackground");
    if(c != null)
      return c;
    return UIManager.getColor("InternalFrame.activeTitleBackground");
  }


  private static class RaisedHeaderBorder extends AbstractBorder
  {
    private static final Insets INSETS = new Insets(1, 1, 1, 0);

    public Insets getBorderInsets(Component c)
    {
      return INSETS;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
      g.translate(x, y);
      g.setColor(UIManager.getColor("controlLtHighlight"));
      g.fillRect(0, 0, w, 1);
      g.fillRect(0, 1, 1, h - 1);
      g.setColor(UIManager.getColor("controlShadow"));
      g.fillRect(0, h - 1, w, h);
      g.translate(-x, -y);
    }
  }

  private static class ShadowBorder extends AbstractBorder
  {
    private static final Insets INSETS = new Insets(1, 1, 3, 3);

    public Insets getBorderInsets(Component c)
    {
      return INSETS;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {

      Color shadow = UIManager.getColor("controlShadow");
      Color lightShadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 170);
      Color lighterShadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 70);
      g.translate(x, y);

      g.setColor(shadow);
      g.fillRect(0, 0, w - 3, 1);
      g.fillRect(0, 0, 1, h - 3);
      g.fillRect(w - 3, 1, 1, h - 3);
      g.fillRect(1, h - 3, w - 3, 1);
      // Shadow line 1
      g.setColor(lightShadow);
      g.fillRect(w - 3, 0, 1, 1);
      g.fillRect(0, h - 3, 1, 1);
      g.fillRect(w - 2, 1, 1, h - 3);
      g.fillRect(1, h - 2, w - 3, 1);
      // Shadow line2
      g.setColor(lighterShadow);
      g.fillRect(w - 2, 0, 1, 1);
      g.fillRect(0, h - 2, 1, 1);
      g.fillRect(w - 2, h - 2, 1, 1);
      g.fillRect(w - 1, 1, 1, h - 2);
      g.fillRect(1, h - 1, w - 2, 1);
      g.translate(-x, -y);
    }
  }

  private static class GradientPanel extends JPanel
  {
    private GradientPanel(LayoutManager lm, Color background)
    {
      super(lm);
      setBackground(background);
    }

    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      if(!isOpaque())
      {
        return;
      }
      Color control = UIManager.getColor("control");
      int width = getWidth();
      int height = getHeight();

      Graphics2D g2 = (Graphics2D)g;
      Paint storedPaint = g2.getPaint();
      g2.setPaint(new GradientPaint(0, 0, getBackground(), width, 0, control));
      g2.fillRect(0, 0, width, height);
      g2.setPaint(storedPaint);
    }
  }

}