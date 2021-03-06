package org.fcitmuk.openmrs.forms;

import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import org.fcitmuk.communication.ConnectionSettings;
import org.fcitmuk.communication.TransportLayer;
import org.fcitmuk.communication.TransportLayerListener;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.EpihandyConstants;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.RequestHeader;
import org.fcitmuk.epihandy.ResponseHeader;
import org.fcitmuk.epihandy.midp.db.EpihandyDataStorage;
import org.fcitmuk.epihandy.midp.forms.DateSettings;
import org.fcitmuk.epihandy.midp.forms.FormListener;
import org.fcitmuk.epihandy.midp.forms.FormManager;
import org.fcitmuk.epihandy.midp.forms.GeneralSettings;
import org.fcitmuk.epihandy.midp.forms.LogonListener;
import org.fcitmuk.midp.db.util.StorageListener;
import org.fcitmuk.openmrs.Cohort;
import org.fcitmuk.openmrs.CohortList;
import org.fcitmuk.openmrs.MedicalHistoryField;
import org.fcitmuk.openmrs.MedicalHistoryValue;
import org.fcitmuk.openmrs.Patient;
import org.fcitmuk.openmrs.PatientData;
import org.fcitmuk.openmrs.PatientDownloadRequestHeader;
import org.fcitmuk.openmrs.PatientField;
import org.fcitmuk.openmrs.PatientFieldList;
import org.fcitmuk.openmrs.PatientFieldValueList;
import org.fcitmuk.openmrs.PatientForm;
import org.fcitmuk.openmrs.PatientMedicalHistory;
import org.fcitmuk.openmrs.VarNames;
import org.fcitmuk.openmrs.db.OpenmrsDataStorage;
import org.fcitmuk.util.AlertMessage;
import org.fcitmuk.util.AlertMessageListener;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.SettingList;
import org.fcitmuk.util.Utilities;

/** This is the main midlet that displays the main user inteface for openmrs. 
 * 
 * @author Daniel Kayiwa
 *
 */
public class MainForm extends MIDlet  implements CommandListener,FormListener,StorageListener,AlertMessageListener, TransportLayerListener,LogonListener{

	/** Reference to the current display. */
	private Display display;

	/** The main menu screen. */   
	private List mainList;

	/** Screen for displaying a list of patients searched. */
	private List patientList;

	/** Screen for displaying a list of cohorts. */
	private List cohortList;

	/** Screen for displaying details of the selected patient. eg Bio data, last visit date,etc. */
	private List patientDetails;

	private List medicalHistory;

	/** Screen for displaying a list of encounter forms. */
	//private List formList;

	/** Screen for entering patient search criteria. */
	private PatientSearchForm patientSearchForm = new PatientSearchForm("Search Patient" + " - " + TITLE,false);

	/** Index for search patient menu item. */
	private static final int INDEX_SEARCH_PATIENT = 0;

	/** Index for new patient menu item. */
	private static final int INDEX_NEW_PATIENT = 1;

	/** Index for selecting an encounter form menu item. */
	//private static final int INDEX_SELECT_FORM = 2;

	/** Index for downloading patients menu item. */
	private static final int INDEX_DOWNLOAD_COHORTS= 1;

	/** Index for downloading patients menu item. */
	private static final int INDEX_DOWNLOAD_PATIENTS = 2;

	/** Index for downloading forms menu item. */
	private static final int INDEX_DOWNLOAD_FORMS = 3;

	/** Index for uploading data menu item. */
	private static final int INDEX_UPLOAD_DATA = 4;

	/** Index for specifying settings like server connection parameters. */
	private static final int INDEX_SETTINGS = 5;

	/** Index for selecting a study menu item. */
	private static final int INDEX_LOGOUT = 6;

	/** Application title. */
	private static final String TITLE = "OpenMRS 1.6";

	/** Status to download a list of patients from the server. */
	public static final byte ACTION_DOWNLOAD_PATIENTS = 6;

	/** Status to download a list of cohorts from the server. */
	public static final byte ACTION_DOWNLOAD_COHORTS = 8;

	/** Status to download a list of cohorts from the server. */
	public static final byte ACTION_DOWNLOAD_FILTERED_PATIENTS = 15;


	/** List of patients. */
	private Vector patients;

	/** List of cohorts. */
	private Vector cohorts;

	private Vector historyFields;

	/** List of forms for the current study. */
	//private Vector forms;

	/** Reference to epihandy form manager. */
	private FormManager formMgr;

	/** Reference to the transportLayer. */
	private TransportLayer transportLayer;

	private AlertMessage alertMsg;

	/** No alert is currently displayed. */
	private static final byte CA_NONE = -1;

	/** Current alert is for patient download textconfirmation. */
	private static final byte CA_PATIENT_DOWNLOAD = 1;

	/** Current alert is for patient search. */
	private static final byte CA_PATIENT_SEARCH = 2;

	/** Current alert is for patient download confirmation. */
	private static final byte CA_COHORT_DOWNLOAD = 3;

	/** Current alert is for cohort select. */
	private static final byte CA_COHORT_SELECT = 4;

	private static final byte CA_FILTERED_PATIENT_DOWNLOAD = 5;

	private static final byte CA_PATIENT_SEARCH_MISSING_INFO = 6;

	private static final byte CA_CONFIRM_PATIENT_DELETE = 7;


