package com.opensymphony.workflow.designer.swing.status;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.swing.plaf.BlueButtonUI;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: Dec 24, 2003
 *         Time: 4:07:15 PM
 */
public class MemoryDisplay extends DisplayItem
{
  private static final Color edgeColor = new Color(82, 115, 214);
  private static final Color centerColor = new Color(180, 200, 230);
  private final Icon gcIcon = ResourceManager.getIcon("gc");
  private MemoryPanel panel;
  private JButton invokeGC;
  private int updateInterval = 2000;
  private Timer timer;

  class MemoryPanel extends JPanel
  {
    public void paint(Graphics g)
    {
      super.paint(g);
      Dimension dimension = getSize();
      Insets insets = getInsets();
      int left = insets.left;
      int top = insets.top;
      Runtime runtime = Runtime.getRuntime();
      long freeMemory = runtime.freeMemory();
      long totalMemory = runtime.totalMemory();
      int insideWidth = dimension.width - (insets.left + insets.right);
      int usedAmount = insideWidth - (int)(((long)insideWidth * freeMemory) / totalMemory);
      int insideHeight = dimension.height - (insets.bottom + insets.top);
      Graphics2D g2 = (Graphics2D)g;
      g2.setPaint(new GradientPaint(left, top, edgeColor, left, insideHeight / 2, centerColor, true));
      g.fillRect(left, top, usedAmount, insideHeight);
      g.setColor(getBackground());
      g.fillRect(left + usedAmount, top, insideWidth - usedAmount, insideHeight);
      g.setFont(getFont());
      g.setColor(Color.black);
      String memory = (Long.toString((totalMemory - freeMemory) / 1048576L) + "M of " + Long.toString(totalMemory / 1048576L) + 'M');
      FontMetrics fontmetrics = g.getFontMetrics();
      int stringWidth = fontmetrics.charsWidth(memory.toCharArray(), 0, memory.length());
      int stringHeight = fontmetrics.getHeight() - fontmetrics.getDescent();
      g.drawString(memory, left + (insideWidth - stringWidth) / 2, top + (insideHeight + stringHeight) / 2);
    }
  }

  public MemoryDisplay()
  {
    super();
    setLayout(new BorderLayout(5, 0));
    panel = new MemoryPanel();
    panel.setOpaque(false);
    invokeGC = new JButton(gcIcon);
    invokeGC.setRequestFocusEnabled(false);
    invokeGC.setFocusable(false);
    setBorder(new EmptyBorder(1, 1, 1, 1));
    invokeGC.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    invokeGC.setOpaque(false);
	  invokeGC.setUI(new BlueButtonUI());
    invokeGC.addActionListener(new ActionListener()
    {
	    public void actionPerformed(ActionEvent e)
	    {
		    System.gc();
		    MemoryDisplay.this.panel.repaint();
	    }
    });
    add(panel, BorderLayout.CENTER);
    add(invokeGC, BorderLayout.WEST);
    timer = new Timer(updateInterval, new ActionListener()
    {
      public void actionPerformed(ActionEvent actionevent)
      {
        MemoryDisplay.this.panel.repaint();
      }
    });
    timer.start();
  }

  public int getPreferredWidth()
  {
    return 30 + getFontMetrics(getFont()).stringWidth("0000M of 0000M");
  }

  public String getItemName()
  {
    return "Memory";
  }

  public void setUpdateInterval(int i)
  {
    timer.stop();
    updateInterval = i;
    timer.setDelay(i);
    timer.start();
  }

  public int getUpdateInterval()
  {
    return updateInterval;
  }

  public void removeNotify()
  {
    super.removeNotify();
    stop();
    timer = null;
  }

  public void stop()
  {
    timer.stop();
  }

 }
