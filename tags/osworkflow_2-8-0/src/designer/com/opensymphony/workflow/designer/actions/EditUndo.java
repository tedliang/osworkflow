package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.undo.CannotUndoException;

import com.opensymphony.workflow.designer.WorkflowDesigner;
import com.opensymphony.workflow.designer.WorkflowGraph;

/**
 * @version $Revision: 1.1 $
 * @author	Quake Wang
 * @since	2004-4-29
 */

public class EditUndo extends AbstractAction
{
  public EditUndo()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    try
    {
      WorkflowGraph graph = WorkflowDesigner.INSTANCE.getCurrentGraph();
      graph.getUndoManager().undo(graph.getGraphLayoutCache());
      graph.getUndoManager().updateHistoryButtons();
    }
    catch(CannotUndoException ex)
    {
      System.out.println("Unable to undo: " + ex);
      ex.printStackTrace();
    }
  }

}
