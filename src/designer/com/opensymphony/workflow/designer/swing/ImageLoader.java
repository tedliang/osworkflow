package com.opensymphony.workflow.designer.swing;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * @author baab
 */
public class ImageLoader
{
  private static final String PATH = "/images/";

  public static ImageIcon getIcon(String name)
  {
    return new ImageIcon(ImageLoader.class.getResource(PATH + name));
  }

  public static Image getImage(String name)
  {
    return new ImageIcon(ImageLoader.class.getResource(PATH + name)).getImage();
  }

}
