package org.ihs.codbr;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.communication.ConnectionRequest;
import org.fcitmuk.communication.TransportLayer;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.midp.forms.FormManager;
import org.fcitmuk.midp.db.util.StorageListener;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.User;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.forms.CohortUIList;
import org.ihs.codbr.forms.HomeUIList;
import org.ihs.codbr.forms.LoginUIForm;
import org.ihs.codbr.forms.PatientSearchUIForm;
import org.ihs.codbr.forms.PatientUIList;
import org.ihs.codbr.forms.XformUIList;
import org.ihs.codbr.handler.FormEventHandler;
import org.ihs.codbr.handler.ResponseHandler;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;
import org.ihs.codbr.util.Utils;
import org.ihs.logger.Log;

public class MainMidlet extends MIDlet implements CommandListener{

	private User currentXformUser;
	private Display mobileDisplay;
	private TransportLayer transportLayer;
	private FormManager formManager;
	private Command EXIT;

	public HomeUIList LST_HOME;
	public CohortUIList LST_COHORT;
	public PatientUIList LST_PATIENT;
	public XformUIList LST_XFORM;
	public PatientSearchUIForm FRM_PATIENT_SEARCH;
	public LoginUIForm FRMLOGIN;
	private String locale = "en";
	
	/**
	 * DOnot call setConnectionParams of ConnectionRequest after it otherwise authentication params would be lost.
	 * @param req
	 */
	public void addAuthenticationParams(ConnectionRequest req){
		req.getConnectionParameter().addParam(WebServerConstants.PARAM_USERNAME, getCurrentXformUser().getName());
		req.getConnectionParameter().addParam(WebServerConstants.PARAM_PASSWORD, getCurrentXformUser().getClearTextPassword());
	}
	
	public void startMainMenu ()
	{
		setDisplay (LST_HOME);
	}
	
	public void sendDownloadRequest(String suburl, boolean addAuthenticationParams, ConnectionParameter connectionParams, boolean async, Persistent requestHeader, Persistent downloadable, ResponseNotification notifierHandler, boolean isZippedStreamExpected) {
		ConnectionRequest req = new ConnectionRequest(true, suburl, connectionParams, requestHeader, downloadable, isZippedStreamExpected);
		
		if(addAuthenticationParams) addAuthenticationParams(req);
		
		/*if(!isConnectionAvailable){
			showAlert(ErrorMessages.CONNECTION_BUSY, null);
			return;
		}TODO */
		if(!async){
			//showProgressBar("Downloading..............");
		}
		getTransportLayer().download(new ResponseHandler(notifierHandler, false), req);
		//isConnectionAvailable = false;
	}
	
	public void sendUploadRequest(String suburl, boolean addAuthenticationParams, ConnectionParameter connectionParams, boolean async, Persistent uploadable, Persistent dataResposne, ResponseNotification notifierHandler, boolean isZippedStreamExpected) {
		ConnectionRequest req = new ConnectionRequest(true, suburl, connectionParams, uploadable, dataResposne, isZippedStreamExpected);
		
		if(addAuthenticationParams) addAuthenticationParams(req);
		
		/*if(!isConnectionAvailable){
			showAlert(ErrorMessages.CONNECTION_BUSY, null);
			return;
		}TODO */
		if(!async){
			//showProgressBar("Downloading..............");
		}
		getTransportLayer().upload(new ResponseHandler(notifierHandler, false), req);
		//isConnectionAvailable = false;
	}
	
	public void sendSimpleRequest(String suburl, boolean addAuthenticationParams, ConnectionParameter connectionParams, boolean async, ResponseNotification notifierHandler, boolean isZippedStreamExpected){
		ConnectionRequest req = new ConnectionRequest(true, suburl, connectionParams, null, null, isZippedStreamExpected);
		if(addAuthenticationParams) addAuthenticationParams(req);
		getTransportLayer().sendRequest(new ResponseHandler(notifierHandler, false), req);
	}
	
	public User getCurrentXformUser() {
		return currentXformUser;
	}
	public void setCurrentXformUser(User currentXformUser) {
		this.currentXformUser = currentXformUser;
	}


	public TransportLayer getTransportLayer() {
		return transportLayer;
	}


	public void setTransportLayer(TransportLayer transportLayer) {
		this.transportLayer = transportLayer;
	}

	public FormManager getFormManager() {
		return formManager;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public String getLocale() {
		return locale ;
	}
	
	public void setDisplay(Displayable d) {
		mobileDisplay.setCurrent(d);
	}
	
	public Display getDisplay() {
		return mobileDisplay;
	}
	
	public MainMidlet() {
		try{
			Log.configureLogger("Console");
			this.mobileDisplay = Display.getDisplay(this);
			Log.showLog(mobileDisplay, null);
			Log.LOGGER().debug("Initiating Communication Layer and Form Manager");
			this.transportLayer = new TransportLayer(getDisplay());
			this.formManager = new FormManager("COD-BR", getDisplay(), new FormEventHandler(this));
			Log.LOGGER().debug("Initiated transport and form manager layer");
			
			Log.LOGGER().debug("Initiating UI elements");
			FRMLOGIN = new LoginUIForm(UIConstants.TITLE_LOGIN_FORM, this);
			FRM_PATIENT_SEARCH = new PatientSearchUIForm(UIConstants.TITLE_PATIENT_SEARCH_FORM, this);
			LST_HOME = new HomeUIList(UIConstants.TITLE_HOME_LIST, this);
			//Home list wont change throughout the application hence initiating once
			LST_HOME.init ();
			LST_COHORT = new CohortUIList(UIConstants.TITLE_COHORT_LIST, this);
			LST_PATIENT = new PatientUIList(UIConstants.TITLE_PATIENT_LIST, this);
			LST_XFORM = new XformUIList(UIConstants.TITLE_XFORM_LIST, this);
			Log.LOGGER().debug("Initiated UI elements");
			
			this.EXIT = new Command("Exit", Command.EXIT, 0x01);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.showLog(mobileDisplay, null);
			Log.LOGGER().error("Error constructing application", e);
		}
	}
	
	protected void startApp() throws MIDletStateChangeException {
		try{
			
			FRMLOGIN.init (null);
			if(Utils.isFirstLaunch()){
				Log.LOGGER().error("Launching first time... Going to settings...");
				getTransportLayer().getConnectionSettings().openSettingsEditor(mobileDisplay, FRMLOGIN);
			}
			else{
				setDisplay(FRMLOGIN);
			}
			
			OpenmrsDataStorage.storageListener = new StorageListener() {
				public void errorOccured(String errorMessage, Exception e) {
					UIUtils.showPopup(errorMessage, null, getDisplay());
				}
			};
			Log.LOGGER().error("Storage listner configured");
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.showLog(mobileDisplay, null);
			Log.LOGGER().error("Error starting application", e);
		}
		
	}
	

	
	public void destroyApp(boolean unconditional) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	public void commandAction(Command command, Displayable displayable) {
		if (command == this.EXIT) {
			this.notifyDestroyed();
		}
	}

}