	private static byte currentAction = CA_NONE;

	/** The current patient. */
	private Patient patient;
	
	/** the current CohortID **/
	private int cohortId=-1;

	/** The user manager object. */
	protected UserManager userMgr;

	/** The current selected index of the main menu. For now, this is used to keep track of
	 * the user's action to return to after successfully logging in. This happens when the user
	 * tries to do something before logging in, and the logon mananer intervenes by requiring the
	 * user to first login. This happens after downloading forms because a new list of users is got
	 * which makes void the current users info. */
	private int selectedIndex = EpihandyConstants.NO_SELECTION;

	//private boolean newPatientsCreated = false;

	private static final String KEY_LAST_SELECTED_MAIN_MENU_ITEM =  "LAST_SELECTED_MAIN_MENU_ITEM";
	private static final String STORAGE_NAME_SETTINGS = "STORAGE_NAME_OPENMRS_SETTINGS";

	private static String NAME_PATIENT_DOWNLOAD_URL = "Patients download url:";
	private static String NAME_COHORT_DOWNLOAD_URL = "Cohort download url:";

	private boolean exitConfirmMode = false;

    private static ConnectionSettings conSettings;
    
    private static final String KEY_CON_SETTINGS_SHOWN="connectionsettingsshown";
    
    private static final String VAL_TRUE="true";
    private static final String VAL_FALSE="false";
    
    private static ConnectionSettingsForm conForm;
    
    private static SettingList settings;
    
	/** Construct the main UI midlet. */

	public MainForm() {
		super();

		display = Display.getDisplay(this);
		initMainList();

		transportLayer = new TransportLayer(/*new EpihandyTransportLayer().getClass()*/);
		transportLayer.setDisplay(display);
		transportLayer.setPrevScreen(mainList);
		transportLayer.setDefaultCommnucationParameter(TransportLayer.KEY_BLUETOOTH_SERVER_ID, /*"F0E0D0C0B0A000908070605040302010"*/ "F0E0D0C0B0A000908070605040301111");
		transportLayer.setDefaultCommnucationParameter(TransportLayer.KEY_HTTP_URL, "");
		//String serverURL ="http://125.209.94.151:8080";
//		if(MenuText.getURL() != null && MenuText.getURL()!="")
//		{
//			serverURL = MenuText.getURL();
//		}
		//transportLayer.addConnectionParameter(new ConnectionParameter(TransportLayer.CON_TYPE_HTTP,NAME_PATIENT_DOWNLOAD_URL, serverURL+"/openmrs/module/xforms/patientDownload.form?downloadPatients=true"));
		//transportLayer.addConnectionParameter(new ConnectionParameter(TransportLayer.CON_TYPE_HTTP,NAME_COHORT_DOWNLOAD_URL,serverURL+"/openmrs/module/xforms/patientDownload.form?downloadCohorts=true"));

		formMgr = new FormManager(TITLE,display,this, mainList,transportLayer,this);
		FormManager.setGlobalInstance(formMgr);

		alertMsg = new AlertMessage(this.display, TITLE, this.mainList,this);

		EpihandyDataStorage.storageListener = this;
		OpenmrsDataStorage.storageListener = this;
		GeneralSettings.setDeleteDataAfterUpload(true);
		//Initialize the Settings 
		settings = new SettingList(STORAGE_NAME_SETTINGS,true);
		
	}
	
	
	private void initMainList(){
		//TODO These strings may need to be localised.
		mainList = new List(TITLE, Choice.IMPLICIT);
		mainList.insert(INDEX_SEARCH_PATIENT, "Search Patient", null);
		//mainList.insert(INDEX_NEW_PATIENT, "New Patient", null);
		//mainList.insert(INDEX_SELECT_FORM, "Select Form", null);
		mainList.insert(INDEX_DOWNLOAD_COHORTS, "Download Cohorts", null);
		mainList.insert(INDEX_DOWNLOAD_PATIENTS, "Download Patients", null);
		mainList.insert(INDEX_DOWNLOAD_FORMS, "Download Forms", null);
		mainList.insert(INDEX_UPLOAD_DATA, "Upload Data", null);
		mainList.insert(INDEX_SETTINGS, "Settings", null);
		mainList.insert(INDEX_LOGOUT, "Logout", null);

		mainList.addCommand(DefaultCommands.cmdSel);
		mainList.addCommand(DefaultCommands.cmdExit);

		SettingList settings = new SettingList(STORAGE_NAME_SETTINGS,true);
		String val = settings.getSetting(KEY_LAST_SELECTED_MAIN_MENU_ITEM);
		if(val != null)
			mainList.setSelectedIndex(Integer.parseInt(val),true);

		mainList.setCommandListener(this);
	}

	protected void destroyApp(boolean arg0) {
		settings.setSetting(KEY_CON_SETTINGS_SHOWN, VAL_FALSE);
		userMgr.logOut();
	}

	protected void pauseApp() {

	}

