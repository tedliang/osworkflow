package com.opensymphony.workflow.designer.swing;

import java.awt.Component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public final class EmptyBorderSplitPane extends JSplitPane
{
  private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

  public EmptyBorderSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent)
  {
    super(newOrientation, newLeftComponent, newRightComponent);
    setBorder(EMPTY_BORDER);
    setOneTouchExpandable(true);
  }

  public void updateUI()
  {
    super.updateUI();
    removeDividerBorder();
  }

  private void removeDividerBorder()
  {
    BasicSplitPaneUI ui = (BasicSplitPaneUI)getUI();
    ui.getDivider().setBorder(new BorderUIResource(EMPTY_BORDER));
  }
}