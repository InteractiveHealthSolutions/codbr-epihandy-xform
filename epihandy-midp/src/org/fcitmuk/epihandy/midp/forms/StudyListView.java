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
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.fcitmuk.epihandy.EpihandyConstants;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;

/**
 * @author daniel
 */
public class StudyListView extends AbstractView implements CommandListener  {
	private Vector studyList;
	
	public StudyListView(){
		
	}
	
	public void showStudyList(){
		screen = new List(MenuText.SELECT_STUDY()+" - "+title , Choice.IMPLICIT);
		
        StudyDef study; 
        int selectedIndex = EpihandyConstants.NO_SELECTION;
		// TODO Settings settings = new Settings(EpihandyConstants.STORAGE_NAME_EPIHANDY_SETTINGS,true);
		//String val = settings.getSetting(EpihandyConstants.KEY_LAST_SELECTED_STUDY);

		String val = getEpihandyController().getFormManager().getFormSettings().getSettingList().getSettingValue(EpihandyConstants.KEY_LAST_SELECTED_STUDY, null);

		for(int i=0; i<studyList.size(); i++){
			study = (StudyDef)studyList.elementAt(i);
			if(val != null && study.getId() == Byte.parseByte(val)){
				selectedIndex = i;
			}
			
			((List)screen).append(study.getId()+":"+study.getName(), null);
		}
		
		if(selectedIndex != EpihandyConstants.NO_SELECTION)
			((List)screen).setSelectedIndex(selectedIndex, true);
		
		screen.setCommandListener(this);
		screen.addCommand(DefaultCommands.cmdOk);
		screen.addCommand(DefaultCommands.cmdCancel);
		display.setCurrent(screen);
	}
	
	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			int selected = ((List)d).getSelectedIndex();
			if(selected > -1 && c == DefaultCommands.cmdOk)
				getEpihandyController().execute(this,DefaultCommands.cmdOk,(StudyDef)studyList.elementAt(selected));
			else if(c == DefaultCommands.cmdCancel)
				getEpihandyController().execute(this,c,null);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setStudyList(Vector list){
		studyList = list;
	}
	
	public Vector getStudyList(){
		return studyList;
	}
	
	private EpihandyController getEpihandyController(){
		return (EpihandyController)controller;
	}
}
