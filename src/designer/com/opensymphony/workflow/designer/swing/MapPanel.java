package com.opensymphony.workflow.designer.swing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.loader.ArgsAware;

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

  public MapPanel(ArgsAware descriptor, String type, String name, String description, String owner)
  {
    FormLayout layout = new FormLayout("2dlu, left:max(40dlu;pref), 3dlu, 110dlu:grow, 7dlu");
		DefaultFormBuilder builder = new DefaultFormBuilder(this, layout, ResourceManager.getBundle());
	  builder.setLeadingColumnOffset(1);
    builder.appendI15dSeparator("info");
    builder.appendI15d("type", new JLabel(noNull(type)));
	  builder.appendI15d("name", new JLabel(noNull(name)));
	  builder.appendI15d("description", new JLabel(noNull(description)));
		if(owner!=null)
		{
			JTextField tf = new JTextField(15);
			tf.setText(noNull(owner));
			builder.appendI15d("owner", tf);
			edits.put("Owner", tf);
		}
    Map args = descriptor.getArgs();
    if(args.size() > 0)
    {
      builder.appendI15dSeparator("args");
    }

    Set keys = args.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      String key = (String)iter.next();
      JTextField field = descriptor.isArgModifiable(key) ? new JTextField(25) : UIFactory.createReadOnlyTextField(25);
      if(args.get(key) != null)
      {
        field.setText((String)args.get(key));
      }
	    builder.append(key, field);
      edits.put(key, field);
    }
  }

  private static String noNull(String s)
  {
    return s==null ? "" : s;
  }
}
