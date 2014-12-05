/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy.midp.forms;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.epihandy.EpihandyConstants;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.QuestionDef;
import org.fcitmuk.epihandy.SkipRule;
import org.fcitmuk.epihandy.StudyData;
import org.fcitmuk.epihandy.StudyDataList;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.epihandy.StudyDefList;
import org.fcitmuk.epihandy.ValidationRule;
import org.fcitmuk.epihandy.midp.db.EpihandyDataStorage;
import org.fcitmuk.midp.db.util.StorageListener;
import org.fcitmuk.midp.mvc.Controller;
import org.fcitmuk.midp.mvc.View;
import org.fcitmuk.util.AlertMessage;
import org.fcitmuk.util.AlertMessageListener;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;
import org.fcitmuk.util.Setting;


/**
 * Manages cordination of views within the application. It knows all the views.
 * Views do not know about each other. All they know is the controller and as a result
 * send requests to it. These requests normally require switching of views and its the 
 * controller that knows which view to switch to.
 * In otherwards, views speak to each other through the controller.
 * 
 * @author Daniel Kayiwa
 *
 */
public class EpihandyController implements Controller, StorageListener, AlertMessageListener,TypeEditorListener {

	private boolean studyEditingMode;
	private FormListener formEventListener;
	private Displayable prevScreen;
	private AlertMessage alertMsg;
	private Hashtable transitionTable;
	private View currentView;
	private Display display;

	/** A list of user defined data type editors. */
	private Hashtable typeEditors = new Hashtable(); //TODO This need to be implemmted such that we allow flexibilty of user defined type editors for those who dont want to subclass the default type editor.

	private FormView formViewer = new FormView();
	private FormDefListView formDefListViewer = new FormDefListView();
	private FormDataListView formDataListViewer = new FormDataListView();
	private TypeEditor typeEditor = new DefaultTypeEditor();
	private StudyListView studyListViewer = new StudyListView();
	private RptQtnsDataListView rptQtnsDataListViewer = new RptQtnsDataListView();

	private FormManager formManager;
	
