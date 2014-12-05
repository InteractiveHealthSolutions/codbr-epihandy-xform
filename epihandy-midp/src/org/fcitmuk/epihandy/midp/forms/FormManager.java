/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy.midp.forms;


import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.QuestionDef;
import org.fcitmuk.epihandy.StudyDataList;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;


/** 
 * Handles display of forms for data entry. This class acts as a facade to the epihandy forms engine.
 * This class uses the epihandy controller to manage view interactions with the user.
 * It also handles security issues.
 * 
 * @author Daniel Kayiwa
 *
 */
public class FormManager {
	
	/** Refefence to current display. */
	//private Display display;
	
	/** The application title. This is for displaying in things like alert titles. */
	private String title;
	private EpihandyController controller;

	
	private FormSettings formSettings;
	
	public FormSettings getFormSettings() {
		return formSettings;
	}
	
	//private boolean downloadingForms = false;
	
	/**
	 * Creates a new instance of form manager.
	 * 
	 * @param title - the title of the application. This is to be used for titles like in alerts.
	 * @param display - a reference to the display.
	 * @param formEventListener - a listener to the form events.
	 * @param currentScreen - the screen currently displayed.
	 * @param transportLayer - a reference to the transportLayer object.
	 * @param transportLayerListener - a reference to the listener to transport layer events.
	 */
	public FormManager(String title,Display display, FormListener formEventListener){
		//this.display = display;
		formSettings = new FormSettings(display);

		this.title = title;		
		AbstractView.display = display;
				
		controller = new EpihandyController();
		controller.init( title, display, formEventListener, this);
		
		//Just testing type editor extension;
		RepeatTypeEditor rptEditor = new RepeatTypeEditor();
		rptEditor.setController(controller);
		controller.setTypeEditor(QuestionDef.QTN_TYPE_REPEAT, rptEditor);
		
		MultmediaTypeEditor mmEditor = new MultmediaTypeEditor();
		mmEditor.setController(controller);
		controller.setTypeEditor(QuestionDef.QTN_TYPE_IMAGE, mmEditor);
		controller.setTypeEditor(QuestionDef.QTN_TYPE_VIDEO, mmEditor);
		controller.setTypeEditor(QuestionDef.QTN_TYPE_AUDIO, mmEditor);
		
		//omar 21-6-11
		//register GPS type editor if it is available on the phone, otherwise use the default GPS one
		controller.setTypeEditor(QuestionDef.QTN_TYPE_GPS, GPSTypeEditor.getGPSTypeEditor());
		//omar
		
		if(getFormSettings().isOkOnRight()){
			DefaultCommands.cmdOk = new Command(MenuText.OK(), Command.CANCEL, 1);
			DefaultCommands.cmdSave = new Command(MenuText.SAVE(),Command.CANCEL,2);
		}
		
		QuestionData.dateDisplayFormat = getFormSettings().getDateFormat();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets a custom editor of a question type.
	 * 
	 * @param type - the question type.
	 * @param typeEditor - the editor.
	 */
	public void setTypeEditor(byte type, TypeEditor typeEditor){
		controller.setTypeEditor(type, typeEditor);
	}
	
	/*public void setFormViewer(FormView formViewer){
		//this.typeEditors.put(new Byte(type), typeEditor);
	}
	
	public void setFormListViewer(FormDataListView formListViewer){
		//this.typeEditors.put(new Byte(type), typeEditor);
	}
	
	public void setStudyListViewer(StudyListView studyListViewer){
		//this.typeEditors.put(new Byte(type), typeEditor);
	}*/
	
	public void showStudies(){
		this.controller.showStudies();
	}
	
	public void showFormDefList(StudyDef study) {
		this.controller.showFormDefList(study);
	}
	
	public void mainDisplayable(Displayable displayable){
		this.controller.setPrevScreen(displayable);
	}
	
	public void showForm(boolean studyEditingMode,String formVarName,boolean showNew,Display display,Displayable currentScreen,FormListener formEventListener){	
		//this.display = display;
		this.controller.showForm(studyEditingMode,formVarName,showNew);
	}
	
	/**
	 * Displays a form for editing.
	 * 
	 * @param data - the form data.
	 */
	public void showForm(boolean studyEditingMode,FormData data, boolean allowDelete){
		this.controller.showForm(studyEditingMode,data,allowDelete);
	}
	
	public void showForm(boolean studyEditingMode,int studyId,FormDef formDef, int formDataRecordId,boolean allowDelete){
		this.controller.showForm(studyEditingMode,studyId, formDef,formDataRecordId, allowDelete);
	}
	
	public Vector getForms(){
		return controller.getForms();
	}
	public Vector getStudies(){
		return controller.getStudyList();
	}
	
	public StudyDataList getCollectedData() {
		return controller.getCollectedData();
	}
}
