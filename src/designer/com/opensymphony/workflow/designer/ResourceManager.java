package com.opensymphony.workflow.designer;

import java.io.*;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.text.FieldPosition;
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

  public static ResourceBundle getBundle()
  {
    return INSTANCE.bundle;
  }

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

  public static String getString(String key, Object args)
  {

    try
    {
      String value = INSTANCE.bundle.getString(key);
      MessageFormat format = new MessageFormat(value);
      return format.format(args, new StringBuffer(), new FieldPosition(0)).toString();
    }
    catch(MissingResourceException e)
    {
      return key;
    }
  }

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

  public static InputStream getInputStream(String path)
  {
    return INSTANCE.loader.getResourceAsStream(path);
  }

  public static ImageIcon readImageIcon(String path)
  {
    URL url = INSTANCE.loader.getResource(path);
    return null == url ? null : new ImageIcon(url);
  }

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

}