package com.opensymphony.workflow.designer.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.imageio.ImageIO;

import com.opensymphony.workflow.designer.event.WorkspaceListener;
import com.opensymphony.workflow.designer.event.WorkspaceEvent;
import com.opensymphony.workflow.designer.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 5:51:14 PM
 */
public class PNGExport extends AbstractAction implements WorkspaceListener
{
  private static final Log log = LogFactory.getLog(PNGExport.class);

  public PNGExport()
  {
    setEnabled(false);
    putValue(NAME, "PNG export");
    putValue(SHORT_DESCRIPTION, "Export as PNG");
    putValue(LONG_DESCRIPTION, "Export currently displayed workflow as a PNG image");
  }

  public void actionPerformed(ActionEvent e)
  {
    File output = Utils.promptUserForFile((Component)e.getSource(), JFileChooser.FILES_AND_DIRECTORIES, true, ".png", "PNG files");
    if(output==null) return;
    WorkflowGraph graph = WorkflowDesigner.INSTANCE.getCurrentGraph();
    BufferedImage image = Utils.toImage(graph);
    try
    {
      ImageIO.write(image, "png", output);
    }
    catch(IOException e1)
    {
      log.error("Error writing png file", e1);
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
