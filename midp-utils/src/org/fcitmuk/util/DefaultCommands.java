/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * Commands shared by the entire application
 * Some commands like OK, Cancel, Save, are intensionally reversed, eg OK=Command.CANCEL
 * just to have them positioned by the phone in the way which will be more convenient for the user
 * 
 * @author Daniel
 *
 */
public class DefaultCommands {
	
	/** Command for closing the application. */
	public static final Command cmdExit= new Command(MenuText.EXIT(), Command.EXIT, 1);
	
	/** Command for cancelling changes. */
	public static Command cmdCancel = new Command(MenuText.CANCEL(), Command.CANCEL, 3);
	
	/** Command for accepting selection. */
	public static Command cmdOk = new Command(MenuText.OK(), Command.OK, 1);
	
	/** Command for accepting selection. Put on cancel side for easy acessiblity on some phones.*/
	//public static Command cmdOkEx = new Command("OK", Command.CANCEL, 1);
	
	/** Command for editing a question. */
	public static Command cmdEdit = new Command(MenuText.EDIT(), Command.OK, 1);
	
	/** Command for displaying a new form. */
	public static Command cmdNew = new Command(MenuText.NEW(),Command.SCREEN, 1);
	
	
	/** Command for saving changes. */
	public static Command cmdSave = new Command(MenuText.SAVE(),Command.OK,1);
	
	/** Command for deleting. */
	public static Command cmdDelete = new Command(MenuText.DELETE(),Command.SCREEN,1);
	
	/** Command for displaying the previous screen. */
	public static Command cmdBack = new Command(MenuText.BACK(),Command.BACK,1);
	
	
	/** Command for accepting selection. */
	public static Command cmdYes = new Command(MenuText.YES(),Command.OK,1);
	
	/** Command for cancelling selection. */
	public static Command cmdNo = new Command(MenuText.NO(),Command.CANCEL,2);
	
	/** Command for going to the parent screen. */
	public static Command cmdBackParent = new Command(MenuText.BACK_TO_LIST(),Command.CANCEL,1);
	
	public static Command cmdNext = new Command(MenuText.NEXT(), Command.CANCEL, 1);
	public static Command cmdPrev = new Command(MenuText.PREVIOUS(), Command.OK, 1);
	public static Command cmdFirst = new Command(MenuText.FIRST(), Command.OK, 1);
	public static Command cmdLast = new Command(MenuText.LAST(), Command.OK, 1);
	
	/** The list selection command. */
	public static Command cmdSelect = List.SELECT_COMMAND;
	
	/** Command for accepting selection. */
	public static Command cmdSel = new Command(MenuText.SELECT(), Command.OK, 1);
	
	/** Command to go back to the main menu. */
	public static Command cmdMainMenu = new Command(MenuText.MAIN_MENU(),Command.BACK,1);

	/** No creation allowed. */
	/*private DefaultCommands(){
		
	}*/
}
