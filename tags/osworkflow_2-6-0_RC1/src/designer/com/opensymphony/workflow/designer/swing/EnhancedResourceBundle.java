package com.opensymphony.workflow.designer.swing;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.text.FieldPosition;
import java.io.InputStream;
import java.net.URL;
import javax.swing.*;

/**
 * User: Hani Suleiman
 * Date: Dec 29, 2003
 * Time: 4:08:58 PM
 */
public class EnhancedResourceBundle
{
	private ResourceBundle bundle;
	private ClassLoader loader = EnhancedResourceBundle.class.getClassLoader();

	public EnhancedResourceBundle(String baseName)
	{
		this.bundle = ResourceBundle.getBundle(baseName);
	}

	public EnhancedResourceBundle(ResourceBundle bundle)
	{
		this.bundle = bundle;
	}

	public ResourceBundle getBundle()
	{
		return bundle;
	}

	public String getString(String key)
	{
		return getString(key, key);
	}

	public String getString(String key, String defaultValue)
	{
		try
		{
		  return bundle.getString(key);
		}
		catch(MissingResourceException e)
		{
		  return defaultValue;
		}
	}

	public String getString(String key, Object args)
	{

	  try
	  {
	    String value = bundle.getString(key);
	    MessageFormat format = new MessageFormat(value);
	    return format.format(args, new StringBuffer(), new FieldPosition(0)).toString();
	  }
	  catch(MissingResourceException e)
	  {
	    return key;
	  }
	}

	public ImageIcon getIcon(String key)
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

	public InputStream getInputStream(String path)
	{
	  return loader.getResourceAsStream(path);
	}

	public ImageIcon readImageIcon(String path)
	{
	  URL url = loader.getResource(path);
	  return null == url ? null : new ImageIcon(url);
	}
}
