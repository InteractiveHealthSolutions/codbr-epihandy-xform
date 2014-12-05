package org.ihs.codbr.forms;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.PatientData;
import org.fcitmuk.openmrs.PatientList;
import org.fcitmuk.openmrs.RequestHeader;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;
import org.ihs.codbr.util.Utils;


/**
 * 
 * @author daniel
 *
 */
public class PatientSearchUIForm extends BaseUIForm implements ResponseNotification{

	private static final String KEY_INCLUDE_SERVER_SEARCH = "INCLUDE_SERVER_SEARCH";
	
	private TextField patientId;
	private TextField patientName;
	private ChoiceGroup cohortcg;
	private ChoiceGroup cgSearchType;
	private Command cmdOK;
	private Command cmdExit;
	
	public void init(Displayable prvDisplayable) {
		setPrevDisplayable(prvDisplayable);
		
		//this.flagServerSearch = searchOnServer;
		patientId = new TextField("Patient Identifier:","",100,TextField.ANY);
		patientName = new TextField("Name:","",100,TextField.ANY);
		cgSearchType = new ChoiceGroup("Search including",Choice.MULTIPLE);
		cgSearchType.append("Server", null);
		
		cohortcg = new ChoiceGroup("Specify Cohort",Choice.EXCLUSIVE);
		Utils.loadCohorts(cohortcg);
		
		cmdOK = new Command ("Choose", Command.OK, 0);
		cmdExit = new Command ("Back", Command.EXIT, 1);
		//Settings settings = new Settings(GeneralSettings.STORAGE_NAME_SETTINGS,true);
		//cgSearchType.setSelectedIndex(0, Utilities.stringToBoolean(settings.getSetting(KEY_INCLUDE_SERVER_SEARCH)));			

		append(patientId);
		append(patientName);
		append(cohortcg);
		append(cgSearchType);
		
		addCommand (cmdOK);
		addCommand (cmdExit);
		setCommandListener (this);
	}
	
	public PatientSearchUIForm(String title, MainMidlet mainMidlet) {
		super(title, mainMidlet);
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == cmdOK)
		{
			RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_FILTERED_PATIENTS, getMainMidlet().getCurrentXformUser().getName(), getMainMidlet().getCurrentXformUser().getClearTextPassword(), getMainMidlet().getLocale(), new Object[]{patientName.getString(), patientId.getString()});
			getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_PATIENT_DOWNLOAD, true, 
				createRequestPayload(), false, reqh, new PatientData(), this, true);
		}
		else if (c == cmdExit)
		{
			getMainMidlet().startMainMenu();
		}
	}
	
	protected ConnectionParameter createRequestPayload() {
		ConnectionParameter payload = new ConnectionParameter();

		payload.addParam(WebServerConstants.PARAM_DOWNLOAD_PATIENT, "true");
		//payload.addParam(WebServerConstants.PARAM_SERIALIZER_KEY, RequestHeader.XFORMS_PATIENT_SERIALIZER);
		
		return payload;
	}
	
	/*public String getId(){
		return this.txtId.getString();
	}
	
	public void setId(String id){
		this.txtId.setString(id);
	}
	
	public String getName(){
		return this.txtName.getString();
	}
	
	public void setName(String name){
		this.txtId.setString(name);
	}*/
	
	public boolean includeServerSearch(){
		//if(this.flagServerSearch)
		//{
			//Settings settings = new Settings(GeneralSettings.STORAGE_NAME_SETTINGS,true);		
			//settings.setSetting(KEY_INCLUDE_SERVER_SEARCH,Utilities.booleanToString((cgSearchType.isSelected(0))));
			//settings.saveSettings();

			//return cgSearchType.isSelected(0);
		//}
		return false;
	}

	protected boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}
	
	public void response(Object otherInformation, Persistent persistentData) {
		PatientList dpat = (PatientList) persistentData;
		if(dpat != null && dpat.size() > 0){
			OpenmrsDataStorage.savePatients(dpat);
		}
	}

	public void error(Object errorInformation) {
		UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, this, getMainMidlet().getDisplay());
	}
}
