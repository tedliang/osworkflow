package com.opensymphony.workflow.designer.swing.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class BlueButtonUI extends BasicButtonUI
{
	private Color blueishBackground = new Color(220, 225, 234);
	private Color blueishBorder = new Color(10, 36, 106);

	public BlueButtonUI()
	{
		super();
	}

	public void installUI(JComponent c)
	{
		super.installUI(c);

		AbstractButton button = (AbstractButton)c;
		button.setRolloverEnabled(true);
		button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    if(c.getClientProperty("showtext") != Boolean.TRUE)
  		button.setText(null);
	}

	public void paint(Graphics g, JComponent c)
	{
		AbstractButton button = (AbstractButton)c;
		if(button.getModel().isRollover() || button.getModel().isArmed() || button.getModel().isSelected())
		{
			Color oldColor = g.getColor();
			g.setColor(blueishBackground);
			g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

			g.setColor(blueishBorder);
			g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

			g.setColor(oldColor);
		}
		super.paint(g, c);
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b)
	{
		setTextShiftOffset();
	}

	protected int getTextShiftOffset()
	{
		return 1;
	}
}
