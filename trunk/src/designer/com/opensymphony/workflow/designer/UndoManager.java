package com.opensymphony.workflow.designer;

import javax.swing.event.UndoableEditEvent;

import org.jgraph.graph.GraphUndoManager;

/**
 * @version $Revision: 1.1 $
 * @author	Quake Wang
 * @since	2004-4-29
 */
public class UndoManager extends GraphUndoManager
{
  public void undoableEditHappened(UndoableEditEvent e)
  {
    super.undoableEditHappened(e);
    updateHistoryButtons();
  }

  public void updateHistoryButtons()
  {
    ActionManager.get("redo").setEnabled(this.canRedo());
    ActionManager.get("undo").setEnabled(this.canUndo());
  }
}
