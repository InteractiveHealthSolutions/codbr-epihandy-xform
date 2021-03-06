/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy.midp.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.epihandy.QuestionData;
import org.fcitmuk.epihandy.RepeatQtnsData;
import org.fcitmuk.epihandy.RepeatQtnsDataList;
import org.fcitmuk.epihandy.RepeatQtnsDef;
import org.fcitmuk.epihandy.ValidationRule;
import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.midp.mvc.CommandAction;
import org.fcitmuk.midp.mvc.Controller;
import org.fcitmuk.midp.mvc.View;
import org.fcitmuk.util.DefaultCommands;


/**
 * This serves as the controller for repeat questions.
 * 
 * @author daniel
 *
 */
public class RepeatTypeEditor extends AbstractView implements TypeEditor, TypeEditorListener , Controller{
	
	private QuestionData questionData;
	private RepeatQtnsDataList rptQtnsDataList;
	private RepeatQtnsDef rptQtnsDef;
	private RepeatQtnsData rptQtnsData; //the current one.
	private int pos; //question position pn the form.
	private int count; //total number of questions on the form.
	
	private RptQtnsDataListView dataListView = new RptQtnsDataListView();
	private RptQtnsDataView dataView = new RptQtnsDataView();
	
	private TypeEditor typeEditor = new DefaultTypeEditor();
	private ValidationRule validationRule;
	
	
	//public RepeatTypeEditor(){
		
	//}

	public void startEdit(QuestionData data, ValidationRule validationRule, boolean singleQtnEdit,int pos, int count, TypeEditorListener listener){
		questionData = data;
		this.validationRule = validationRule;
		this.pos = pos;
		this.count = count;
		
		rptQtnsDef = questionData.getDef().getRepeatQtnsDef();
		
		if(questionData.getAnswer() != null)
			rptQtnsDataList = new RepeatQtnsDataList((RepeatQtnsDataList)questionData.getAnswer());
		else{
			rptQtnsDataList = new RepeatQtnsDataList();
			questionData.setAnswer(rptQtnsDataList);
		}

		showQtnsData(validationRule);
		
		typeEditor.setController(this);
	}
	
	private void showQtnsData(ValidationRule validationRule){
		dataListView.showQtnDataList(rptQtnsDef,rptQtnsDataList,this,validationRule);
	}
	
	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			if(c == DefaultCommands.cmdOk || c == DefaultCommands.cmdNext)
				getEpihandyController().endEdit(true, questionData, c);
			else if(c == DefaultCommands.cmdCancel || c == DefaultCommands.cmdPrev)
				getEpihandyController().endEdit(false, questionData, c);
			else
				getEpihandyController().endEdit(false, questionData, c);
		}
		catch(Exception e){
			getEpihandyController().errorOccured(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes the new command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	/*public void handleNewCommand(RepeatQtnsData rptQtnsData){
		dataView.showQtnData(rptQtnsData, this);
	}*/
	
	private EpihandyController getEpihandyController(){
		return (EpihandyController)controller;
	}
	
	public void endEdit(boolean save, QuestionData data, Command cmd){
		rptQtnsData.setQuestionDataById(data);
		dataView.showQtnData(rptQtnsData, this);
	}
	
	public void execute(View view, Object commandAction, Object data){
		
		if(view == dataListView){
			if(commandAction == CommandAction.NEW){
				rptQtnsData = new RepeatQtnsData((byte)(rptQtnsDataList.size()+1),rptQtnsDef);
				dataView.showQtnData(rptQtnsData, this);
			}
			else if(commandAction == CommandAction.EDIT){
				rptQtnsData = new RepeatQtnsData((RepeatQtnsData)data);
				dataView.showQtnData(rptQtnsData, this);
			}
			else if(commandAction == CommandAction.OK){
				questionData.setAnswer(rptQtnsDataList);
				getEpihandyController().endEdit(true/*MAIMOONA*/, questionData, null);
			}
			else if(commandAction == CommandAction.CANCEL){
				//MAIMOONA: to handle back to parent there id needed a way to send Command to controller to endEdit method. This is the quickest way to accomplish it. Send command as object and cast there.
				getEpihandyController().endEdit(false, null, (Command)data);
			}
		}
		else if(view == dataView){
			if(commandAction == CommandAction.EDIT)
				typeEditor.startEdit(new QuestionData((QuestionData)data),null, false,pos,count,this);
			else if(commandAction == CommandAction.OK)
				rptQtnsDataList.setRepeatQtnsDataById((RepeatQtnsData)data);
			
			if(commandAction == CommandAction.OK || commandAction == CommandAction.CANCEL)
				showQtnsData(validationRule);
		}
	}
}
