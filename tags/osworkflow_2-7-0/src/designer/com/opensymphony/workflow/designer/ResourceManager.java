package com.opensymphony.workflow.designer;

import java.io.*;
import java.util.ResourceBundle;
import javax.swing.*;

import com.opensymphony.workflow.designer.swing.EnhancedResourceBundle;

public final class ResourceManager
{
  private static final ResourceManager INSTANCE = new ResourceManager();

  private EnhancedResourceBundle bundle;

  private ResourceManager()
  {
    bundle = new EnhancedResourceBundle("com.opensymphony.workflow.designer.resources");
  }

  public static ResourceBundle getBundle()
  {
    return INSTANCE.bundle.getBundle();
  }

  public static String getString(String key)
  {
	  return getString(key, key);
  }

	public static String getString(String key, String defaultValue)
	{
		return INSTANCE.bundle.getString(key, defaultValue);
	}

  public static String getString(String key, Object args)
  {
	  return INSTANCE.bundle.getString(key, args);
  }

  public static ImageIcon getIcon(String key)
  {
	  return INSTANCE.bundle.getIcon(key);
  }

  public static InputStream getInputStream(String path)
  {
    return INSTANCE.bundle.getInputStream(path);
  }

  public static ImageIcon readImageIcon(String path)
  {
	  return INSTANCE.bundle.readImageIcon(path);
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