	protected void startApp() {
		//if(!connectionSettingsShown())
		//{
		//	userMgr = new UserManager(display,conForm = new ConnectionSettingsForm("Connection Settings", display, (Displayable)mainList),TITLE,this);		
		//	settings.setSetting(KEY_CON_SETTINGS_SHOWN, VAL_TRUE);
		//	settings.saveSettings();
	//	}
	//	else
	//	{
			userMgr = new UserManager(display,mainList,TITLE,this);			
	//	}
		userMgr.logOn();
		formMgr.setUserManager(userMgr);
			
		//display.setCurrent(mainList);
	
	}
	
//	private void deletePreviousUserData()
//	{
//		String usrName = userMgr.getUserName();
//		String lastUsrName = userMgr.getPreviousUserName();
//		if (usrName.equals(lastUsrName))
//		{
//			settings.setSetting(KEY_CON_SETTINGS_SHOWN, VAL_TRUE);
//			settings.saveSettings();		
//			return;
//		}
//		//delete all patients
//		OpenmrsDataStorage.deleteAllPatients();
//		//delete all cohorts
//		OpenmrsDataStorage.deleteAllCohorts();
//		
//		//show connections settings to the new user for the first time use		
//		conForm = new ConnectionSettingsForm("Connection Settings", display, (Displayable)mainList);
//		display.setCurrent(conForm);
//		settings.setSetting(KEY_CON_SETTINGS_SHOWN, VAL_TRUE);
//		settings.saveSettings();			
//	}
	
	//Method to check whether the User has been shown connection settings on the first log in
	private boolean connectionSettingsShown()
	{		
		String str = settings.getSetting(KEY_CON_SETTINGS_SHOWN);//delete this string after debugging and use directly instead!
		if(str!=null && str.toLowerCase().equals(VAL_TRUE.toLowerCase()))
		{
			return true;
		}
		return false;	
	}
	
