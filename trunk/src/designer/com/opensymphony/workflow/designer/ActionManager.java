package com.opensymphony.workflow.designer;

import java.util.*;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ActionManager
{

  private static final String SMALL_GRAY_ICON = "smallGrayIcon";
  private static final String DISPLAYED_MNEMONIC_INDEX = "mnemonicIndex";

  private static final ActionManager INSTANCE = new ActionManager();
  private static final Log LOGGER = LogFactory.getLog(ActionManager.class);

  private final Map actions;
  private ResourceBundle bundle;

  private ActionManager()
  {
    this.actions = new HashMap(50);
    bundle = ResourceBundle.getBundle(getClass().getName());
  }

  public static Action register(String id, Action action)
  {
    if(action == null)
      throw new NullPointerException("Registered actions must not be null.");

    ActionReader.readAndPutValues(action, INSTANCE.bundle, id);
    Object oldValue = INSTANCE.actions.put(id, action);
    if(oldValue != null)
      LOGGER.warn("Duplicate action id: " + id);
    return action;
  }

  public static Action get(String id)
  {
    Action action = (Action)(INSTANCE.actions.get(id));
    if(null == action)
    {
      LOGGER.error("No action found for id: " + id);
      return null;
    }
    return action;
  }

  public static List getAll(String[] allIDs)
  {
    List result = new LinkedList();
    for(int i = 0; i < allIDs.length; i++)
    {
      Action action = get(allIDs[i]);
      if(action != null)
        result.add(action);
    }
    return result;
  }

  /**
   * Retrieves and answers the small icon for the given <code>id</code>.
   */
  public static Icon getIcon(String id)
  {
    Action action = get(id);
    if(action == null)
      return null;
    return (Icon)action.getValue(Action.SMALL_ICON);
  }

  private static class ActionReader
  {
    private static final String LABEL = "label";
    private static final char MNEMONIC_MARKER = '&';
    private static final String DOT_STRING = "...";
    private static final String SHORT_DESCRIPTION = "tooltip";
    private static final String LONG_DESCRIPTION = "helptext";
    private static final String ICON = "icon";
    private static final String GRAY_ICON = ICON + ".gray";
    private static final String ACCELERATOR = "accelerator";

    private final String id;
    private final String name;
    private final Integer mnemonic;
    private final Integer aMnemonicIndex;
    private final String shortDescription;
    private final String longDescription;
    private final ImageIcon icon;
    private final ImageIcon grayIcon;
    private final KeyStroke accelerator;

    /**
     * Reads properties for <code>id</code> in <code>bundle</code>.
     */
    static void readValues(ResourceBundle bundle, String id)
    {
      new ActionReader(bundle, id);
    }

    /**
     * Reads properties for <code>id</code> in <code>bundle</code> and
     * sets the approriate values in the given <code>action</code>.
     */
    static void readAndPutValues(Action action, ResourceBundle bundle, String id)
    {
      ActionReader reader = new ActionReader(bundle, id);
      reader.putValues(action);
    }

    private ActionReader(ResourceBundle bundle, String id)
    {
      this.id = id;
      String nameWithMnemonic = getString(bundle, id + "." + LABEL, id);
      int index = mnemonicIndex(nameWithMnemonic);
      name = stripName(nameWithMnemonic, index);
      mnemonic = stripMnemonic(nameWithMnemonic, index);
      aMnemonicIndex = new Integer(index);

      shortDescription = getString(bundle, id + '.' + SHORT_DESCRIPTION, defaultShortDescription(name));
      longDescription = getString(bundle, id + '.' + LONG_DESCRIPTION, name);

      String iconPath = getString(bundle, id + '.' + ICON, null);
      icon = (iconPath == null) ? null : new ImageIcon(getClass().getClassLoader().getResource(iconPath));

      String grayIconPath = getString(bundle, id + '.' + GRAY_ICON, null);
      grayIcon = (grayIconPath == null) ? null : new ImageIcon(getClass().getClassLoader().getResource(grayIconPath));

      String shortcut = getString(bundle, id + '.' + ACCELERATOR, null);
      accelerator = getKeyStroke(shortcut);
    }

    /**
     * Put the ActionReader's properties as values in the Action.
     */
    private void putValues(Action action)
    {
      action.putValue(Action.NAME, name);
      action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
      action.putValue(Action.LONG_DESCRIPTION, longDescription);
      if(icon!=null)
        action.putValue(Action.SMALL_ICON, icon);
      if(grayIcon!=null)
        action.putValue(ActionManager.SMALL_GRAY_ICON, grayIcon);
      if(accelerator!=null)
        action.putValue(Action.ACCELERATOR_KEY, accelerator);
      if(mnemonic!=null)
      action.putValue(Action.MNEMONIC_KEY, mnemonic);
      action.putValue(ActionManager.DISPLAYED_MNEMONIC_INDEX, aMnemonicIndex);
    }

    private int mnemonicIndex(String nameWithMnemonic)
    {
      return nameWithMnemonic.indexOf(MNEMONIC_MARKER);
    }

    private String stripName(String nameWithMnemonic, int mnemonicIndex)
    {
      return mnemonicIndex == -1 ? nameWithMnemonic : nameWithMnemonic.substring(0, mnemonicIndex) + nameWithMnemonic.substring(mnemonicIndex + 1);
    }

    private Integer stripMnemonic(String nameWithMnemonic, int mnemonicIndex)
    {
      return mnemonicIndex == -1 ? null : new Integer(nameWithMnemonic.charAt(mnemonicIndex + 1));
    }

    private String defaultShortDescription(String nameWithDots)
    {
      return nameWithDots.endsWith(DOT_STRING) ? (nameWithDots.substring(0, nameWithDots.length() - DOT_STRING.length())) : nameWithDots;
    }

    private KeyStroke getKeyStroke(String accelleratorString)
    {
      if(accelleratorString == null)
      {
        return null;
      }
      else
      {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(accelleratorString);
        if(keyStroke == null)
          LogFactory.getLog(getClass()).warn("Action " + id + " has an invalid accellerator " + accelleratorString);
        return keyStroke;
      }
    }

    private String getString(ResourceBundle bundle, String key, String defaultString)
    {
      try
      {
        return bundle.getString(key);
      }
      catch(MissingResourceException e)
      {
        return defaultString;
      }
    }
  }
}