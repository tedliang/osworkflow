package com.opensymphony.workflow.designer.swing;

import java.util.*;

import javax.swing.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.opensymphony.workflow.designer.ResourceManager;
import com.opensymphony.workflow.designer.UIFactory;
import com.opensymphony.workflow.loader.ArgsAware;
import com.opensymphony.workflow.loader.ArgType;

/**
 * @author Gulei
 */
public class MapPanel extends JPanel
{
  private Map edits = new HashMap();
	private Map buttons = new HashMap();

  public Map getEdits()
  {
    return edits;
  }

	public Map getButtons()
	{
		return buttons;
	}
	
  public MapPanel(final ArgsAware descriptor, String type, String owner)
  {
    //FormLayout layout = new FormLayout("2dlu, left:max(40dlu;pref), 3dlu, pref:grow, 7dlu");
		FormLayout layout = new FormLayout("2dlu, left:max(40dlu;pref), 3dlu, pref:grow, 3dlu, pref:grow, 2dlu");
		DefaultFormBuilder builder = new DefaultFormBuilder(this, layout, ResourceManager.getBundle());
	  builder.setLeadingColumnOffset(1);
    builder.appendI15dSeparator("info");
    builder.appendI15d("type", new JLabel(noNull(type)));
    builder.nextLine();
	  builder.appendI15d("name", new JLabel(noNull(descriptor.getName())));
	  builder.nextLine();
	  builder.appendI15d("description", new JLabel(noNull(descriptor.getDescription())));
		builder.nextLine();
		
		if(owner!=null)
		{
			JTextField tf = new JTextField(15);
			tf.setText(noNull(owner));
			builder.appendI15d("owner", tf);
			edits.put("Owner", tf);
			builder.nextLine();
		}
    Map args = descriptor.getArgs();
    if(args.size() > 0)
    {
      builder.appendI15dSeparator("args");
    }

    List keys = new ArrayList(args.keySet());
	  Collections.sort(keys, new Comparator()
	  {
		  public int compare(Object o1, Object o2)
		  {
			  String key1 = (String)o1;
			  String key2 = (String)o2;
			  boolean mod1 = descriptor.isArgModifiable(key1);
			  boolean mod2 = descriptor.isArgModifiable(key2);
			  if(mod1 && !mod2) return 1;
			  if(mod2 && !mod1) return -1;
			  String value1 =  descriptor.getPalette().getBundle().getString(descriptor.getName() + "." + key1, key1);
			  String value2 =  descriptor.getPalette().getBundle().getString(descriptor.getName() + "." + key2, key2);
			  return value1.compareTo(value2);
		  }
	  });
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      String key = (String)iter.next();
      JTextField field = descriptor.isArgModifiable(key) ? new JTextField(30) : UIFactory.createReadOnlyTextField(30);
      if(args.get(key) != null)
      {
        field.setText((String)args.get(key));
      }
	    String value = descriptor.getPalette().getBundle().getString(descriptor.getName() + "." + key, key);
	    //builder.append(value, field);
      edits.put(key, field);
	    ArgType argType = descriptor.getArgType(key);
	    if (argType!=null)
	    {
	    	// button displaying list of predefined arguments
	    	JButton button = new JButton("...");
	    	//builder.append(button);
	    	builder.append(value, field, button);
	    	buttons.put(key, button);
	    	button.addActionListener(new ArgListAction(button, field, argType));
	    }
	    else
	   	{
	   		builder.append(value, field);
	   	}
			builder.nextLine();
    }
		builder.appendI15dSeparator("");
  }

  private static String noNull(String s)
  {
    return s==null ? "" : s;
  }
}
