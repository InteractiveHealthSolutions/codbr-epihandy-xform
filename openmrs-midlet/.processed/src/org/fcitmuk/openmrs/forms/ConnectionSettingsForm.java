package org.fcitmuk.openmrs.forms;

import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.util.DefaultCommands;


public class ConnectionSettingsForm  extends Form implements CommandListener{
	//
	private Display display;
	private Displayable screen, prevScreen;
	private String title;
		
	private static final String KEY_SERVER_URL = "serverUrl";
	
	/** The connection parameters. */
	protected Hashtable params = new Hashtable();	
	
	public ConnectionSettingsForm(String title, Display d, Displayable prevScreen)
	{
		super("Connection Settings");
		this.title = title;
		this.display = d;
		this.prevScreen = prevScreen;
		showConnectionParams();
	}

	public void showConnectionParams()
	{	
		this.setTitle(this.title);
		TextField txtField =new TextField("Server Address", "http://199.172.1.132:8181/openmrs/", 80, TextField.ANY);
		
		this.append(txtField);
		StringItem notification = new StringItem("Note", "Please enter complete web address of the server which points to the login page.");
		
		this.append(notification);
		this.addCommand(DefaultCommands.cmdCancel);
		this.addCommand(DefaultCommands.cmdOk);
		this.setCommandListener(this);

		display.setCurrent(this);
	}
	
	public String getURL()
	{
		return (String)params.get(KEY_SERVER_URL);
	}
	
	public void commandAction(Command cmd, Displayable disp) {
		if(cmd.getCommandType()== Command.OK)
		{
			Form frm = (Form)disp;
			TextField fld = (TextField)(frm.get(0));
			String val = fld.getString();

			if(!val.toLowerCase().equals("")) //URL has been entered
			{
				if(val.endsWith("/"))//
				{
					val = val.substring(0, val.length()-1); //save without the tail '/'
				}
				params.put(KEY_SERVER_URL,val);	
			}
			display.setCurrent(prevScreen);
		}
		
	}

}
