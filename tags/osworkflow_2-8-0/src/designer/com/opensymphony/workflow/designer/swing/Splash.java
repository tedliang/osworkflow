package com.opensymphony.workflow.designer.swing;

import java.awt.*;

public final class Splash extends Window
{

  private static final int DEFAULT_BAR_WIDTH = 100;
  private static final int DEFAULT_BAR_HEIGHT = 10;
  private static final int VPAD = 10;

  private final Image image;
  private final boolean showProgress;
  private final String text;

  private Color textColor;
  private Rectangle progressBarBounds;
  private int percent;

  public Splash(Window owner, Image image, String text, boolean showProgress)
  {
    super(owner);
    this.image = image;
    this.text = text;
    this.percent = 0;
    this.showProgress = showProgress;
    setSize(image.getWidth(null), image.getHeight(null));
    setProgressBarBounds(VPAD);
    setForeground(Color.darkGray);
    setBackground(Color.lightGray.brighter());
    textColor = Color.black;
    ScreenUtils.center(this);
  }

  public void setProgressBarBounds(Rectangle r)
  {
    progressBarBounds = new Rectangle(r);
  }

  public void setProgressBarBounds(int bottomPad)
  {
    setProgressBarBounds(defaultProgressBarBounds(bottomPad));
  }

  private Rectangle defaultProgressBarBounds(int bottomPad)
  {
    int x = (getWidth() - DEFAULT_BAR_WIDTH) / 2;
    int y = getHeight() - DEFAULT_BAR_HEIGHT - bottomPad;
    return new Rectangle(x, y, DEFAULT_BAR_WIDTH, DEFAULT_BAR_HEIGHT);
  }

  public void paint(Graphics g)
  {
    boolean clipIsProgressRect = progressBarBounds.equals(g.getClipBounds());

    if(image != null && (!showProgress || !clipIsProgressRect))
    {
      g.drawImage(image, 0, 0, this);
    }
    if(showProgress)
    {
      int x = progressBarBounds.x;
      int y = progressBarBounds.y;
      int w = progressBarBounds.width;
      int h = progressBarBounds.height;
      int progressWidth = (w - 2) * percent / 100;
      int progressHeight = h - 2;

      g.translate(x, y);
      // Paint border
      g.setColor(Color.gray);
      g.drawLine(0, 0, w - 2, 0);
      g.drawLine(0, 0, 0, h - 1);
      g.setColor(Color.white);
      g.drawLine(0, h - 1, w - 1, h - 1);
      g.drawLine(w - 1, 0, w - 1, h - 1);
      // Paint background
      g.setColor(getBackground());
      g.fillRect(1, 1, w - 2, progressHeight);
      // Paint progress bar
      g.setColor(getForeground());
      g.fillRect(1, 1, progressWidth, progressHeight);
      g.translate(-x, -y);

      if(!clipIsProgressRect)
      {
        FontMetrics fm = getFontMetrics(g.getFont());
        int textWidth = fm.stringWidth(text);
        int textX = (getWidth() - textWidth) / 2;
        g.setColor(textColor);
        g.drawString(text, textX, progressBarBounds.y - VPAD / 2);
      }
    }
  }

  public void openSplash()
  {
    setVisible(true);
  }

  public void closeSplash()
  {
    dispose();
  }

  public void setProgress(int percent)
  {
    if(!showProgress)
      return;
    this.percent = percent;
    repaint(progressBarBounds.x, progressBarBounds.y, progressBarBounds.width, progressBarBounds.height);
  }
}