package com.opensymphony.workflow.designer;

import java.util.prefs.Preferences;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 21, 2003
 * Time: 12:15:05 AM
 */
public final class Prefs
{
  public static Preferences INSTANCE = Preferences.userNodeForPackage(WorkflowDesigner.class);
  public static final String DESIGNER_BOUNDS = "designer.bounds";
  public static final String CURRENT_DIR = "designer.dir";
  public static final String MAIN_DIVIDER_LOCATION = "designer.maindivider.location";
  public static final String DETAIL_DIVIDER_LOCATION = "designer.detaildivider.location";
  public static final String LAST_WORKSPACE = "designer.last.workspace";
  public static final String WORKFLOW_CURRENT = "designer.current.workflow";
  public static final String OPEN_WORKFLOWS = "designer.open.workflows";
}
