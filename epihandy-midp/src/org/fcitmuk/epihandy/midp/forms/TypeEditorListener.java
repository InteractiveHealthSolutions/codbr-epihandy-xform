/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy.midp.forms;

import javax.microedition.lcdui.Command;

import org.fcitmuk.epihandy.QuestionData;



/**
 * Events fired by type editors. E.g when finished editing.
 * 
 * @author Daniel Kayiwa
 *
 */
public interface TypeEditorListener {
	public void endEdit(boolean save, QuestionData value, Command cmd);
}
