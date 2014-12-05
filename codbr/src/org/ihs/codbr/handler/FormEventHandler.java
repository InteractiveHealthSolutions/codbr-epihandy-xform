package org.ihs.codbr.handler;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.PageData;
import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.epihandy.midp.forms.FormListener;
import org.fcitmuk.util.Setting;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.util.Utils;
import org.ihs.xform.StringUtils;

public class FormEventHandler implements FormListener, ResponseNotification{
	private MainMidlet mainMidlet;
	private StringBuffer xmlModel = new StringBuffer();
	private StudyDef study;

	private static final String LAST_FORM_SAVED_DATE_KEY = "LAST_FORM_SAVED_DATE";
	private static final String LAST_FORM_SAVED_SERIAL_KEY = "LAST_FORM_SAVED_SERIAL";
	private static final String LAST_FORM_SAVED_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public FormEventHandler(MainMidlet mainMidlet) {
		this.mainMidlet = mainMidlet;
	}
	
	public boolean beforeFormCancelled(FormData data) {
		// TODO Auto-generated method stub
		return true;
	}

	public void afterFormCancelled(FormData data) {
		// TODO Auto-generated method stub
		
	}

	public boolean beforeFormSaved(FormData data, boolean isNew) {
		//if(isNew){
			//EpihandyDataStorage.saveFormData(study.getId(), data);
		//}
/*		final String str = EpihandyXform.fromFormData2XformModel(data);
		final String str2 = EpihandyXform.updateXformModel(EpihandyXform.getDocument(new StringReader(xmlModel.toString())), data);
		str2.toLowerCase();

		//RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_UPLOAD_FORMS, mainMidlet.getCurrentXformUser().getName(), mainMidlet.getCurrentXformUser().getClearTextPassword(), mainMidlet.getLocale(), new Object[]{str});
		mainMidlet.sendDownloadRequest(WebServerConstants.SUBURL_XFORM_FORM_UPLOAD_XML, true, 
				new ConnectionParameter(WebServerConstants.PARAM_XFORM_BATCH_ENTRY, "true"), false, new Persistent() {
					
					public void write(DataOutputStream dos) throws IOException {
						dos.writeByte(1);
						dos.writeUTF(str2);
					}
					
					public void read(DataInputStream dis) throws IOException,
							InstantiationException, IllegalAccessException {
						// TODO Auto-generated method stub
						
					}
				}, new XformTempList(), this, false);
		return false;*/
		return true;
	}

	public void afterFormSaved(FormData data, boolean isNew) {
		if(isNew){
			Setting lastFormSavedDate = mainMidlet.getFormManager().getFormSettings().getSettingList().getSetting(LAST_FORM_SAVED_DATE_KEY);
			Setting lastFormSavedSerial = mainMidlet.getFormManager().getFormSettings().getSettingList().getSetting(LAST_FORM_SAVED_SERIAL_KEY);
			
			int lastSerial = getIdSerial(lastFormSavedDate, lastFormSavedSerial);
			if(lastFormSavedDate == null){
				lastFormSavedDate = new Setting(LAST_FORM_SAVED_DATE_KEY, "", LAST_FORM_SAVED_DATE_KEY, "", "", "", "false", "false");
				lastFormSavedSerial = new Setting(LAST_FORM_SAVED_SERIAL_KEY, "", LAST_FORM_SAVED_SERIAL_KEY, "", "", "", "false", "false");
			}
					
			lastFormSavedDate.setValue(Utils.formatDate(new Date(), LAST_FORM_SAVED_DATE_FORMAT));
			lastFormSavedSerial.setValue(Integer.toString(lastSerial));
			
			mainMidlet.getFormManager().getFormSettings().getSettingList().addSetting(lastFormSavedDate);
			mainMidlet.getFormManager().getFormSettings().getSettingList().addSetting(lastFormSavedSerial);
			mainMidlet.getFormManager().getFormSettings().getSettingList().saveSettings();
		}
	}

	private int getIdSerial(Setting lastFormDate, Setting lastFormSerial){
		int lastSerial = 0;
		if(lastFormDate != null){
			Date lastDate = Utils.parseDate(lastFormDate.getValue(), LAST_FORM_SAVED_DATE_FORMAT);
			String tempformat = "yyyyMMdd";
			if(Utils.formatDate(lastDate, tempformat).equals(Utils.formatDate(new Date(), tempformat))){
				lastSerial = Integer.parseInt(lastFormSerial.getValue());
			}
		}
		return lastSerial+1;
	}
	
/*	public boolean beforeFormDisplay(FormData data, String xmlModel) {
		this.xmlModel = new StringBuffer(xmlModel);
		return true;
	}*/
	
	private String makeId(){
		Setting lastFormSavedDate = mainMidlet.getFormManager().getFormSettings().getSettingList().getSetting(LAST_FORM_SAVED_DATE_KEY);
		Setting lastFormSavedSerial = mainMidlet.getFormManager().getFormSettings().getSettingList().getSetting(LAST_FORM_SAVED_SERIAL_KEY);

		StringBuffer id = new StringBuffer();
		id.append(StringUtils.leftPad(Integer.toString(mainMidlet.getCurrentXformUser().getUserId()), 5, '0'));
		id.append(Utils.formatDate(new Date(), "yyMMddHHmm"));
		id.append(StringUtils.leftPad(""+getIdSerial(lastFormSavedDate, lastFormSavedSerial), 3, '0'));
				
		return id.toString();
	}

	public boolean beforeFormDisplay(FormData data) {
		if(data.isNew()){
			Hashtable autocalvals = new Hashtable();
			for(byte i=0; i<data.getPages().size(); i++){
				PageData pageData = (PageData)data.getPages().elementAt(i);
				for(short j=0; j<pageData.getQuestions().size(); j++){
					QuestionData qtnData = (QuestionData)pageData.getQuestions().elementAt(j);
					if(qtnData.getDef().getVariableName().toLowerCase().endsWith("patient.identifier")){
						autocalvals.put(qtnData.getDef().getVariableName(), makeId());
					}
				}	
			}
			data.setAutocalculatedValues(autocalvals);
		}
		return true;
	}

	public boolean beforeQuestionEdit(QuestionData data) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean afterQuestionEdit(QuestionData data) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean beforeFormDataListDisplay(FormDef formDef, StudyDef studyDef) {
		study = studyDef;
		return true;
	}

	public boolean beforeFormDelete(FormData data) {
		// TODO Auto-generated method stub
		return true;
	}

	public void afterFormDelete(FormData data) {
		// TODO Auto-generated method stub
		
	}

	public boolean beforeFormDefListDisplay(Vector formDefList) {
		// TODO Auto-generated method stub
		return true;
	}
	public void response(Object otherInformation, Persistent persistentData) {
		Class clz = persistentData.getClass();
		clz.getName();
	}
	public void error(Object errorInformation) {
		// TODO Auto-generated method stub
		
	}

}
