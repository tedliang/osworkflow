package com.opensymphony.workflow.loader;

import java.util.*;
import java.io.*;
import java.net.URL;

/**
 * @author Hani Suleiman
 * Date: May 18, 2002
 * Time: 9:30:01 PM
 */

public class FileMonitor
{
  private Timer timer;
  private Map timerEntries;

  public FileMonitor()
  {
    // Create timer, run timer thread as daemon.
    timer = new Timer(true);
    timerEntries = new Hashtable();
  }

  /** Add a monitored file with a FileChangeListener.
   * @param listener listener to notify when the file changed.
   * @param fileName name of the file to monitor.
   * @param period polling period in milliseconds.
   */
  public void addFileChangeListener(FileChangeListener listener, Object key, String fileName, long period) throws FileNotFoundException
  {
    removeFileChangeListener(key);
    File file = getFile(listener, fileName);
    TimerTask task;
    if(file.isDirectory())
    {
      task = new DirectoryMonitorTask(listener, fileName, file, key);
    }
    else
    {
      task = new FileMonitorTask(listener, fileName, key, file);
    }
    timerEntries.put(key, task);
    timer.schedule(task, period, period);
  }

  /** Add a monitored file with a FileChangeListener.
   * @param listener listener to notify when the file changed.
   * @param dir name of the directory to monitor.
   * @param period polling period in milliseconds.
   * @param filter The file filter to apply when checking for whether a file within the specified dir should be monitored or not
   */
  public void addFileChangeListener(FileChangeListener listener, Object key, String dir, long period, FileFilter filter) throws FileNotFoundException
  {
    removeFileChangeListener(key);
    File file = getFile(listener, dir);
    TimerTask task;
    if(file.isDirectory())
    {
      task = new DirectoryMonitorTask(listener, dir, file, filter);
    }
    else
    {
      task = new FileMonitorTask(listener, dir, key, file);
    }
    timerEntries.put(key, task);
    timer.schedule(task, period, period);
  }

  /** Remove the listener from the notification list.
   */
  public void removeFileChangeListener(Object key)
  {
    TimerTask task = (TimerTask)timerEntries.remove(key);
    if(task != null)
    {
      task.cancel();
    }
  }

  protected void fireFileChangeEvent(FileChangeListener listener, Object key, String fileName)
  {
    listener.fileChanged(key, fileName);
  }

  private File getFile(FileChangeListener listener, String name) throws FileNotFoundException
  {
    File file = new File(name);
    if(!file.exists())
    {  // but is it on CLASSPATH?
      URL fileURL = listener.getClass().getClassLoader().getResource(name);
      if(fileURL != null)
      {
        file = new File(fileURL.getFile());
      }
      else
      {
        throw new FileNotFoundException("File Not Found: " + name);
      }
    }
    return file;
  }

  class DirectoryMonitorTask extends TimerTask
  {
    FileChangeListener listener;
    File monitoredDirectory;
    private Map modifiedMap;
    private String dirName;
    private Object key;
    private FileFilter filter;

    public DirectoryMonitorTask(FileChangeListener listener, String name, File dir, Object key)
    {
      this.listener = listener;
      this.dirName = name;
      modifiedMap = new HashMap();
      monitoredDirectory = dir;
      this.key = key;
    }

    public DirectoryMonitorTask(FileChangeListener listener, String name, File dir, FileFilter filter, Object key)
    {
      this.listener = listener;
      this.dirName = name;
      modifiedMap = new HashMap();
      monitoredDirectory = dir;
      this.filter = filter;
      this.key = key;
    }

    public void run()
    {
      //check if we have new entries/entries deleted, if we do, fire change
      File[] files;
      if(filter==null)
        files = monitoredDirectory.listFiles();
      else
        files = monitoredDirectory.listFiles(filter);
      Set allFiles = new HashSet();
      for (int i = 0; i < files.length; i++)
      {
        allFiles.add(files[i].toString());
      }
      if(files.length != modifiedMap.size() || !allFiles.containsAll(modifiedMap.keySet()))
      {
        updateMap();
        fireFileChangeEvent(this.listener, this.key, this.dirName);
      }
      //same number of files, so check their modified timestamps
      boolean isModified = false;
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        long lastModified = file.lastModified();
        Long oldModified = (Long)modifiedMap.get(file.toString());
        if(oldModified==null || lastModified != oldModified.longValue())
        {
          isModified = true;
          modifiedMap.put(file.toString(), new Long(lastModified));
        }
      }
      if(isModified)
      {
        fireFileChangeEvent(this.listener, this.key, this.dirName);
      }
    }

    private void updateMap()
    {
      File[] files;
      if(filter==null)
        files = monitoredDirectory.listFiles();
      else
        files = monitoredDirectory.listFiles(filter);
      Map map = new HashMap(files.length);
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        long lastModified = file.lastModified();
        Long oldModified = (Long)map.get(file.toString());
        if(oldModified==null || lastModified != oldModified.longValue())
        {
          map.put(file.toString(), new Long(lastModified));
        }
      }
      modifiedMap = map;
    }
  }

  class FileMonitorTask extends TimerTask
  {
    FileChangeListener listener;
    File monitoredFile;
    long lastModified;
    String fileName;
    Object key;
    
    public FileMonitorTask(FileChangeListener listener, String name, Object key, File file)
    {
      this.listener = listener;
      this.fileName = name;
      this.lastModified = 0;
      this.key = key;
      monitoredFile = file;
      this.lastModified = monitoredFile.lastModified();
    }

    public void run()
    {
      long lastModified = monitoredFile.lastModified();
      if(lastModified != this.lastModified)
      {
        this.lastModified = lastModified;
        fireFileChangeEvent(this.listener, this.key, this.fileName);
      }
    }
  }

/*  public static void main(String[] args) throws Exception
  {
    //create some crap
    File file = new File("blah.txt");
    File dir = new File("testdir");
    file.deleteOnExit();
    dir.deleteOnExit();
    dir.mkdir();
    file.createNewFile();
    FileChangeListener listener = new FileChangeListener()
    {
      public void fileChanged(String fileName)
      {
        System.out.println(System.currentTimeMillis() + " fileChanged " + fileName);
      }
    };
    FileMonitor.getInstance().addFileChangeListener(listener, file.toString(), 1000L);
    FileMonitor.getInstance().addFileChangeListener(listener, dir.toString(), 1000L);
    Thread.currentThread().sleep(60000L);
   }*/
}
