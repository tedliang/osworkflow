package com.opensymphony.workflow.loader;

/**
 * @author Hani Suleiman
 */
public interface FileChangeListener
{
  /** Invoked when a file changes.
   * @param fileName name of changed file.
   */
  public void fileChanged(Object key, String fileName);
}
