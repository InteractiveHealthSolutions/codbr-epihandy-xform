package org.ihs.codbr.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.Patient;
import org.fcitmuk.openmrs.PatientList;
import org.fcitmuk.openmrs.RequestHeader;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;

public class PatientUIList extends BaseUIList implements ResponseNotification{
	
	private Command cmdOK;
	private Command cmdExit;
	private Command cmdDownload;
	private Command cmdDelete;
	
	private PatientList dataitems = new PatientList();

	public PatientUIList (String title, MainMidlet mainMidlet)
	{
		super (title, mainMidlet);
		cmdOK = new Command ("Choose", Command.OK, 0);
		cmdExit = new Command ("Back", Command.EXIT, 1);
		cmdDownload = new Command ("Download", Command.OK, 0);
		cmdDelete = new Command ("Delete", Command.OK, 1);
	}

	protected void loadItems ()
	{
		PatientList stored = OpenmrsDataStorage.getPatientList();
		if(stored != null){
			dataitems = stored;
			
			for (int i = 0; i < dataitems.size(); i++) {
				Patient pat = ((Patient)dataitems.getPatient(i));
				append(pat.getName(), null);
			}
		}
	}
	
	public void init (Displayable previousDisplayable)
	{
		setPrevDisplayable(previousDisplayable);
		
		deleteAll ();
		loadItems ();

		addCommand (cmdOK);
		addCommand (cmdExit);
		addCommand(cmdDownload);
		addCommand(cmdDelete);
		setCommandListener (this);
	}
	
	public void commandAction (Command c, Displayable d)
	{
		final PatientUIList curdisp = this;
		if (c == cmdOK)
		{
			/*if (itemName.equals(HomeListItem.UPDATE_VACCINATION.NAME))
			{
				mainMidlet.mridQueryForm.setFormType (HomeListItem.UPDATE_VACCINATION.getFormType());
			}

			mainMidlet.startForm(mainMidlet.mridQueryForm, this);*/
		}
		else if (c == cmdExit)
		{
			getMainMidlet().startMainMenu();
		}
		else if(c == cmdDownload){
			RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_PATIENTS, getMainMidlet().getCurrentXformUser().getName(), getMainMidlet().getCurrentXformUser().getClearTextPassword(), getMainMidlet().getLocale(), new Object[]{new Integer(1)});
			getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_PATIENT_DOWNLOAD, true, 
					createRequestPayload(-1), false, reqh, new PatientList(), this, true);

		}
		else if(c == cmdDelete){
			UIUtils.showConfirm(UIConstants.TEXT_DELETE_PATIENT_CONFIRM, new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					OpenmrsDataStorage.deletePatient(dataitems.getPatient(getString (curdisp.getSelectedIndex ())));
					loadItems();
				}
			}, null, getMainMidlet().getDisplay());
		}
	}

	protected ConnectionParameter createRequestPayload(int type) {
		ConnectionParameter payload = new ConnectionParameter();

		payload.addParam(WebServerConstants.PARAM_COHORT_ID, "1");//TODO

		
		payload.addParam(WebServerConstants.PARAM_DOWNLOAD_PATIENT, "true");
		//payload.addParam(WebServerConstants.PARAM_SERIALIZER_KEY, RequestHeader.XFORMS_PATIENT_SERIALIZER);
		
		return payload;
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}

	public void response(Object otherInformation, Persistent persistentData) {
		PatientList dpat = (PatientList) persistentData;
		if(dpat != null && dpat.size() > 0){
			OpenmrsDataStorage.savePatients(dpat);
			loadItems();
		}
		else if(otherInformation != null){
			UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION +"\nDetails: "+ otherInformation.toString(), null, this, getMainMidlet().getDisplay());
		}
		else{
			UIUtils.renderAlert(ErrorMessages.NO_PATIENTS_DOWNLOADED, null, this, getMainMidlet().getDisplay());
		}
	}

	public void error(Object errorInformation) {
		UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, this, getMainMidlet().getDisplay());
	}
}
