package com.opensymphony.workflow.designer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import org.jgraph.JGraph;

/**
 * @author Hani Suleiman (hani@formicary.net)
 *         Date: May 21, 2003
 *         Time: 12:12:44 AM
 */
public class Utils
{
  private static Map contexts = new HashMap();

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

  public static void centerComponent(Component parent, Component child)
  {
    Point point = parent.getLocationOnScreen();
    Dimension parentDim = parent.getSize();
    Dimension childDim = child.getSize();
    int x;
    if(parentDim.width > childDim.width)
      x = point.x + (parentDim.width - childDim.width) / 2;
    else
      x = point.x - (childDim.width - parentDim.width) / 2;
    int y;
    if(parentDim.height > childDim.height)
      y = point.y + (parentDim.height - childDim.height) / 2;
    else
      y = point.y - (childDim.height - parentDim.height) / 2;
    child.setLocation(x, y);
  }

  public static BufferedImage toImage(JGraph graph)
  {
    Object[] cells = graph.getRoots();
    if(cells.length > 0)
    {
      Rectangle bounds = graph.getCellBounds(cells).getBounds();
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

  public static void checkId(Object context, AbstractDescriptor descriptor)
  {
    if(descriptor == null) return;
    Integer i = (Integer)contexts.get(context);
    int nextId = i == null ? 0 : i.intValue();
    if(descriptor.getId() >= nextId) nextId = descriptor.getId() + 1;
    contexts.put(context, new Integer(nextId));
  }

  public static int getNextId(Object context)
  {
    Integer i = (Integer)contexts.get(context);
    if(i == null) return 0;
    return i.intValue();
  }

  public static void centerComponent(Component component)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Window window;
    if(component instanceof Window)
    {
      window = (Window)component;
    }
    else
    {
      window = SwingUtilities.getWindowAncestor(component);
    }
    window.setLocation((screenSize.width - window.getSize().width) / 2, (screenSize.height - window.getSize().height) / 2);
  }
}
