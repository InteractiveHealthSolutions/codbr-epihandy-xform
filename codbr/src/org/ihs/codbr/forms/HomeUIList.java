package org.ihs.codbr.forms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.StudyData;
import org.fcitmuk.epihandy.StudyDataList;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.epihandy.StudyDefList;
import org.fcitmuk.epihandy.midp.db.EpihandyDataStorage;
import org.fcitmuk.openmrs.RequestHeader;
import org.fcitmuk.openmrs.UploadResponseList;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.Constants;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;
import org.ihs.logger.Log;


public class HomeUIList extends List implements CommandListener{
	
	public MainMidlet			mainMidlet;
	private Command				cmdOK;
	private Command				cmdExit;
	private Displayable			prevDisplayable;

	public HomeUIList (String title, MainMidlet mainMidlet)
	{
		super (title, Choice.IMPLICIT);
		this.mainMidlet = mainMidlet;
		cmdOK = new Command ("Choose", Command.OK, 0);
		cmdExit = new Command ("Back", Command.EXIT, 1);
	}

	public Displayable getPrevDisplayable ()
	{
		return prevDisplayable;
	}

	public void setPrevDisplayable (Displayable prevDisplayable)
	{
		this.prevDisplayable = prevDisplayable;
	}

	private void loadItems ()
	{
		for (int i = 0; i < HomeUIListItem.HOME_LIST_ITEMS.size(); i++) {
			HomeUIListItem item = (HomeUIListItem)HomeUIListItem.HOME_LIST_ITEMS.elementAt(i);
			item.INDEX = this.append(item.NAME, null);
		}
	}

	public void init ()
	{
		deleteAll ();
		loadItems ();

		addCommand (cmdOK);
		addCommand (cmdExit);
		setCommandListener (this);
	}

	public void commandAction (Command c, Displayable d)
	{
		final Displayable curdisp = this;
		if (c == cmdOK)
		{
			String itemName = getString (this.getSelectedIndex ());

			/*if (itemName.equals(HomeUIListItem.COHORTS.NAME))
			{
				UIUtils.startForm(mainMidlet.LST_COHORT, this, mainMidlet.getDisplay());
			}
			else if (itemName.equals(HomeUIListItem.PATIENTS.NAME))
			{
				UIUtils.startForm(mainMidlet.LST_PATIENT, this, mainMidlet.getDisplay());
			}
			else */
			if (itemName.equals(HomeUIListItem.XFORMS_DOWNLOAD.NAME))
			{
				// if no studies exists
				if(EpihandyDataStorage.getStudy(1) != null){
					UIUtils.showConfirm(UIConstants.TEXT_FORMS_EXISTS, new CommandListener() {
						public void commandAction(Command c, Displayable d) {
							//OpenMRS doesnot maintain studies list. there is only one default study with id 1. so nowhere we are using studylisting things
							EpihandyDataStorage.deleteData(EpihandyDataStorage.getStudy(1));
							downloadForms(curdisp);
							mainMidlet.setDisplay(curdisp);
						}
					}, new CommandListener() {
						public void commandAction(Command c, Displayable d) {mainMidlet.setDisplay(curdisp);}
					}, mainMidlet.getDisplay());
				}
				else {
					downloadForms(curdisp);
				}
			}
			else if (itemName.equals(HomeUIListItem.XFORMS_UPLOAD.NAME))
			{
				uploadForms(curdisp);
			}
			else if (itemName.equals(HomeUIListItem.XFORMS_DISPLAY.NAME))
			{
				//OpenMRS doesnot maintain studies list. there is only one default study with id 1. so nowhere we are using studylisting things
				// if no studies exists
				if(EpihandyDataStorage.getStudy(1) == null){
					UIUtils.showPopup(UIConstants.TEXT_NO_FORM_EXISTS, null, mainMidlet.getDisplay());
				}
				else {
					//OpenMRS doesnot maintain studies list. there is only one default study with id 1. so nowhere we are using studylisting things
					mainMidlet.getFormManager().mainDisplayable(curdisp);
					mainMidlet.getFormManager().showFormDefList(EpihandyDataStorage.getStudy(1));
				}
			}
			else if(itemName.equals(HomeUIListItem.CONNECTION_SETTING.NAME)){
				mainMidlet.getTransportLayer().getConnectionSettings().openSettingsEditor(mainMidlet.getDisplay(), d);
			}
			else if(itemName.equals(HomeUIListItem.FORM_SETTING.NAME)){
				mainMidlet.getFormManager().getFormSettings().openSettingsEditor(mainMidlet.getDisplay(), this);
			}
			else if(itemName.equals(HomeUIListItem.LOG.NAME)){
				Log.showLog(mainMidlet.getDisplay(), this);
			}
			else if(itemName.equals(HomeUIListItem.PATIENT_SEARCH.NAME)){
				UIUtils.startForm(mainMidlet.FRM_PATIENT_SEARCH, this, mainMidlet.getDisplay());
			}

		}
		else if (c == cmdExit)
		{
			UIUtils.showExitConfirmation(this, mainMidlet);
		}
	}
	
