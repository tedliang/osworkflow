package com.opensymphony.workflow.designer;

import java.io.File;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import org.jgraph.JGraph;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 12:12:44 AM
 */
public class Utils
{
  private static int nextId = 0;

  public static File promptUserForFile(Component component, int type, boolean save, final String suffix, final String description)
  {
    JFileChooser chooser = new JFileChooser(Prefs.INSTANCE.get(Prefs.CURRENT_DIR, System.getProperty("user.dir")));
    chooser.rescanCurrentDirectory();
    if(type == JFileChooser.FILES_AND_DIRECTORIES)
    {
      FileFilter filter = new FileFilter()
      {
        public boolean accept(File f)
        {
          return f.isDirectory() || f.getName().toLowerCase().endsWith(suffix);
        }

        public String getDescription()
        {
          return description;
        }
      };
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      chooser.setFileFilter(filter);
    }
    else
    {
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    File selectedFile = null;
    int result;
    if(save)
    {
      result = chooser.showSaveDialog(component);
    }
    else
    {
      result = chooser.showOpenDialog(component);
    }

    if(result == JFileChooser.APPROVE_OPTION)
    {
      selectedFile = chooser.getSelectedFile();
      if(save && type == JFileChooser.FILES_AND_DIRECTORIES && !selectedFile.getName().toLowerCase().endsWith(suffix))
      {
        selectedFile = new File(selectedFile.toString() + suffix);
      }
    }
    Prefs.INSTANCE.put(Prefs.CURRENT_DIR, chooser.getCurrentDirectory().toString());
    return selectedFile;
  }

  public static BufferedImage toImage(JGraph graph)
  {
    Object[] cells = graph.getRoots();
    if(cells.length > 0)
    {
      Rectangle bounds = graph.getCellBounds(cells);
      graph.toScreen(bounds);

      // Create a Buffered Image
      Dimension d = bounds.getSize();
      BufferedImage img = new BufferedImage(d.width + 10, d.height + 10, BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics = img.createGraphics();
      graphics.setColor(graph.getBackground());
      graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
      graphics.translate(-bounds.x + 5, -bounds.y + 5);

      Object[] selection = graph.getSelectionCells();
      boolean gridVisible = graph.isGridVisible();
      graph.setGridVisible(false);
      graph.clearSelection();

      graph.paint(graphics);

      graph.setSelectionCells(selection);
      graph.setGridVisible(gridVisible);

      return img;
    }
    return null;
  }

  public static void checkId(AbstractDescriptor descriptor)
  {
    if(descriptor == null) return;
    if(descriptor.getId() >= nextId) nextId = descriptor.getId() + 1;
  }

  public static int getNextId()
  {
    //todo needs to be per workflow, not global!
    return nextId;
  }
}
