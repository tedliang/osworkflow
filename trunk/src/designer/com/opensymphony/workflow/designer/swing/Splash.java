package com.opensymphony.workflow.designer.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * @author Gulei
 */
public class Splash extends JLabel
{
  private ImageIcon image;
  private static final Font font = new Font("Arial", Font.BOLD, 10);

  public Splash(ImageIcon image)
  {
    super(image);
    this.image = image;
  }

  public void paint(Graphics g)
  {
    super.paint(g);

    int w = image.getIconWidth();
    int h = image.getIconHeight();
    String version = "OSWorkflow Designer v 0.1";
    String copyright = "(c)" + Calendar.getInstance().get(Calendar.YEAR) + " OpenSymphony";

    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Rectangle2D rect = g2.getFontMetrics().getStringBounds(copyright, g2);
    g2.setColor(Color.BLACK);
    g2.setFont(font);
    g2.drawString(copyright, w - (int)rect.getWidth() - 5, h - 4);

    rect = g2.getFontMetrics().getStringBounds(version, g2);
    g2.drawString(version, w - (int)rect.getWidth() - 5, h - 22);
  }

}
