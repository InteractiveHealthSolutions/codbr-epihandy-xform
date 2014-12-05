package org.ihs.codbr.forms;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.communication.ConnectionParameter;
import org.ihs.codbr.MainMidlet;

public abstract class BaseUIList extends List implements CommandListener{
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

	public BaseUIList (String title, MainMidlet mainMidlet)
	{
		super (title, List.IMPLICIT);
		this.setMainMidlet(mainMidlet);
	}

	public void setMainMidlet(MainMidlet mainMidlet) {
		this.mainMidlet = mainMidlet;
	}

	public MainMidlet getMainMidlet() {
		return mainMidlet;
	}

	protected abstract ConnectionParameter createRequestPayload(int type);
	
	public abstract void init(Displayable prvDisplayable);
	
	protected abstract void loadItems();
	
	protected abstract void cleanup();
	
}
