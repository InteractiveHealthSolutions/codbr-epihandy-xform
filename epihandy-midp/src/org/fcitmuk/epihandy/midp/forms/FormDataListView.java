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

import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.AlertMessage;
import org.fcitmuk.util.AlertMessageListener;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;


/**
 * Display a list of data collected forms.
 * 
 * @author Daniel Kayiwa.
 *
 */
public class FormDataListView extends AbstractView implements AlertMessageListener {	

	private Vector formDataList;
	private FormDef formDef;
	private boolean deleting = false;
	private AlertMessage alertMsg;

	private final byte CA_NONE = 0;
	private final byte CA_ERROR = 1;

	private byte currentAction = CA_NONE;

	public FormDataListView(){

	}

	/**
	 * Displays data collected for a form type.
	 * 
	 * @param def - the form definition.
	 */
	public void showFormList(FormDef def, Vector frmDataList){
		try{
			this.formDef = def;
			this.formDataList = frmDataList;

			screen = new List(def.getName()+ " - " + MenuText.DATA_LIST() + " - " + title, Choice.IMPLICIT );
			alertMsg = new AlertMessage(display, title, screen, this);

			if(formDataList != null){
				for(int i=0; i<formDataList.size(); i++){
					FormData data = (FormData)formDataList.elementAt(i);
					data.setDef(def);
					data.buildDataDescription();
					((List)screen).append(data.toString(), null);
				}
			}
			else
				formDataList = new Vector();

			screen.setCommandListener(this);
			screen.addCommand(DefaultCommands.cmdNew);
			screen.addCommand(DefaultCommands.cmdBack);
			if(formDataList.size() > 0)
				screen.addCommand(DefaultCommands.cmdDelete);
			screen.addCommand(DefaultCommands.cmdMainMenu);

			/*if((this.currentFormDataIndex != EpihandyConstants.NO_SELECTION) && (this.currentFormDataIndex < formDataList.size()))
				mainList.setSelectedIndex(this.currentFormDataIndex, true);*/

			display.setCurrent(screen);
		}
		catch(Exception ex){
			currentAction = CA_ERROR;
			//TODO Changing form definition corrupts existing data. So it's safe to first upload all form collected data before downloading new form definitions.
			alertMsg.showError(MenuText.DATA_LIST_DISPLAY_PROBLEM());
			ex.printStackTrace();
		}
	}

	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			if(c == DefaultCommands.cmdOk || c == List.SELECT_COMMAND)
				getEpihandyController().showForm(true,(FormData)this.formDataList.elementAt(((List)d).getSelectedIndex()),true);
			else if(c == DefaultCommands.cmdBack)
				getEpihandyController().handleCancelCommand(this);
			else if(c == DefaultCommands.cmdNew)
				getEpihandyController().showForm(true,new FormData(this.formDef),false);
			else if(c == DefaultCommands.cmdDelete)
				handleDeleteCommand(d);
			else if(c == DefaultCommands.cmdMainMenu)
				getEpihandyController().backToMainMenu();
		}
		catch(Exception e){
			alertMsg.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Processes the cancel command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	/*private void handleBackCommand(Displayable d){
		getEpihandyController().handleCancelCommand(this);
	}

	/**
	 * Processes the new command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	/*private void handleNewCommand(Displayable d){
		getEpihandyController().showForm(true,new FormData(this.formDef),false);
	}

	/**
	 * Processes the OK command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	/*private void handleOkCommand(Displayable d){
		getEpihandyController().showForm(true,(FormData)this.formDataList.elementAt(((List)d).getSelectedIndex()),true);
	}

	/**
	 * Processes the delete command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleDeleteCommand(Displayable d){
		FormData formData = (FormData)this.formDataList.elementAt(((List)screen).getSelectedIndex());
		alertMsg.showConfirm(MenuText.FORM_DELETE_PROMPT() + " " + formData.toString());
		deleting = true;
	}

	public void deleteCurrentForm(){
		int index = ((List)screen).getSelectedIndex();
		FormData formData = (FormData)this.formDataList.elementAt(index);

		getEpihandyController().deleteForm(formData,this);

		((List)screen).delete(index);
		formDataList.removeElementAt(index);

		if(formDataList.size() == 0)
			screen.removeCommand(DefaultCommands.cmdDelete);
	}
	
	public boolean hasSelectedForm(){
		return screen != null;
	}

	public void onFormSaved(FormData formData,boolean isNew){
		formData.buildDataDescription();

		if(isNew){
			formDataList.addElement(formData);
			((List)screen).append(formData.toString(), null);
			if(formDataList.size() == 1)
				screen.addCommand(DefaultCommands.cmdDelete);
			((List)screen).setSelectedIndex(formDataList.size()-1, true);			
		}
		else{
			formDataList.setElementAt(formData, ((List)screen).getSelectedIndex());
			((List)screen).set(((List)screen).getSelectedIndex(),formData.toString(), null);
		}

		//display.setCurrent(screen);
		//TODO Should this mouthing really be done here or somewhere elese?
		alertMsg.show(MenuText.FORM_SAVE_SUCCESS());
	}

	public void onAlertMessage(byte msg){
		if(msg == AlertMessageListener.MSG_OK){
			if(deleting){
				deleting = false;
				deleteCurrentForm();
			}
			else if(currentAction == CA_ERROR){
				getEpihandyController().handleCancelCommand(this);
				return;
			}
		}

		display.setCurrent(screen);
	}

	public Vector getFormDataList(){
		return formDataList;
	}

	private EpihandyController getEpihandyController(){
		return (EpihandyController)controller;
	}
}
