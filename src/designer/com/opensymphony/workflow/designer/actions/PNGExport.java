package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.imageio.ImageIO;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.*;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 5:51:14 PM
 */
public class PNGExport extends AbstractAction implements WorkspaceListener
{
  public PNGExport()
  {
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    File output = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, true, ".png", ResourceManager.getString("png.files"));
    if(output==null) return;
    WorkflowGraph graph = WorkflowDesigner.INSTANCE.getCurrentGraph();
    BufferedImage image = Utils.toImage(graph);
    try
    {
      ImageIO.write(image, "png", output);
    }
    catch(IOException e1)
    {
      e1.printStackTrace();
    }
  }

  public void workspaceChanged(WorkspaceEvent event)
  {
    if(event.getId()==WorkspaceEvent.WORKSPACE_OPENED)
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }
}
