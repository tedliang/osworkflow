package com.opensymphony.workflow.designer.swing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Gulei
 */
public class MapPanel extends JPanel
{
  private Map edits = new HashMap();

  public Map getEdits()
  {
    return edits;
  }

  public MapPanel(Map args, String type, String name, String description)
  {
    super();

    String prefix = "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 3dlu";
    String repeat = ", pref, 2dlu";

    if(args.size() > 0)
    {
      prefix += repeat;
    }

    for(int i = 0; i < args.size(); i++)
    {
      prefix += repeat;
    }
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", prefix);

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));

    builder.addTitle("Type", cc.xy(2, 3));
    builder.addLabel(noNull(type), cc.xy(4, 3));

    builder.addTitle("Name", cc.xy(2, 5));
    builder.addLabel(noNull(name), cc.xy(4, 5));

    builder.addTitle("Description", cc.xy(2, 7));
    builder.addLabel(noNull(description), cc.xy(4, 7));

    if(args.size() > 0)
    {
      builder.addSeparator("Arguments", cc.xywh(2, 9, 3, 1));
    }

    int base = 11;
    Set keys = args.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      Object key = iter.next();
      builder.addLabel((String)key, cc.xy(2, base));
      JTextField field = new JTextField(15);
      if(args.get(key) != null)
      {
        field.setText((String)args.get(key));
      }
      builder.add(field, cc.xy(4, base));

      edits.put(key, field);
      base += 2;
    }
  }

  public MapPanel(Map args, String type, String name, String description, String owner)
  {
    super();

    String prefix = "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 3dlu";
    String repeat = ", pref, 2dlu";

    if(args.size() > 0)
    {
      prefix += repeat;
    }

    for(int i = 0; i < args.size(); i++)
    {
      prefix += repeat;
    }
    FormLayout layout = new FormLayout("2dlu, max(30dlu;pref), 2dlu, pref:grow, 4dlu", prefix);

    PanelBuilder builder = new PanelBuilder(this, layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Info", cc.xywh(2, 1, 3, 1));

    builder.addTitle("Type", cc.xy(2, 3));
    builder.addLabel(noNull(type), cc.xy(4, 3));

    builder.addTitle("Name", cc.xy(2, 5));
    builder.addLabel(noNull(name), cc.xy(4, 5));

    builder.addTitle("Description", cc.xy(2, 7));
    builder.addLabel(noNull(description), cc.xy(4, 7));

    builder.addTitle("Owner", cc.xy(2, 9));
    JTextField tf = new JTextField(15);
    tf.setText(noNull(owner));
    builder.add(tf, cc.xy(4, 9));
    edits.put("Owner", tf);

    if(args.size() > 0)
    {
      builder.addSeparator("Arguments", cc.xywh(2, 11, 3, 1));
    }

    int base = 13;
    Set keys = args.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      Object key = iter.next();
      builder.addLabel((String)key, cc.xy(2, base));
      JTextField field = new JTextField(15);
      if(args.get(key) != null)
      {
        field.setText((String)args.get(key));
      }
      builder.add(field, cc.xy(4, base));

      edits.put(key, field);
      base += 2;
    }
  }

  private static String noNull(String s)
  {
    return s==null ? "" : s;
  }
}
