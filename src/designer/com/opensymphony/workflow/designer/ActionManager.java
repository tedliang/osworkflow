package com.opensymphony.workflow.designer;

import java.net.URL;
import java.util.*;
import javax.swing.*;

/**
 * Central repository for all Actions.
 * On startup, the application is responsible for registering all actions via the {@link #register(java.lang.String, javax.swing.Action)}
 * method. Once this is done, any action can be retrieved via the {@link #get(java.lang.String)} method.
 * The action manager will look for an actions.properties file in the same package, and will read all properties
 * specified in it for a given action.
 * <p>
 * The benefit of specifying actions in the external file is that actions themselves need not be aware of their textual or
 * graphic representation or key bindings.
 */
public final class ActionManager
{
	private static final String OS_NAME_STRING = System.getProperty("os.name").replace(' ', '_').toLowerCase();

  private static final String SMALL_GRAY_ICON = "smallGrayIcon";
  private static final String DISPLAYED_MNEMONIC_INDEX = "mnemonicIndex";

  private static final ActionManager INSTANCE = new ActionManager();

  private final Map actions;
  private ResourceBundle bundle;

  private ActionManager()
  {
    this.actions = new HashMap(50);
    bundle = ResourceBundle.getBundle("com.opensymphony.workflow.designer.actions");
  }

  /**
   * Register an action.
   * @param id The action id denotes the set of properties that will be read for the action
   * @param action The action instance to bind to the specified id.
   * @return The action, once it has been initialised. If the id is not specified in the
   * properties file, then null is returned.
   * @throws NullPointerException if the specified action is null
   */
  public static Action register(String id, Action action)
  {
    if(action == null)
      throw new NullPointerException("Registered actions must not be null.");

		boolean exists = ActionReader.readAndPutValues(action, INSTANCE.bundle, id);
		if(!exists) return null;

    Object oldValue = INSTANCE.actions.put(id, action);
    if(oldValue != null)
			System.out.println("WARNING: Duplicate action id: " + id);
    return action;
  }

	/**
	 * Remove a registered action
	 * @param id the action id
	 * @return The removed action, if it existed.
	 */
	public static Action deregister(String id)
	{
		return (Action)INSTANCE.actions.remove(id);
	}

  /**
   * Get a previously registered action
   * @param id The action id
   * @return The action bound to the specified id, or null if no action is bound.
   */
  public static Action get(String id)
  {
    Action action = (Action)(INSTANCE.actions.get(id));
    if(null == action)
    {
			System.out.println("ERROR: No action found for id: " + id);
      return null;
    }
    return action;
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

  /**
   * Alias a particular id to another one.
   * This allows one action to be bound to multiple keys.
   * @param newKey The new alias to bind to.
   * @param oldKey The old id to bind to.
   */
	public static void alias(String newKey, String oldKey)
	{
		Object oldValue = INSTANCE.actions.put(newKey, INSTANCE.actions.get(oldKey));
		if(oldValue != null)
			System.out.println("WARNING: Duplicate action id: " + newKey);
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
		private static final String COMMAND = "command";

		private String id;
		private String name;
		private Integer mnemonic;
		private Integer aMnemonicIndex;
		private String shortDescription;
		private String longDescription;
		private ImageIcon icon;
		private ImageIcon grayIcon;
		private KeyStroke accelerator;
		private String command;
		private boolean exists = true;

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
		static boolean readAndPutValues(Action action, ResourceBundle bundle, String id)
    {
      ActionReader reader = new ActionReader(bundle, id);
			if(!reader.actionExists()) return false;
      reader.putValues(action);
			return true;
    }

    private ActionReader(ResourceBundle bundle, String id)
    {
			String iconPath = getString(bundle, id + '.' + ICON, null);
			if(getString(bundle, id + "." + LABEL, null) == null && iconPath == null)
			{
				exists = false;
				return;
			}

      this.id = id;
      String nameWithMnemonic = getString(bundle, id + "." + LABEL, id);
      int index = mnemonicIndex(nameWithMnemonic);
      name = stripName(nameWithMnemonic, index);
      mnemonic = stripMnemonic(nameWithMnemonic, index);
      aMnemonicIndex = new Integer(index);

      shortDescription = getString(bundle, id + '.' + SHORT_DESCRIPTION, defaultShortDescription(name));
      longDescription = getString(bundle, id + '.' + LONG_DESCRIPTION, name);

			URL iconURL = iconPath != null ? getClass().getClassLoader().getResource(iconPath) : null;
			if(iconURL == null && iconPath != null)
			{
				System.out.println("WARNING Invalid icon " + iconPath + " specified in actions.properties for action '" + name + "'");
				icon = null;
			}
			else
			{
				icon = (iconPath == null) ? null : new ImageIcon(iconURL);
			}

      String grayIconPath = getString(bundle, id + '.' + GRAY_ICON, null);
      grayIcon = (grayIconPath == null) ? null : new ImageIcon(getClass().getClassLoader().getResource(grayIconPath));

			String shortcut = getString(bundle, id + '.' + ACCELERATOR + '.' + OS_NAME_STRING, null);
			if(shortcut == null)
			{
				shortcut = getString(bundle, id + '.' + ACCELERATOR, null);
			}
      accelerator = getKeyStroke(shortcut);

			command = getString(bundle, id + '.' + COMMAND, null);
		}

		public boolean actionExists()
		{
			return exists;
    }

    /**
     * Put the ActionReader's properties as values in the Action.
     */
    private void putValues(Action action)
    {
      action.putValue(Action.NAME, name);
      action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
      action.putValue(Action.LONG_DESCRIPTION, longDescription);
			if(icon != null)
        action.putValue(Action.SMALL_ICON, icon);
			if(grayIcon != null)
        action.putValue(ActionManager.SMALL_GRAY_ICON, grayIcon);
			if(accelerator != null)
        action.putValue(Action.ACCELERATOR_KEY, accelerator);
			if(mnemonic != null)
      action.putValue(Action.MNEMONIC_KEY, mnemonic);
			if(command != null)
				action.putValue(Action.ACTION_COMMAND_KEY, command);
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

		private KeyStroke getKeyStroke(String accelerator)
    {
			if(accelerator == null)
      {
        return null;
      }
      else
      {
				KeyStroke keyStroke = KeyStroke.getKeyStroke(accelerator);
        if(keyStroke == null)
					System.out.println("WARNING: Action " + id + " has an invalid accelerator " + accelerator);
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