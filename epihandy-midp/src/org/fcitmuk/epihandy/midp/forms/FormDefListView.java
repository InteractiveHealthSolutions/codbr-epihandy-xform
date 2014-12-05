/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy.midp.forms;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.AlertMessage;
import org.fcitmuk.util.AlertMessageListener;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;
import org.fcitmuk.util.Setting;


/**
 * Display a list of form defintions.
 * 
 * @author Daniel Kayiwa.
 *
 */
public class FormDefListView extends AbstractView implements AlertMessageListener{

	private StudyDef studyDef;
	private AlertMessage alertMsg;
	private int lastSelectionIndex = 0;

	private static final String KEY_LAST_SELECTED_FORMDEF =  "LAST_SELECTED_FORMDEF";
	private Vector formDefList;


	public FormDefListView(){

	}

	/**
	 * Displays the list of forms in a study.
	 * 
	 * @param studyId - the numeric unique identifier of the study.
	 */
	public void showFormList(StudyDef currentStudy, FormListener formListener){
		studyDef = currentStudy;

		screen = new List(MenuText.SELECT_FORM() + " - "+title , Choice.IMPLICIT);
		alertMsg = new AlertMessage(display, title, screen, this);

		try{
			if(currentStudy == null)
				alertMsg.show(MenuText.NO_SELECTED_STUDY());
			else{
				formDefList = copyFormDefs(currentStudy.getForms());
				if(formDefList != null && formDefList.size() > 0){		
					boolean showList = true;
					if(formListener != null)
						showList = formListener.beforeFormDefListDisplay(formDefList);

					if(showList){
						for(int i=0; i<formDefList.size(); i++)
							((List)screen).append(((FormDef)formDefList.elementAt(i)).getName(), null);

						screen.setCommandListener(this);
						screen.addCommand(DefaultCommands.cmdSel);
						screen.addCommand(DefaultCommands.cmdBack);

						// TODO Settings settings = new Settings(EpihandyConstants.STORAGE_NAME_EPIHANDY_SETTINGS,true);
						String val = getEpihandyController().getFormManager().getFormSettings().getSettingList().getSettingValue(KEY_LAST_SELECTED_FORMDEF, null);
						if(val != null)
							lastSelectionIndex = Integer.parseInt(val);

						if(lastSelectionIndex < formDefList.size())
							((List)screen).setSelectedIndex(lastSelectionIndex, true);

						display.setCurrent(screen);
					}
				}
				else
					alertMsg.show(MenuText.NO_STUDY_FORMS());
			}
		}
		catch(Exception e){
			e.printStackTrace();
			alertMsg.showError("Error : "+ e.getMessage());
		}
	}

	private Vector copyFormDefs(Vector formDefs){
		Vector forms = new Vector();
		for(int i=0; i<formDefs.size(); i++)
			forms.addElement(formDefs.elementAt(i));
		return forms;
	}

	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		if(c == DefaultCommands.cmdSel || c == List.SELECT_COMMAND)
			handleOkCommand(d);
		else if(c == DefaultCommands.cmdBack)
			getEpihandyController().handleCancelCommand(this);
	}

	/**
	 * Processes the OK command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleOkCommand(Displayable d){
		try{
			lastSelectionIndex = ((List)d).getSelectedIndex();
			getEpihandyController().showFormDataList((FormDef)formDefList.elementAt(lastSelectionIndex)/*(FormDef)studyDef.getForms().elementAt(lastSelectionIndex)*/);

			//TODO Settings settings = new Settings(EpihandyConstants.STORAGE_NAME_EPIHANDY_SETTINGS,true);
			//settings.setSetting(KEY_LAST_SELECTED_FORMDEF, String.valueOf(lastSelectionIndex));
			//settings.saveSettings();
			
			getEpihandyController().getFormManager().getFormSettings().getSettingList().addSetting(new Setting(KEY_LAST_SELECTED_FORMDEF, String.valueOf(lastSelectionIndex), KEY_LAST_SELECTED_FORMDEF, "", "", "", "false", "false"));
			getEpihandyController().getFormManager().getFormSettings().getSettingList().saveSettings();
		}
		catch(Exception ex){
			//TODO Looks like we should help the user out of this by say enabling them
			//delete any existing data.
			String s = MenuText.FORM_DATA_DISPLAY_PROBLEM();
			if(ex.getMessage() != null && ex.getMessage().trim().length() > 0)
				s = ex.getMessage();
			alertMsg.showError(s);
			ex.printStackTrace();
		}
	}

	public void onAlertMessage(byte msg){
		if(msg == AlertMessageListener.MSG_OK){
			show();
		}
		else
			getEpihandyController().handleCancelCommand(this);
	}

	public void setStudy(StudyDef study){
		studyDef = study;
	}

	public StudyDef getStudy(){
		return studyDef;
	}

	private EpihandyController getEpihandyController(){
		return (EpihandyController)controller;
	}
}