	public void uploadForms(final Displayable prevDisplayable) {
		final StudyDataList coldata = mainMidlet.getFormManager().getCollectedData();
		RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_UPLOAD_FORMS, mainMidlet.getCurrentXformUser().getName(), mainMidlet.getCurrentXformUser().getClearTextPassword(),	
				mainMidlet.getLocale(), new Object[] {coldata});
		
		mainMidlet.sendUploadRequest(WebServerConstants.SUBURL_XFORM_FORM_UPLOAD_MULTIPART, true,
				new ConnectionParameter(WebServerConstants.PARAM_XFORM_BATCH_ENTRY, "true"),false, 
					reqh, new UploadResponseList(), new ResponseNotification() {
					public void response(Object otherInformation, Persistent persistentData) {
						Hashtable uplerl = ((UploadResponseList) persistentData).getResponseList();
						
						for (int i = 0; i < coldata.getStudies().size(); i++) 
						{
							StudyData study = ((StudyData)coldata.getStudies().elementAt(i));
							Vector forms = study.getForms();
							for (int j = 0; j < forms.size(); j++) 
							{
								FormData frm = (FormData) forms.elementAt(j);
								
								QuestionData q = null;
								if((q = frm.getQuestionMatchingEnd(Constants.NODE_PATIENT_PATIENT_IDENTIFIER)) != null
										|| (q = frm.getQuestionMatchingEnd(Constants.NODE_PATIENT_IDENTIFIER)) != null
										|| (q = frm.getQuestionMatchingEnd(Constants.BINDING_PATIENT_ID)) != null
										|| (q = frm.getQuestionMatchingEnd(Constants.NODE_PATIENT_ID)) != null){
									String ans = (String) q.getAnswer();
									String ansStatus = (String) uplerl.get(ans);
									if(ansStatus != null && 
											(ansStatus.trim().toLowerCase().equals("success")
													|| ansStatus.trim().toLowerCase().startsWith("success"))){
										EpihandyDataStorage.deleteFormData(study.getId(), frm);
									}
								}
							}
						}
						
						String message = "Upload response is as follows. Forms submitted successfully has been deleted from phone, while forms with errors need to be resubmitted after correction.";
						
						Enumeration keys = uplerl.keys();
						while (keys.hasMoreElements()) {
							Object object = (Object) keys.nextElement();
							message += object + " : " + uplerl.get(object);
						}
						
						UIUtils.renderAlert(message, null, prevDisplayable, mainMidlet.getDisplay());
					}
					public void error(Object errorInformation) {
						UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, prevDisplayable, mainMidlet.getDisplay());
					}
				}, true);
	}
	
	public void downloadForms(final Displayable prevDisplayable){
		ConnectionParameter payload = new ConnectionParameter();
		payload.addParam(WebServerConstants.PARAM_XFORM_TARGET, "xforms");//TODO
		//payload.addParam(WebServerConstants.PARAM_XFORM_CONTENT_TYPE, "xml");
		payload.addParam(WebServerConstants.PARAM_XFORM_EXCLUDE_LAYOUT, "true");
		
		RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_FORMS, mainMidlet.getCurrentXformUser().getName(), mainMidlet.getCurrentXformUser().getClearTextPassword(), mainMidlet.getLocale(), null);
		mainMidlet.sendDownloadRequest(WebServerConstants.SUBURL_XFORM_FORM_DOWNLOAD, true, 
				payload, false, reqh, new StudyDef(), new ResponseNotification() {
					public void response(Object otherInformation, Persistent persistentData) {
						// we have only one study in the project so we wont follow the flow: download studylist and 
						// then on request download study and its forms. we are rather using one study 
						// and all its forms. but to follow epihandy structure we have to save studylist and then studydef
						StudyDef study = (StudyDef) persistentData;
						Vector studies = new Vector();
						studies.addElement(study);
						EpihandyDataStorage.saveStudyList(new StudyDefList(studies));
						EpihandyDataStorage.saveStudy(study);
						UIUtils.renderAlert(study.getForms().size()+ " forms downloaded.", null, prevDisplayable, mainMidlet.getDisplay());
					}
					public void error(Object errorInformation) {
						UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, prevDisplayable, mainMidlet.getDisplay());
					}
				}, true);
	}
}
