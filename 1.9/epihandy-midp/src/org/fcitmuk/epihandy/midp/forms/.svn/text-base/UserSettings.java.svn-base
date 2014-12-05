package org.fcitmuk.epihandy.midp.forms;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.communication.ConnectionSettingsListener;
import org.fcitmuk.communication.TransportLayer;
import org.fcitmuk.communication.TransportLayerListener;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.midp.db.EpihandyDataStorage;
import org.fcitmuk.midp.db.util.Settings;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.AlertMessage;
import org.fcitmuk.util.AlertMessageListener;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;


/**
 * 
 * @author daniel
 *
 */
public class UserSettings extends AbstractView implements ConnectionSettingsListener, TransportLayerListener, AlertMessageListener{

	private static final String KEY_LAST_SELECTED_SETTING = "KEY_LAST_SELECTED_SETTING";
	private static final String STORAGE_NAME_SETTINGS = "fcitmuk.UserSettings";
	
	private final byte CA_NONE=0;
	private final byte CA_RETRY=1;
	private final byte CA_LOGOUT=2;
	private byte currentAction=CA_NONE;
	private TransportLayer transportLayer;
	private String userName;
	private String password;
	private EpihandyController controller;
	private AlertMessage alertMsg =null;
	
	public void display(Display display, Displayable prevScreen, TransportLayer transportLayer,String userName, String password, EpihandyController controller){
		
		this.userName = userName;
		this.password = password;
		this.controller =controller;
		
		setDisplay(display);
		setPrevScreen(prevScreen);
		this.transportLayer = transportLayer;
		
		List list = new List(MenuText.SETTINGS(), Choice.IMPLICIT);
		list.setFitPolicy(List.TEXT_WRAP_ON);
		screen = list;
			
		screen.addCommand(DefaultCommands.cmdSel);
		screen.addCommand(DefaultCommands.cmdBack);
		
		((List)screen).append(MenuText.GENERAL(), null);
		((List)screen).append(MenuText.DATE_FORMAT(), null);
		((List)screen).append(MenuText.MULTIMEDIA(), null);
		((List)screen).append(MenuText.LANGUAGE(), null);
		((List)screen).append(MenuText.CONNECTION(), null);
		((List)screen).append(MenuText.ABOUT(), null);
		
		Settings settings = new Settings(STORAGE_NAME_SETTINGS,true);
		String val = settings.getSetting(KEY_LAST_SELECTED_SETTING);
		if(val != null)
			((List)screen).setSelectedIndex(Integer.parseInt(val),true);
		
		screen.setCommandListener(this);
				
		AbstractView.display.setCurrent(screen);
	}
	
	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			if(c == DefaultCommands.cmdSel || c == List.SELECT_COMMAND)
				handleOkCommand(d);
			else if(c == DefaultCommands.cmdBack)
				handleCancelCommand(d);
		}
		catch(Exception e){
			// TODO: Handle this gracefully.
		}
	}
	

	/**
	 * Processes the OK command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleOkCommand(Displayable d){
		int index = ((List)getScreen()).getSelectedIndex();
		if(index == 0)
			new GeneralSettings().display(display, screen);
		else if(index == 1)
			new DateSettings().display(display, screen);
		else if(index == 2)
			new MultMediaSettings().display(display, screen);
		else if(index == 3)
			new LanguageSettings().display(display, screen);
		else if(index == 4){
			transportLayer.setCurrentAlert(null);
			transportLayer.getUserSettings(display, screen,userName,password, this);
		}else if(index == 5){
			new AboutSettings().display(display, screen);
		}
		
		Settings settings = new Settings(STORAGE_NAME_SETTINGS,true);
		settings.setSetting(KEY_LAST_SELECTED_SETTING, String.valueOf(((List)getScreen()).getSelectedIndex()));
		settings.saveSettings();
	}
	
	/**
	 * Processes the cancel command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleCancelCommand(Displayable d){
		display.setCurrent(getPrevScreen());
	}

	public void connectionSettingsClosed() {
		DownloadUploadManager dwnloadMgr = new DownloadUploadManager(
				this.transportLayer, this.controller, title, this, false);
		dwnloadMgr.downloadUsers(this.screen, this.controller.getUserManager()
				.getUserName(), this.controller.getUserManager().getPassword());
	}

	// From TransportLayerListener: Not needed for verifying & authenticating URL
	public void uploaded(Persistent dataInParams, Persistent dataIn,
			Persistent dataOutParams, Persistent dataOut) {
	}

	public void downloaded(Persistent dataInParams, Persistent dataIn,
			Persistent dataOutParams, Persistent dataOut) {
		// user downloaded successfully, so show the previous screen
		display.setCurrent(this.prevScreen);
	}

	public void errorOccured(String errorMessage, Exception e) {
		// user not downloaded
		alertMsg = new AlertMessage(this.getDisplay(), "Error",
				this.getPrevScreen(), this);
		if (errorMessage.indexOf(MenuText.ACCESS_DENIED()) > -1) {
			currentAction = CA_LOGOUT;
			alertMsg.showConfirm(MenuText.ACCESS_DENIED_RELOGIN());
		} else {
			currentAction = CA_RETRY;
			alertMsg.showConfirm(MenuText.INVALID_CONNECTION_SETTINGS());
		}
	}

	// From TransportLayerListener: Not needed for verifying & authenticating URL
	public void cancelled() {
	}

	// From TransportLayerListener: Not needed for verifying & authenticating URL
	public void updateCommunicationParams() {
	}

	public void onAlertMessage(byte msg) {

		if (msg == AlertMessageListener.MSG_OK) {
			switch (currentAction) {

			case (CA_RETRY):
				transportLayer.setCurrentAlert(null);
				transportLayer.getUserSettings(display, screen, userName,
						password, this);
				break;

			case (CA_LOGOUT):				
				//delete user from the recordstore to re-download on the login 
				EpihandyDataStorage.deleteUser(this.controller.getUserManager().getUserId(),this.controller.getUserManager().getUserName()) ;
				controller.logout();
				break;
			}
		} else if (msg == AlertMessageListener.MSG_CANCEL) {
			display.setCurrent(this.prevScreen);
		}
	}
}
