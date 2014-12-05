package org.ihs.codbr.forms;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import org.ihs.codbr.MainMidlet;

public abstract class BaseUIDataView extends Form implements CommandListener
 {
	private MainMidlet 	mainMidlet;
	private Displayable prevDisplayable;
	
	public Displayable getPrevDisplayable ()
	{
		return prevDisplayable;
	}

	public void setPrevDisplayable (Displayable prvDisplayable)
	{
		this.prevDisplayable = prvDisplayable;
	}

	public BaseUIDataView (String title, MainMidlet mainMidlet)
	{
		super (title);
		this.setMainMidlet(mainMidlet);
	}

	public void setMainMidlet(MainMidlet mainMidlet) {
		this.mainMidlet = mainMidlet;
	}

	public MainMidlet getMainMidlet() {
		return mainMidlet;
	}
	public abstract void init(Displayable prvDisplayable);
	
	protected abstract void cleanup();
	
}
