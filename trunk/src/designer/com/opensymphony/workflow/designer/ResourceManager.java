package com.opensymphony.workflow.designer;

import java.io.*;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;

public final class ResourceManager
{
  private static final ResourceManager INSTANCE = new ResourceManager();

  private ResourceBundle bundle;

  private ClassLoader loader = ResourceManager.class.getClassLoader();

  private ResourceManager()
  {
    bundle = ResourceBundle.getBundle(getClass().getName());
  }

  /**
   * Retrieves and answers a <code>String</code> for the given key from the
   * bundle.
   */
  public static String getString(String key)
  {
    try
    {
      return INSTANCE.bundle.getString(key);
    }
    catch(MissingResourceException e)
    {
      return key;
    }
  }

  /**
   * Retrieves and answers an <code>ImageIcon</code> for the given key from
   * the bundle.
   */
  public static ImageIcon getIcon(String key)
  {
    String path = getString("image." + key);
    if(path == null)
    {
      return null;
    }
    else if(path.length() == 0)
    {
      return null;
    }
    else
      return readImageIcon(path);
  }

  /**
   * Gets and answers an <code>InputStream</code> for the given path
   * using the default <code>ClassLoader</code>.
   */
  public static InputStream getInputStream(String path)
  {
    return INSTANCE.loader.getResourceAsStream(path);
  }

  /**
   * Reads and answers an <code>ImageIcon</code> for the given path
   * using the default <code>ClassLoader</code>.
   */
  public static ImageIcon readImageIcon(String path)
  {
    URL url = INSTANCE.loader.getResource(path);
    return null == url ? null : new ImageIcon(url);
  }

  /**
   * Reads and answers the <code>String</code> contents of a text file
   * located at the the given path using the default <code>ClassLoader</code>.
   */
  public static String readTextFromFile(String path)
  {
    InputStream in = getInputStream(path);
    if(null == in)
      return null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuffer result = new StringBuffer();
    try
    {
      String line = reader.readLine();
      while(line != null)
      {
        result.append(line);
        line = reader.readLine();
        if(line != null)
          result.append('\n');
      }
      return result.toString();
    }
    catch(IOException e)
    {
      return null;
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch(IOException e)
      {
      }
    }
  }

  /**
   * Loads <code>Properties</code> from the specified path.
   *
   * @deprecated Property files should be replaced by resource bundles.
   */
  public static void loadProperties(Properties properties, String pathname)
  {
    try
    {
      properties.load(new BufferedInputStream(ResourceManager.getInputStream(pathname)));
    }
    catch(Exception e)
    {
    }
  }

}