package org.ihs.codbr.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDletStateChangeException;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.RequestHeader;
import org.fcitmuk.openmrs.User;
import org.fcitmuk.openmrs.UserList;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;
import org.ihs.codbr.util.Utils;
import org.ihs.logger.Log;

public class LoginUIForm extends BaseUIForm implements ResponseNotification
{
	private TextField username;
	private TextField password;
	private Command		cmdOK;
	private Command		cmdExit;
	private Command		cmdSettings;
	private Command		cmdLog;
	
	public LoginUIForm (String title, MainMidlet tbrMidlet)
	{
		super (title, tbrMidlet);
	}

	public boolean validate ()
	{
		if (Utils.isEmptyOrWhiteSpaceOnly(username.getString()))
		{
			UIUtils.showPopup(ErrorMessages.USERNAME_MISSING, null, getMainMidlet().getDisplay());
			return false;
		}
		else if (Utils.isEmptyOrWhiteSpaceOnly(password.getString()))
		{
			UIUtils.showPopup(ErrorMessages.PASSWORD_MISSING, null, getMainMidlet().getDisplay());
			return false;
		}
		return true;
	}
	
	private void authenticateFromServer(){
		UIUtils.showProgressBar("Authenticating user from server", getMainMidlet().getDisplay());
		RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_USERS, username.getString(), password.getString(), getMainMidlet().getLocale(), null);
		//TODO ideally it should download users but we donot have working copy of it in Openmrs
		getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_USER_DOWNLOAD, false, 
				createRequestPayload(), false, reqh, new UserList(), this, true);
		//getMainMidlet().sendSimpleRequest(WebServerConstants.SUBURL_XFORM_USER_VALIDATOR, false, createRequestPayload(), false, this, false);
	}

	public void commandAction (Command c, Displayable d)
	{
		if (c == cmdOK)
		{
			if (validate ())
			{
				//RecordStoreUtil.getInstance().clearRecordStore();
				//System.out.println(RecordStoreUtil.getInstance().writeRecord(USERNAME_KEY, username.getString()));
				//System.out.println(RecordStoreUtil.getInstance().writeRecord(PASSWORD_KEY, password.getString()));

				if(Utils.isFirstLaunch()){
					authenticateFromServer();
				}
				else {
					trylogin(username.getString(), password.getString());
				}
			}
		}
		else if (c == cmdExit)
		{
			cleanup();

			try
			{
				getMainMidlet().destroyApp (false);
				getMainMidlet().notifyDestroyed ();
			}
			catch (MIDletStateChangeException e)
			{
				e.printStackTrace();
			}
		}
		else if(c == cmdSettings){
			getMainMidlet().getTransportLayer().getConnectionSettings().openSettingsEditor(getMainMidlet().getDisplay(), d);
		}
		else if(c == cmdLog){
			Log.showLog(getMainMidlet().getDisplay(), this);
		}
	}

	private void trylogin(String username, String clearTextPassword){
		User u = Utils.authenticate(username, clearTextPassword);
		if(u != null){
			u.setClearTextPassword(clearTextPassword);
			getMainMidlet().setCurrentXformUser(u);
			getMainMidlet().startMainMenu();
		}
		else {
			UIUtils.showConfirm(UIConstants.TEXT_SERVER_AUTHENTICATION_CONFIRM, new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					authenticateFromServer();
				}
			}, null, getMainMidlet().getDisplay());
		}
	}
	
	public void init (Displayable prvDisplayable)
	{
		super.setPrevDisplayable(null);
		
		username = new TextField ("Username", "", 20, TextField.ANY);
		password = new TextField ("Password", "", 20, TextField.PASSWORD);
		
		cmdOK = new Command ("Login", Command.OK, 1);
		cmdExit = new Command ("Quit", Command.BACK, 0);
		cmdSettings = new Command ("Settings", Command.OK, 0);
		cmdLog = new Command ("Log", Command.OK, 0);
		
		/*String user = RecordStoreUtil.getInstance().searchRecordStoreByKey(USERNAME_KEY);
		if(!user.trim().equals("")){
			username.setString(user);
		}
		String pwd = RecordStoreUtil.getInstance().searchRecordStoreByKey(PASSWORD_KEY);
		if(!pwd.trim().equals("")){
			password.setString(pwd);
		}*/
		
		setCommandListener (this);
		addCommand (cmdOK);
		addCommand (cmdExit);
		addCommand(cmdSettings);
		addCommand(cmdLog);
		append (username);
		append (password);
	}

	protected ConnectionParameter createRequestPayload ()
	{
		ConnectionParameter payload = new ConnectionParameter();

		String usernameString = username.getString();
		String passwordString = password.getString();

		payload.addParam(WebServerConstants.PARAM_USERNAME, usernameString);
		payload.addParam(WebServerConstants.PARAM_PASSWORD, passwordString);

		return payload;
	}
	
	protected void cleanup() {
		deleteAll();
		removeCommand(cmdExit);
		removeCommand(cmdOK);
		removeCommand(cmdSettings);
		removeCommand(cmdLog);
	}

	public void response(Object otherInformation, Persistent persistentData) {
		getMainMidlet().setDisplay(this);
		if(persistentData != null && ((UserList)persistentData).size() > 0){
			UserList users = (UserList)persistentData;
			for (int i = 0; i < users.size(); i++) {
				OpenmrsDataStorage.saveXformUser(users.getUser(i));
			}

			trylogin(username.getString(), password.getString());
		}
		else if(otherInformation != null){
			UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION +"\nDetails: "+ otherInformation.toString(), null, this, getMainMidlet().getDisplay());
		}
		else{
			UIUtils.renderAlert(ErrorMessages.NO_USERS_DOWNLOADED, null, this, getMainMidlet().getDisplay());
		}
	}

	public void error(Object errorInformation) {
		UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, this, getMainMidlet().getDisplay());
	}
}
