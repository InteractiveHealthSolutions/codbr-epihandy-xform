package org.fcitmuk.epihandy.midp.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import org.fcitmuk.epihandy.RequestHeader;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;

public class AboutSettings extends AbstractView{
	public void display(Display display, Displayable prevScreen){
		setDisplay(display);
		setPrevScreen(prevScreen);
		screen = new Form(MenuText.ABOUT());
		screen.addCommand(DefaultCommands.cmdBack);
		screen.setCommandListener(this);
		((Form)screen).append(new StringItem(MenuText.Client_Name(),AbstractView.mainForm.getAppProperty("MIDlet-Name")));
		((Form)screen).append(new StringItem(MenuText.Client_Vendor(),AbstractView.mainForm.getAppProperty("MIDlet-Vendor")));
		((Form)screen).append(new StringItem(MenuText.Client_Version(),AbstractView.mainForm.getAppProperty("MIDlet-Version")));
		((Form)screen).append(new StringItem(MenuText.Server_Protocol(),RequestHeader.getSerializer()));
		AbstractView.display.setCurrent(screen);
	}
	public void commandAction(Command c, Displayable s) {
		try{
			if(c == DefaultCommands.cmdBack)
				display.setCurrent(getPrevScreen());
		}
		catch(Exception e){
		}
		
	}

}