	public FormManager getFormManager() {
		return formManager;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public EpihandyController(){

	}

	public void init(String title, Display display,FormListener formEventListener, FormManager formManager){
		this.formEventListener = formEventListener;
		this.display = display;
		this.formManager = formManager;

		setDefaults(title);
		
		alertMsg = new AlertMessage(display, title, this.prevScreen, this);
		transitionTable = new Hashtable();
	}

	private void setDefaults(String title){
		studyListViewer.setController(this);
		formDefListViewer.setController(this);
		formDataListViewer.setController(this);
		formViewer.setController(this);
		typeEditor.setController(this);

		studyListViewer.setDisplay(display);
		formDefListViewer.setDisplay(display);
		formDataListViewer.setDisplay(display);
		formViewer.setDisplay(display);
		typeEditor.setDisplay(display);

		studyListViewer.setTitle(title);
		formDefListViewer.setTitle(title);
		formDataListViewer.setTitle(title);
		formViewer.setTitle(title);
		typeEditor.setTitle(title);
	}

	public void setPrevScreen(Displayable prevScreen){
		this.prevScreen = prevScreen;
	}

	public void setStudyEditingMode(boolean studyEditingMode){
		this.studyEditingMode = studyEditingMode;
	}

	private void showErrorMessage(String text, Exception e){
		/*if(e != null)
			e.printStackTrace();*/
		alertMsg.showError(text);
	}

	/**
	 * Shows a form given its data.
	 */
	public void showForm(boolean studyEditingMode,FormData formData, boolean allowDelete){
		try{			
			setStudyEditingMode(studyEditingMode);
			boolean displayForm = true;
			displayForm = formEventListener.beforeFormDisplay(formData);

			if(displayForm){
				FireSkipRules(formData);
				this.formViewer.showForm(formData,formEventListener,allowDelete);
				saveCurrentView(formViewer);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			showErrorMessage("Exception:"+ e.getMessage(),e);
		}
	}

	/**
	 * Shows a form given its variable name.
	 */
	public void showForm(boolean studyEditingMode,String formVarName,boolean showNew){
		
		/* TODO MAIMOONA if(!formsDownloaded2())
			return;*/
		
		FormDef def = formDefListViewer.getStudy().getForm(formVarName);
		if(def == null){
			showErrorMessage(MenuText.NO_FORM_DEF() + " " + formVarName,null);
			return;
		}

		setStudyEditingMode(studyEditingMode);
		if(showNew){
			FormData data = new FormData(def);
			FireSkipRules(data);
			showForm(studyEditingMode,data,false);
		}
		else
			showFormDataList(def);
	}


	public void startEdit(QuestionData currentQuestion,int pos, int count){		
		//Inform the user that we are about to start editing.
		boolean edit = true;
		edit = formEventListener.beforeQuestionEdit(currentQuestion);

		//Check to see if the user (of the API) has not cancelled editing.
		if(!edit)
			return;

		TypeEditor editor = typeEditor;
		Byte type = new Byte(currentQuestion.getDef().getType());
		if(typeEditors.containsKey(type))
			editor = (TypeEditor)typeEditors.get(type);

		FormData formData = formViewer.getFormData();
		ValidationRule rule = formData.getDef().getValidationRule(currentQuestion.getId());
		if(rule != null)
			rule.setFormData(formData);
		editor.setTitle(formData.getDef().getName()+ " - " + formViewer.getTitle());
		editor.startEdit(currentQuestion,rule, getFormManager().getFormSettings().isSingleQtnEdit(),pos,count,this);
		//no need to save the current view since its managed by the form viewer.
	}

	/**
	 * Sets a custom editor of a question type.
	 * 
	 * @param type - the question type.
	 * @param typeEditor - the editor.
	 */
	public void setTypeEditor(byte type, TypeEditor typeEditor){
		this.typeEditors.put(new Byte(type), typeEditor);
	}

	/** Stops editing of a question. */
	public void endEdit(boolean save, QuestionData data, Command cmd){
		if(save){
			save = this.formEventListener.afterQuestionEdit(data);
			
			if(save){
				FireSkipRules(formViewer.getFormData());
				formViewer.getFormData().buildQuestionDataDescription();
				
				int type = data.getDef().getType();
				if(type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
					formViewer.getFormData().updateDynamicOptions(data,false);
				
				//handling separate for GPS type - Omar 12-7-11
				if(type == QuestionDef.QTN_TYPE_GPS)
				{
					//call methods to fill Longitude and Latitude from the answer - Omar 12-7-11
					if(data != null){
						String answer = (String)data.getAnswer();
						int pos1 = answer.indexOf(",");
						String latitude = answer.substring(0,pos1);
						
						int pos2 = answer.lastIndexOf(',');
						String longitude = answer.substring(pos1+1,pos2);
						
						QuestionDef qDef=null;
						QuestionData qData =null;
						FormData frmData = this.formViewer.getFormData();
						
						//latitude
						qDef =frmData.getDef().getQuestion("Latitude");
						
						//Question Exists
						if(qDef!=null){ 
						
							//get Data for the question
							qData=frmData.getQuestion(qDef.getId());
							qData.setTextAnswer(latitude);
						}
						
						//longitude
						qDef =frmData.getDef().getQuestion("Longitude");
						
						//Question Exists
						if(qDef!=null){ 
						
							//get Data for the question
							qData=frmData.getQuestion(qDef.getId());
							qData.setTextAnswer(longitude);
						}
					}

				}
			}
		}

		this.formViewer.onEndEdit(save == true,cmd);
		//no saving of current view since it was type editor displayed.
	}

	/** Fires rules in the form. */
	private void FireSkipRules(FormData formData){		
		Vector rules = formData.getDef().getSkipRules();
		if(rules != null && rules.size() > 0){
			for(int i=0; i<rules.size(); i++){
				//EpiHandySkipRule rule = (EpiHandySkipRule)rules.elementAt(i);
				SkipRule rule = (SkipRule)rules.elementAt(i);
				rule.fire(formData);
			}
		}
	}

	public void saveForm(FormData formData){
		//Give the user of the API a chance to do custom validation.
		boolean save = true;
		boolean isNew = formData.isNew();
		if(formEventListener != null)
			save = formEventListener.beforeFormSaved(formData,isNew); //Give the API user a chance to do some custom validations.
		
		if(save){
			// MAIMOONA web xforms` calculation field doenot work hence a hack to allow default value field to be used as a flag to copy value of some other question
			formViewer.getFormData().setCalculationDefault();
			
			saveFormData(formData);
			formEventListener.afterFormSaved(formData,isNew);
		}
	}

	/** Saves the current form data. */
	private void saveFormData(FormData formData){	
		boolean isNew = formData.isNew();
		
		formData.setDateValue("/"+formData.getDef().getVariableName()+"/endtime", new Date());
		
		if(EpihandyDataStorage.saveFormData(formDefListViewer.getStudy().getId(),formData)){
			currentView = (View)transitionTable.get(formViewer);
			transitionTable.remove(formViewer);

			if(this.studyEditingMode)
				formDataListViewer.onFormSaved(formData, isNew);
			else
				display.setCurrent(this.prevScreen);
		}
	}

	//garisa lodge
	public void deleteForm(FormData formData, View sender){
		if(sender == formViewer && formDataListViewer.hasSelectedForm()){
			formDataListViewer.deleteCurrentForm();
			handleCancelCommand(sender);
		}
		else{
			boolean delete = true;
			delete  = formEventListener.beforeFormDelete(formData);

			if(delete){
				EpihandyDataStorage.deleteFormData(formDefListViewer.getStudy().getId(),formData);
				formEventListener.afterFormDelete(formData);
				handleCancelCommand(sender);
			}
		}
	}

	public Vector getStudyList(){
		return studyListViewer.getStudyList();
	}

	void showStudies(){
		if(this.studyListViewer.getStudyList() == null || this.studyListViewer.getStudyList().size() == 0){
			StudyDefList studyDefList = EpihandyDataStorage.getStudyList();
			if(studyDefList != null && studyDefList.getStudies() != null){
				this.studyListViewer.setStudyList(studyDefList.getStudies());
			}
			else{
				this.studyListViewer.setStudyList(new Vector());
			}
		}
		
		this.studyListViewer.showStudyList();
	}

	public void showFormDefList(StudyDef studyDef){
		studyDef = loadStudyForms(studyDef);

		//Save settings for next run
		//Settings settings = new Settings(EpihandyConstants.STORAGE_NAME_EPIHANDY_SETTINGS,true);
		getFormManager().getFormSettings().getSettingList().addSetting(new Setting(EpihandyConstants.KEY_LAST_SELECTED_STUDY,String.valueOf(studyDef.getId()), EpihandyConstants.KEY_LAST_SELECTED_STUDY, "", "", "", "false", "false"));
		getFormManager().getFormSettings().getSettingList().saveSettings();

		this.formDefListViewer.showFormList(studyDef,formEventListener);
		saveCurrentView(formDefListViewer);
	}

	public void showFormDataList(FormDef formDef){
		boolean display = true;
		StudyDef currentStudy = formDefListViewer.getStudy();
		FormDef frmDef = new FormDef(formDef);
		display = formEventListener.beforeFormDataListDisplay(frmDef, currentStudy);

		if(display){
			this.formDataListViewer.showFormList(frmDef, EpihandyDataStorage.getFormData(currentStudy.getId(), formDef.getId()));
			saveCurrentView(formDataListViewer);
		}
	}

	/**
	 * Gets a study definition having forms instead of a blank one which just lists 
	 * the name and some little meta data about a study. Such blank studies
	 * are contained in StudyDefList objects.
	 * 
	 * @param studyDef a default study without forms, to return just incase none is found with forms.
	 * @return the study with forms if found, else the passed in default.
	 */
	private StudyDef loadStudyForms(StudyDef studyDef){
		StudyDef retStudyDef = EpihandyDataStorage.getStudy(studyDef.getId());

		//If no forms saved yet, this will be null, hence we preserve the
		//blank study, to hold study info for the time being, until when we have forms
		if(retStudyDef == null)
			retStudyDef = studyDef;

		return retStudyDef;
	}

	public Vector getForms(){
		if(formDefListViewer.getStudy() != null)
			return this.formDefListViewer.getStudy().getForms();
		return null;
	}

	public void errorOccured(String errorMessage, Exception e){
		if(e != null)
			errorMessage += " : " + e.getMessage();
		showErrorMessage(errorMessage,e);
	}

	/**
	 * This is callback when one hits the Ok button for an alert message.
	 */
	public void onAlertMessage(byte msg){
		display.setCurrent(prevScreen);
	}

	/**
	 * Any view where the user hits the cancel command, calls into this method
	 * to allow the controller display the previous view.
	 */
	public void handleCancelCommand(Object viewer){
		View view = (View)transitionTable.get(viewer);
		if(view != null){
			transitionTable.remove(viewer);
			view.show(); //AbstractView.getDisplay().setCurrent(view.getScreen());
			currentView = view;
		}
		else{
			display.setCurrent(prevScreen);
			currentView = null;
		}
	}

	/**
	 * Before displaying a view, saves the one which was current.
	 * This is for rembering view to diplay on closing one or pressing the back button.
	 * 
	 * @param newView - the view which is to be displayed.
	 */
	private void saveCurrentView(View newView){
		if(currentView != null) // && !(view.equals(currentView))
			transitionTable.put(newView, currentView);

		currentView = newView;
	}

	public void showForm(boolean studyEditingMode,int studyId,FormDef formDef, int formDataRecordId,boolean allowDelete){
		FormData formData = EpihandyDataStorage.getFormData(studyId, formDef.getId(), formDataRecordId);
		formData.setDef(formDef);
		showForm(studyEditingMode,formData,allowDelete);
	}

	public void execute(View view, Object cmd, Object data){
		if(cmd == DefaultCommands.cmdCancel){
			display.setCurrent(prevScreen);
		}
		else if(view instanceof StudyListView && cmd == DefaultCommands.cmdOk){
			showFormDefList((StudyDef)data);
		}
	}

	public void backToMainMenu(){
		display.setCurrent(prevScreen);
	}
	
	public StudyDataList getCollectedData() {
		StudyDefList studyList = EpihandyDataStorage.getStudyList();
		if(studyList  == null)
			return null;

		StudyDataList studyDatalist = new StudyDataList();
		for (byte i = 0; i < studyList.size(); i++){
			StudyDef studyDef = (StudyDef) studyList.getStudy(i);
			//Study list always has no forms, so we have to get them from the database.
			studyDef = EpihandyDataStorage.getStudy(studyDef.getId());

			//If no forms downloaded yet, then we don't expect any data to save.
			if(studyDef != null){
				StudyData studyData = getCollectedStudyData(studyDef);
				if(studyData != null)
					studyDatalist.addStudy(studyData);
			}
		}

		return studyDatalist;
	}
	
	private StudyData getCollectedStudyData(StudyDef studyDef) {
		StudyData studyData = new StudyData(studyDef.getId());
		Vector formDefs = studyDef.getForms();
		if (formDefs != null && formDefs.size() > 0) {
			for (int i = 0; i < formDefs.size(); i++) {
				FormDef formDef = ((FormDef) formDefs.elementAt(i));
				Vector formDatas = EpihandyDataStorage.getFormData(studyDef.getId(), formDef.getId());
				if (formDatas != null) {
					setFormDefs(formDatas, formDef); // These are for writing to stream but they are not persisted.
					studyData.addForms(formDatas);
				}
			}
			if (studyData.getForms() != null && studyData.getForms().size() > 0)
				return studyData;
		}

		return null;
	}
	
	private void setFormDefs(Vector formDatas, FormDef formDef) {
		for (int i = 0; i < formDatas.size(); i++) {
			FormData formData = (FormData) formDatas.elementAt(i);
			formData.setDef(formDef);
		}
	}
}
