package com.opensymphony.workflow.designer.swing;

import java.io.InputStream;
import java.net.URL;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * User: Hani Suleiman
 * Date: Dec 29, 2003
 * Time: 4:08:58 PM
 */
public class EnhancedResourceBundle extends ResourceBundle
{
  private PropertyResourceBundle bundle1;
  private PropertyResourceBundle bundle2;

  private ClassLoader loader = EnhancedResourceBundle.class.getClassLoader();

  public EnhancedResourceBundle(String bundleName)
  {
    this.bundle1 = (PropertyResourceBundle)ResourceBundle.getBundle(bundleName);
  }

  public EnhancedResourceBundle(PropertyResourceBundle bundle)
  {
    this.bundle1 = bundle;
  }

  public EnhancedResourceBundle(PropertyResourceBundle bundle1, PropertyResourceBundle bundle2)
  {
    this.bundle1 = bundle1;
    this.bundle2 = bundle2;
  }

  public ResourceBundle getBundle()
  {
    return this;
  }

  public String getString(String key, String defaultValue)
  {
    try
    {
      return bundle1.getString(key);
    }
    catch(MissingResourceException e)
    {
    }

    if(bundle2 != null)
    {
      try
      {
        return bundle2.getString(key);
      }
      catch(MissingResourceException e)
      {
      }
    }

    return defaultValue;
  }

  public String getString(String key, Object args)
  {

    try
    {
      String value = bundle1.getString(key);
      MessageFormat format = new MessageFormat(value);
      return format.format(args, new StringBuffer(), new FieldPosition(0)).toString();
    }
    catch(MissingResourceException e)
    {
    }

    if(bundle2 != null)
    {
      try
      {
        String value = bundle2.getString(key);
        MessageFormat format = new MessageFormat(value);
        return format.format(args, new StringBuffer(), new FieldPosition(0)).toString();
      }
      catch(MissingResourceException e)
      {
      }
    }

    return key;
  }

  protected Object handleGetObject(String key)
  {
    Object o = bundle1.handleGetObject(key);

    if(o == null && bundle2 != null)
    {
      o = bundle2.handleGetObject(key);
    }

    return o;
  }

  public Enumeration getKeys()
  {
    ArrayList keys = Collections.list(bundle1.getKeys());

    if(bundle2 != null)
    {
      keys.addAll(Collections.list(bundle1.getKeys()));
    }

    return Collections.enumeration(keys);
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
    else return readImageIcon(path);
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
