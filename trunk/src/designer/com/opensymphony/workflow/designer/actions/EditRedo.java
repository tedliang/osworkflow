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

public class EditRedo extends AbstractAction
{
  public EditRedo()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    try
    {
      WorkflowGraph graph = WorkflowDesigner.INSTANCE.getCurrentGraph();
      graph.getUndoManager().redo(graph.getGraphLayoutCache());
      graph.getUndoManager().updateHistoryButtons();
    }
    catch(CannotUndoException ex)
    {
      System.out.println("Unable to redo: " + ex);
      ex.printStackTrace();
    }
  }
}