	private void showConnectionSettings(Display display, Displayable screen, boolean forcedView)
	{		
		if(!forcedView && connectionSettingsShown())
			return;
		formMgr.displayUserSettings(display, mainList);
		transportLayer.setCurrentAlert(null);
		transportLayer.getUserSettings(display,screen ,userMgr.getUserName(),userMgr.getPassword());
	}

	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			if (c == DefaultCommands.cmdExit)
				handledExitCommand();
			else if(c == List.SELECT_COMMAND)
				handleListSelectCommand(((List)d).getSelectedIndex());
			else if(c == DefaultCommands.cmdCancel)
				handledCancelCommand(d);
			else if(c == DefaultCommands.cmdSel || c == DefaultCommands.cmdOk)
				handleOkCommand(d);
			else if(c == DefaultCommands.cmdBack)
				handledCancelCommand(d);
			else if(c == DefaultCommands.cmdDelete)
				handleDeleteCommand(d);
		}
		catch(Exception e){
			alertMsg.showError(e.getMessage());
			//e.printStackTrace();
		}
	}

	/**
	 * Handles the back command.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	/*private void handledBackCommand(Displayable d){
		//display.setCurrent(mainList);
		handledCancelCommand(d);
	}*/

	/**
	 * Handles the cancel command.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handledCancelCommand(Displayable d){
		if(d == patientDetails)
			display.setCurrent(patientList);
		else if(d == patientList)
			display.setCurrent(patientSearchForm);
		else if(d == medicalHistory)
			display.setCurrent(patientDetails);
		else
			this.display.setCurrent(mainList);
	}

	/**
	 * Handles the exit command.
	 *
	 */
	private void handledExitCommand(){
		exitConfirmMode = true;
		alertMsg.showConfirm("Do you really want to exit the application and lose any unsaved changes if any?");
	}

	/**
	 * Handles the list selection command.
	 * 
	 * @param selectedIndex - the index of the selected item.
	 */
	private void handleListSelectCommand(int selectedIndex){
		Displayable currentScreen = display.getCurrent();

		if(currentScreen == mainList)
			handleMainListSelectCommand(selectedIndex);
		else if(currentScreen == patientList)
			selectPatient(selectedIndex);
		else if(currentScreen == patientDetails){
			if(selectedIndex < 7)
				formMgr.selectForm(true, display.getCurrent());
			else
				displayMedicalHistory(selectedIndex);
		}
		else if(currentScreen == cohortList)
			startPatientDownload(((Cohort)cohorts.elementAt(selectedIndex)).getId());
	}

	/**
	 * Handles the main list selection command.
	 * 
	 * @param selectedIndex - the index of the selected item.
	 */
	private void handleMainListSelectCommand(int selectedIndex){

		this.selectedIndex = selectedIndex;

		if(!userMgr.isLoggedOn()){
			userMgr.logOn();
			return;
		}
		
		RequestHeader.setSerializer("xforms.xformSerializer");

		switch(selectedIndex){
		/*case INDEX_NEW_PATIENT:        		
			createNewPatient();
			break;*/
		case INDEX_SEARCH_PATIENT:
			RequestHeader.setSerializer("xforms.patientSerializer"); //May need to download them
			searchPatient();
			break;
		case INDEX_DOWNLOAD_FORMS:
			formMgr.downloadForms(mainList);
			break;
		case INDEX_UPLOAD_DATA:
			this.formMgr.uploadData(mainList);
			break;
		case INDEX_DOWNLOAD_PATIENTS:			
			RequestHeader.setSerializer("xforms.patientSerializer");
			downloadPatients();		
			break;
		case INDEX_DOWNLOAD_COHORTS:
			RequestHeader.setSerializer("xforms.cohortSerializer");
			downloadCohorts();			
			//startCohortPatientFormDownload();			
			break;
		case INDEX_LOGOUT:
			logout();
			break;
		case INDEX_SETTINGS:
			//bypass all other settings and only show connection
			showConnectionSettings(display, display.getCurrent(),true);
			break;
		}

		SettingList settings = new SettingList(STORAGE_NAME_SETTINGS,true);
		settings.setSetting(KEY_LAST_SELECTED_MAIN_MENU_ITEM, String.valueOf(selectedIndex));
		settings.saveSettings();
	}

	/** Downloads patiens from the server. */
	private void downloadPatients(){
		currentAction = CA_PATIENT_DOWNLOAD;
		alertMsg.showConfirm("Do you really want to download patients?");
		//onAlertMessage(AlertMessageListener.MSG_OK);
	}

	/** Downloads cohorts from the server. */
	private void downloadCohorts(){
		currentAction = CA_COHORT_DOWNLOAD;
		alertMsg.showConfirm("Do you really want to download cohorts?");
		//onAlertMessage(AlertMessageListener.MSG_OK);
	}

	/** Displays a list of patients according to the user search criteria of id and name. */
	private void displayPatientList(boolean listPopulated){
		patientList = new List("Patients" + " - " + TITLE,Choice.IMPLICIT);
		patientList.addCommand(DefaultCommands.cmdSel);
		patientList.addCommand(DefaultCommands.cmdBack);
		patientList.addCommand(DefaultCommands.cmdDelete);

		currentAction = CA_PATIENT_SEARCH;

		boolean includeServerSearch = false; String id = null, name = null;
		if(!listPopulated){
			id = patientSearchForm.getId();
			name = patientSearchForm.getName();

			if(id != null) id = id.trim();
			if(name != null) name = name.trim();

			includeServerSearch = patientSearchForm.includeServerSearch();
			if(includeServerSearch){
				if((id == null || id.length() == 0) && 
						(name == null || name.length() == 0)){
					currentAction = CA_PATIENT_SEARCH_MISSING_INFO;
					alertMsg.show("Please enter the Identifier or name of the patient to search from server.");
					return;
				}
				else if(name != null && name.length() < 3 && name.length() > 0){
					currentAction = CA_PATIENT_SEARCH_MISSING_INFO;
					alertMsg.show("Please enter atleast three characters for the name of the patient to search.");
					return;
				}
			}

			patients  = OpenmrsDataStorage.getPatients(id,name);
		}

		if(patients != null && patients.size() > 0){
			for(int i=0; i<patients.size(); i++)
				patientList.append(((Patient)patients.elementAt(i)).toString(),null);

			patientList.setCommandListener(this);
			display.setCurrent(patientList);
		}
		else if(!includeServerSearch){
			currentAction = CA_PATIENT_SEARCH_MISSING_INFO;
			alertMsg.show("No patients found. You may need to download patients first.");
		}
		else
			startFilteredPatientDownload(id,name);

		//alertMsg.show("No patients found. If you have just uploaded data including new patients, you may need to download patents again.");
	}

	/** Displays a list of cohorts for the user to select one. */
	private void displayCohortList(){
		cohortList = new List("Cohort Select" + " - " + TITLE,Choice.IMPLICIT);
		cohortList.addCommand(DefaultCommands.cmdOk);
		cohortList.addCommand(DefaultCommands.cmdCancel);

		currentAction = CA_COHORT_SELECT;

		cohorts  = getCohorts();
		if(cohorts != null && cohorts.size() > 0){
			for(int i=0; i<cohorts.size(); i++)
				cohortList.append(((Cohort)cohorts.elementAt(i)).toString(),null);

			cohortList.setCommandListener(this);
			display.setCurrent(cohortList);
		}
		else
			alertMsg.show("No cohorts found. Please first download the list of cohorts.");
	}

	/**
	 * Handles the ok command.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleOkCommand(Displayable d){
		if(d == patientSearchForm)
			displayPatientList(false);
		else if(d == patientDetails)
			handleListSelectCommand(patientDetails.getSelectedIndex());
		else if(d == patientList)
			handleListSelectCommand(patientList.getSelectedIndex());
		else if(d == cohortList)
			handleListSelectCommand(cohortList.getSelectedIndex());
		else if(d == mainList)
			handleListSelectCommand(mainList.getSelectedIndex());	
	}

	/**
	 * Handles the delete command.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleDeleteCommand(Displayable d){
		if(d != patientList) //patientDetails
			return;

		patient = (Patient)patients.elementAt(patientList.getSelectedIndex());
		currentAction = CA_CONFIRM_PATIENT_DELETE;
		alertMsg.showConfirm("Do you really want to remove " + patient + " from this device?");
	}

	/** 
	 * Displays details for the selected patient.
	 * These details, for now, are bio data, but could include things like last visit date, etc.
	 * 
	 * @param selectedIndex - the index of the currently selected patient.
	 */
	private void selectPatient(int selectedIndex){
		patientDetails = new List("Patient Details" + " - " + TITLE,Choice.IMPLICIT);
		patientDetails.addCommand(DefaultCommands.cmdOk);
		patientDetails.addCommand(DefaultCommands.cmdCancel);

		patient = (Patient)patients.elementAt(selectedIndex);
		fillPatientDetails(patientDetails);

		patientDetails.setCommandListener(this);	
		display.setCurrent(patientDetails);
	}

	/**
	 * Fills a list with patient details.
	 * 
	 * @param patientDetails - the list to fill with patient details.
	 * @param pt - the patient whose details to fill the list with.
	 */
	private void fillPatientDetails(List patientDetails){
		String s = "";
		if(patient.getPatientIdentifier()!= null)
			s = patient.getPatientIdentifier();
		patientDetails.append("PatientID: " + s, null);

		s = "";
		if(patient.getPrefix() != null)
			s = patient.getPrefix();
		patientDetails.append("Prefix: " + s, null);

		s = "";
		if(patient.getFamilyName() != null)
			s = patient.getFamilyName();
		patientDetails.append("FamilyName: " + s, null);

		s = "";
		if(patient.getGivenName() != null)
			s = patient.getGivenName();
		patientDetails.append("GivenName: " + s, null);

		s = "";
		if(patient.getMiddleName() != null)
			s = patient.getMiddleName();
		patientDetails.append("MiddleName: " + s, null);

		s = "";
		if(patient.getGender() != null)
			s = patient.getGender();
		patientDetails.append("Gender: " + s, null);

		s = "";
		if(patient.getBirthDate() != null)
			s = Utilities.dateToString(patient.getBirthDate(),DateSettings.getDateFormat());
		patientDetails.append("BirthDate: " + s, null);
		
		//Populate medical history
		PatientMedicalHistory history = OpenmrsDataStorage.getMedicalHistory(patient.getPatientId().intValue());
		if(history == null)
			return;
		
		historyFields = history.getHistory();
		if(historyFields == null || historyFields.size() == 0)
			return;

		for(int i = 0; i < historyFields.size(); i++){
			MedicalHistoryField field = (MedicalHistoryField)historyFields.elementAt(i);
			patientDetails.append(field.getFieldName(), null);
		}
		//patientDetails.append("Medical History:", null);
	}

	/** 
	 * Loads cohorts from persistent storage. 
	 * 
	 */
	private Vector getCohorts(){
		Vector list = null;

		CohortList cohorts = OpenmrsDataStorage.getCohorts();
		if(cohorts != null)
			list = cohorts.getCohorts();

		return list;
	}
	
		
	/** Displays the search patient form. */
	private void searchPatient(){
		patientSearchForm.removeCommand(DefaultCommands.cmdOk);
		patientSearchForm.removeCommand(DefaultCommands.cmdCancel);
		
		patientSearchForm.addCommand(DefaultCommands.cmdOk);
		patientSearchForm.addCommand(DefaultCommands.cmdCancel);
		patientSearchForm.setCommandListener(this);
		display.setCurrent(patientSearchForm);
	}

	/**
	 * Called by the epihandy form manager when a form has been closed without saving.
	 * 
	 * @param data - the data in the form that has been cancelled.
	 */
	public void afterFormCancelled(FormData data){
		Alert alert = new Alert("FormCancelled","The form has not been saved",null,AlertType.CONFIRMATION);
		alert.setTimeout(Alert.FOREVER);
		//display.setCurrent(alert);
	}

	/**
	 * Called by the epihandy form manager when a form is about to be displayed.
	 * 
	 * @param data - the data in the form that is to be displayed.
	 * 
	 */
	public boolean beforeFormDisplay(FormData data){
		if(patient != null && data.isNew()){
			data.setTextValue(VarNames.FORM_PATIENT_NAME, patient.getName());
			data.setTextValue(VarNames.FORM_FAMILY_NAME, patient.getFamilyName());
			data.setTextValue(VarNames.FORM_GIVEN_NAME, patient.getGivenName());
			data.setTextValue(VarNames.FORM_MIDDLE_NAME, patient.getMiddleName());
			data.setTextValue(VarNames.FORM_PREFIX, patient.getPrefix());
			data.setTextValue(VarNames.FORM_GENDER, patient.getGender());
			data.setValue(VarNames.FORM_PATIENT_ID, patient.getPatientId());
			data.setDateValue(VarNames.FORM_ENCOUNTER_DATETIME, new java.util.Date());
			data.setDateValue(VarNames.FORM_BIRTHDATE, patient.getBirthDate());
			data.setOptionValue(VarNames.FORM_PROVIDER_ID, String.valueOf(userMgr.getUserId()));
			data.setOptionValueIfOne(VarNames.FORM_LOCATION_ID);

			PatientFieldList fields = OpenmrsDataStorage.getPatientFields();
			PatientFieldValueList fieldVals = OpenmrsDataStorage.getPatientFieldValues();
			if(fields != null && fieldVals != null){
				for(int i=0; i<fields.size(); i++){
					PatientField field = fields.getField(i);
					if(data.containsQuestion(field.getName()))
						data.setValue(field.getName(), fieldVals.getPatintFiledValue(field.getId(), patient.getPatientId()));
				}
			}
		}

		return true;
	}

	public boolean beforeFormDataListDisplay(FormDef formDef){
		boolean display = true;
		FormData data;
		if(patient != null){
			display = false;
			int formDataRecordId = OpenmrsDataStorage.getPatientFormRecordId(patient.getPatientId(), formDef.getId());
			if(formDataRecordId != EpihandyConstants.NULL_ID)
			{
				data =EpihandyDataStorage.getFormData(EpihandyConstants.DEFAULT_STUDY_ID, formDef.getId(), formDataRecordId);
				if(data!=null) //data was not deleted by user - Omar 15/6/11
					formMgr.showForm(false,EpihandyConstants.DEFAULT_STUDY_ID, formDef, formDataRecordId,true);
				else
					formMgr.showForm(false,new FormData(formDef),false);		
			}else
				formMgr.showForm(false,new FormData(formDef),false);
		}

		return display;
	}

	/*public void afterFormDisplay(FormData data, boolean save){

	}*/

	public boolean beforeQuestionEdit(QuestionData data){
		return true;
	}

	public boolean afterQuestionEdit(QuestionData data){
		return true;
	}

	public boolean beforeFormCancelled(FormData data){
		return true;
	}

	public boolean beforeFormSaved(FormData formData,boolean isNew){

		if(isNew && formData.getDef().getVariableName().equals(VarNames.PATIENT_FORM)){
			Patient pt = new Patient();
			pt.setGender(formData.getOptionValue(VarNames.PATIENT_GENDER));
			pt.setBirthDate(formData.getDateValue(VarNames.PATIENT_BIRTH_DATE));
			pt.setFamilyName(formData.getTextValue(VarNames.PATIENT_FAMILY_NAME));
			pt.setGivenName(formData.getTextValue(VarNames.PATIENT_GIVEN_NAME));
			pt.setMiddleName(formData.getTextValue(VarNames.PATIENT_MIDDLE_NAME));

			pt.setNewPatient(true);
			pt.setPatientIdentifier(formData.getTextValue(VarNames.PATIENT_IDENTIFIER));
			pt.setPrefix(formData.getOptionValue(VarNames.PATIENT_PREFIX));

			OpenmrsDataStorage.savePatient(pt);

			formData.setValue(VarNames.PATIENT_PATIENT_ID, pt.getPatientId());
		}

		return true;
	}

	public boolean beforeFormDelete(FormData data){
		return true;
	}

	public void afterFormDelete(FormData data){
		if(patient != null)
			OpenmrsDataStorage.deletePatientForm(patient.getPatientId(), data.getDefId());
		//alertMsg.show("Form data deleted successfully.");
	}

	/**
	 * @see org.fcitmuk.epihandy.midp.forms.FormListener#afterFormSaved(org.fcitmuk.epihandy.FormData,java.lang.boolean)
	 */
	public void afterFormSaved(FormData formData, boolean isNew){

		if(isNew && patient != null)
			OpenmrsDataStorage.savePatientForm(formData.getDefId(), new PatientForm(patient.getPatientId(),formData.getRecordId()));

		alertMsg.show("Form Saved Successfully.");
	}


	/**
	 * Called when an error occurs during any operation.
	 * 
	 * @param errorMessage - the error message.
	 * @param e - the exception, if any, that did lead to this error.
	 */
	public void errorOccured(String errorMessage, Exception e){
		if(currentAction == CA_FILTERED_PATIENT_DOWNLOAD)
			currentAction = CA_PATIENT_SEARCH_MISSING_INFO;
		else
			currentAction = CA_NONE; //if not set to this value, the alert will be on forever.

		if(e != null)
			errorMessage += " : "+ e.getMessage();
		alertMsg.showError(errorMessage);
	}

	public void cancelled(){
		display.setCurrent(mainList);
	}

	public void onAlertMessage(byte msg){
		if(exitConfirmMode){
			if(msg == AlertMessageListener.MSG_OK)
				exit();
			else
				alertMsg.turnOffAlert();

			exitConfirmMode = false;
		}
		else{
			if(msg == AlertMessageListener.MSG_OK){
				if(currentAction == CA_PATIENT_DOWNLOAD)
					displayCohortList();
				else if(currentAction == CA_COHORT_DOWNLOAD){
					alertMsg.showProgress("Cohort Download","Downloading Cohorts");
					startCohortDownload();
				}
				else if(currentAction == CA_PATIENT_SEARCH_MISSING_INFO)
					searchPatient();
				else if(currentAction == CA_CONFIRM_PATIENT_DELETE)
					deleteSelectedPatient();
				else
					alertMsg.turnOffAlert();
			}
			else{
				if(currentAction == CA_CONFIRM_PATIENT_DELETE){
					currentAction = CA_PATIENT_SEARCH;
					display.setCurrent(patientList);
				}
				else{
					currentAction = CA_NONE;
					alertMsg.turnOffAlert();
				}
			}
		}
	}

	private void deleteSelectedPatient(){
		OpenmrsDataStorage.deletePatient(patient);
		OpenmrsDataStorage.deletePatientForms(patient.getPatientId(), formMgr.getStudyList());

		patientList.delete(patientList.getSelectedIndex());
		patients.removeElement(patient);

		if(patients.size() > 0){
			currentAction = CA_PATIENT_SEARCH;
			display.setCurrent(patientList);
		}
		else
			this.display.setCurrent(patientSearchForm);
	}

	private void startFilteredPatientDownload(String id, String name){
		currentAction = CA_FILTERED_PATIENT_DOWNLOAD;
		PatientDownloadRequestHeader comnParam = new PatientDownloadRequestHeader(PatientDownloadRequestHeader.FILTER_TYPE_NAME_AND_IDENTIFIER);
		comnParam.setAction(ACTION_DOWNLOAD_FILTERED_PATIENTS);
		comnParam.setUserName(userMgr.getUserName());
		comnParam.setPassword(userMgr.getPassword());
		comnParam.setIdentifier(id);
		comnParam.setName(name); 
		transportLayer.setCommnucationParameter(TransportLayer.KEY_HTTP_URL, transportLayer.getConnectionParameterValue(TransportLayer.CON_TYPE_HTTP, NAME_PATIENT_DOWNLOAD_URL)+"&uname="+userMgr.getUserName()+"&pw="+userMgr.getPassword()+"&identifier="+id+"&name="+name);
		transportLayer.download(comnParam, null, new ResponseHeader(), new PatientData(), this,userMgr.getUserName(),userMgr.getPassword());		
	}

	private void startPatientDownload(int cohortId){
		currentAction = CA_PATIENT_DOWNLOAD;
		PatientDownloadRequestHeader comnParam = new PatientDownloadRequestHeader(PatientDownloadRequestHeader.FILTER_TYPE_COHORT);
		comnParam.setAction(ACTION_DOWNLOAD_PATIENTS);
		comnParam.setUserName(userMgr.getUserName());
		comnParam.setPassword(userMgr.getPassword());
		comnParam.setCohortId(cohortId);
		
		
		//String url = "http://localhost:8080/openmrs/module/xforms/patientDownload.form?downloadPatients=true&uname="+userMgr.getUserName()+"&pw="+userMgr.getPassword();
		if(transportLayer.getServerURL().equals(""))
			return;
		transportLayer.setCommnucationParameter(TransportLayer.KEY_HTTP_URL, transportLayer.getServerURL()+"/module/xforms/patientDownload.form?downloadPatients=true"+"&uname="+userMgr.getUserName()+"&pw="+userMgr.getPassword()+"&cohortId="+cohortId);
		transportLayer.download(comnParam, null, new ResponseHeader(), new PatientData(), this,userMgr.getUserName(),userMgr.getPassword());		
		//transportLayer.downloadPatients(comnParam, null, new ResponseHeader(), new PatientData(), this,userMgr.getUserName(),userMgr.getPassword());
	}

	private void startCohortDownload(){
		RequestHeader comnParam = new RequestHeader();
		comnParam.setAction(ACTION_DOWNLOAD_COHORTS);
		comnParam.setUserName(userMgr.getUserName());
		comnParam.setPassword(userMgr.getPassword());
		if(transportLayer.getServerURL().equals(""))
			return;
		//																		http://server:8080/openmrs/module/xforms/patientDownload.form?downloadCohorts=true&serializerKey=xforms.cohortSerializer&uname=name&pw=password
		transportLayer.setCommnucationParameter(TransportLayer.KEY_HTTP_URL,transportLayer.getServerURL()+"/module/xforms/patientDownload.form?downloadCohorts=true&serializerKey=xforms.cohortSerializer&uname="+userMgr.getUserName()+"&pw="+userMgr.getPassword());		
		transportLayer.download(comnParam, null, new ResponseHeader(), new CohortList(), this,userMgr.getUserName(),userMgr.getPassword());		
	}
	

	/**
	 * Called after data has been successfully downloaded.
	 * 
	 * @param dataOutParams - the parameters sent with the data.
	 * @param dataOut - the downloaded data.
	 */
	public void downloaded(Persistent dataOutParams, Persistent dataOut){
		if(currentAction == CA_PATIENT_DOWNLOAD || currentAction == CA_FILTERED_PATIENT_DOWNLOAD){
			String message = "Problem saving downloading patients";
			try{
				if(dataOut != null && ((PatientData)dataOut).getPatients() != null && ((PatientData)dataOut).getPatients().size() > 0){
					if(currentAction == CA_FILTERED_PATIENT_DOWNLOAD){
						OpenmrsDataStorage.savePatientData((PatientData)dataOut,true);
						patients = ((PatientData)dataOut).getPatients().getPatients();
						displayPatientList(true);
						return;
					}
					else{
						OpenmrsDataStorage.savePatientData((PatientData)dataOut,false);
						message = ((PatientData)dataOut).getPatients().size() + " Patient(s) downloaded and saved successfully";
					}
				}
				else
					message = "No patient found.";

				if(currentAction == CA_FILTERED_PATIENT_DOWNLOAD){
					currentAction = CA_PATIENT_SEARCH_MISSING_INFO;
					alertMsg.show(message);
					return;
				}

			}catch(Exception e){
				message += e.getMessage();
			}

			currentAction = CA_NONE;				
			alertMsg.show(message);
		}	
		else if(currentAction == CA_COHORT_DOWNLOAD){
			String message = "Problem saving downloaded cohorts";
			try{
				CohortList filteredCohorts;
				if(dataOut != null && ((CohortList)dataOut).getCohorts() != null && ((CohortList)dataOut).getCohorts().size() > 0){
					filteredCohorts = filterCohorts((CohortList)dataOut);
					if(filteredCohorts.getCohorts().size() == 1)
					{
						//set the cohortid for the currently logged in user
						cohortId=filteredCohorts.getCohort(0).getId();
						OpenmrsDataStorage.saveCohorts(filteredCohorts);
						message = filteredCohorts.getCohorts().size() + " Cohort(s) downloaded and saved successfully";				
						
						//download patients
						//RequestHeader.setSerializer("xforms.patientSerializer");
						//startPatientDownload(cohortId);
						
						//download forms
						//DownloadUploadManager duManager = new DownloadUploadManager(transportLayer, controller, title, transportLayerListener);
						//formMgr.downloadForms(null);
						
					}
					else message ="No cohorts available for this user";
				}
				else
					message = "No cohorts downloaded.";
			}catch(Exception e){
				message += e.getMessage();
				alertMsg.show(message);
			}

			currentAction = CA_NONE;				
			alertMsg.show(message);
		}
		//else //Must be forms download because we have only two kinds of downloads.
		//	userMgr.logOut();
	}
	/**
	 * @param downloadCohorts - The cohorts which are downloaded and need to be filtered
	 * @author Omar Ahmed @ IRD
	 * @return List of cohorts for the signed in user
	 * Date: 30-7-10
	 */
	private CohortList filterCohorts(CohortList downloadedCohorts)
	{
		CohortList filteredCohorts = new CohortList();
		Cohort cohort;
		
		for(int i=0; i < downloadedCohorts.getCohorts().size();i++)
		{
			cohort =  downloadedCohorts.getCohort(i);
			if(cohort.getName().toLowerCase().equals(userMgr.getUserName().toLowerCase()))
			{
				filteredCohorts.addCohort(cohort);
			}
		}
		return filteredCohorts;
	}

	//not used for now
	public void uploaded(Persistent dataOutParams, Persistent dataOut){
		OpenmrsDataStorage.deletePatientForms(formMgr.getStudyList());
		OpenmrsDataStorage.deleteNewPatients();

		//when user closes and reopens, this is reset hence introducing a bug.
		//if(newPatientsCreated) //If nay new patients were created, user, we need to redownload patients to get their server assigned patient ids.

		//For costly data transfers like sms, we dont need to unnecessarily force users
		//to redownload patients every after a data upload.
		//OpenmrsDataStorage.deletePatients();
		//newPatientsCreated = false;
	}

	public boolean onLoggedOn(){
		boolean displayPrevScreen = false;

		/*String[] connections = PushRegistry.listConnections(true);
		if(connections != null && connections.length > 0)
			transportLayer.handleIncomingSmsData(connections[0]);
		else{*/
		
		//delete previous data
		//deletePreviousUserData();
		
		/*
		 * FROM DELETEPREVIOUS DATA METHOD
		 */
		String usrName = userMgr.getUserName();
		String lastUsrName = userMgr.getPreviousUserName();
		if (!usrName.equals(lastUsrName))
		{
			//delete all patients
			OpenmrsDataStorage.deleteAllPatients();
			//delete all cohorts
			OpenmrsDataStorage.deleteAllCohorts();
			
//			conForm = new ConnectionSettingsForm("Connection Settings", display, (Displayable)mainList);
//			display.setCurrent(conForm);
//			settings.setSetting(KEY_CON_SETTINGS_SHOWN, VAL_TRUE);
//			settings.saveSettings();			
//			displayPrevScreen = false;
//			return displayPrevScreen;
		}	
		
//		if(selectedIndex != EpihandyConstants.NO_SELECTION)
//			handleMainListSelectCommand(selectedIndex);
//		else
			formMgr.displayUserSettings(display, mainList);
			transportLayer.setCurrentAlert(null);
			transportLayer.getUserSettings(display,(Displayable)mainList ,userMgr.getUserName(),userMgr.getPassword());
			
			displayPrevScreen = false;

		return displayPrevScreen;
	}

	public void onLogonCancel(){
		if(selectedIndex == EpihandyConstants.NO_SELECTION)
			exit();
		else
			display.setCurrent(mainList);
	}

	private void logout(){
		/** If this is not reset, after loggin in, we shall wrongly execute an action that
		 * the user did not intend to.*/
		this.selectedIndex = EpihandyConstants.NO_SELECTION;
		userMgr.logOut();
		userMgr.logOn();
	}

	public boolean beforeFormDefListDisplay(Vector formDefList){
		for(int i=0; i<formDefList.size(); i++){
			if(((FormDef)formDefList.elementAt(i)).getVariableName().equals(VarNames.PATIENT_FORM)){
				formDefList.removeElementAt(i);
				break;
			}
		}
		return true;
	}	

	private void displayMedicalHistory(int selectedIndex){
		MedicalHistoryField field = (MedicalHistoryField)historyFields.elementAt(selectedIndex - 7);

		medicalHistory = new List(field.getFieldName() + " - " + patient.getName(),Choice.IMPLICIT);
		medicalHistory.addCommand(DefaultCommands.cmdBack);

		Vector values = field.getValues();
		for(int index = values.size() - 1; index >= 0 ; index--){
			MedicalHistoryValue fieldValue = (MedicalHistoryValue)values.elementAt(index);
			medicalHistory.append(fieldValue.getValue() + " -> " + Utilities.dateToString(fieldValue.getValueDate(),DateSettings.getDateFormat()), null);
		}

		medicalHistory.setCommandListener(this);	
		display.setCurrent(medicalHistory);
	}

	public void updateCommunicationParams(){

	}

	private void exit(){
		destroyApp(true);
		notifyDestroyed();
	}
}

//Patient table field attributes server load,phone save,phone populate, and delete on patient download.
//create new patient form
//allow creation of new patients together with entry for new forms
//submit all this new patient data correctly to the server.
//Patient details should be in new patient form
//separation of connection parameters and working on url parameters append of login info.