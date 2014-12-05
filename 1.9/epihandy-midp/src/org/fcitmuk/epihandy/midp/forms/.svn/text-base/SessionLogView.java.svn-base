package org.fcitmuk.epihandy.midp.forms;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.epihandy.FormDataSummary;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;

public class SessionLogView extends AbstractView implements CommandListener {

	private Command cmdClearLog = new Command(MenuText.CLEAR_UPLOAD_LOG(), Command.SCREEN, 5);
	
	void showSessionLog(Vector sessionReferences) {
		
		screen = initializeScreen();
		display.setCurrent(screen);
		
		addSessionReferencesToScreen(sessionReferences);
	}
	
	private Displayable initializeScreen() {
		
		List list = new List(MenuText.UPLOAD_LOG(), Choice.IMPLICIT);
		list.setFitPolicy(List.TEXT_WRAP_ON);
		
		screen = list;
		
		screen.addCommand(cmdClearLog);
		screen.addCommand(DefaultCommands.cmdBack);
		screen.setCommandListener(this);
		
		return screen;
	}

	private void addSessionReferencesToScreen(Vector sessionReferences) {
		
		((List) screen).deleteAll();
		Vector sessionRefStrings = buildSessionReferences(sessionReferences);
		for(int ref = 0; ref < sessionRefStrings.size(); ref++){
			((List) screen).append((String) sessionRefStrings.elementAt(ref), null);
		}
	}
	
	private Vector buildSessionReferences(Vector sessionReferences) {
		
		Vector refs = new Vector(sessionReferences.size());
		for(int i = 0; i < sessionReferences.size(); i++) {
			String ref = "";
			FormDataSummary summary = (FormDataSummary) sessionReferences.elementAt(i);
			ref = summary.getReference() + " - " + summary.getDataDescription() + " - " + summary.getDateSubmitted();
			refs.addElement(ref);
		}
		
		return refs;
	}

	public void commandAction(Command c, Displayable s) {
		
		if(c == DefaultCommands.cmdBack){
			((EpihandyController)controller).handleCancelCommand(this);
		}
		else if(c == cmdClearLog){
			((List) screen).deleteAll();
			((EpihandyController)controller).clearSessionReferences();
			((EpihandyController)controller).handleCancelCommand(this);
		}
	}
}
