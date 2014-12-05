package org.fcitmuk.midp.mvc;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public abstract class SettingsManager {

	protected abstract void initDefaultSettings();
	
	protected abstract void openSettingsEditor(Display display, Displayable nextDisplayable);
}
