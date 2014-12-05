package org.fcitmuk.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.midp.db.util.StorageListener;

public class SettingView extends Form{
	private Hashtable uiItems = new Hashtable();
	private SettingList settingList;
	private Command cmdSave;
	private Command cmdCancel;
	private Displayable nextDisplayable;
	private Display display;
	public SettingList getSettingList(){
		return settingList;
	}
	
	public SettingView(String title, final CommandListener saveCommandHandler, 
			final CommandListener cancelCommandHandler, final SettingValidator settingValidator, 
			StorageListener storgeListner, String settingStorgeName, Display display, 
			Displayable nextDisplayable) {
		super(title);
		this.display = display;
		this.nextDisplayable = nextDisplayable;
		settingList = new SettingList(settingStorgeName, true, storgeListner);
		
		cmdSave = new Command ("Save", Command.OK, 0);
		cmdCancel = new Command ("Cancel", Command.EXIT, 1);
		
		addCommand(cmdSave);
		addCommand(cmdCancel);
		
		final SettingView settingView = this;
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				CommandListener saveListner = saveCommandHandler;
				if(saveListner == null){
					saveListner = new CommandListener() {
						public void commandAction(Command c, Displayable d) {
							boolean isValid = settingValidator!=null?settingValidator.validateSettings():true;
							if(isValid){
								for (Enumeration e = getSettingUiItems().keys(); e.hasMoreElements();) {
									String key = (String) e.nextElement();
									settingList.getSetting(key).setValue(getSettingUiItemValue(key));
								}
								
								settingList.saveSettings();
								settingView.display.setCurrent(settingView.nextDisplayable);
							}
						}
					};
				}
				CommandListener cancelListner = cancelCommandHandler;
				if(cancelListner == null){
					cancelListner = new CommandListener() {
						public void commandAction(Command c, Displayable d) {
							settingView.display.setCurrent(new Alert("Exiting without saving settings"), settingView.nextDisplayable);
						}
					};
				}
				
				if(c == cmdSave){
					saveListner.commandAction(c, d);
				}
				else if(c == cmdCancel){
					cancelListner.commandAction(c, d);
				}
			}
		});
	}
	
	public Hashtable getSettingUiItems() {
		return uiItems;
	}
	
	public String getSettingUiItemValue(String key) {
		return getItemValue((Item) uiItems.get(key));
	}
	private String getItemValue(Item item){
		if(item instanceof TextField){
			return ((TextField) item).getString();
		}
		else if(item instanceof ChoiceGroup){
			return ((ChoiceGroup) item).getString(((ChoiceGroup) item).getSelectedIndex());
		}
		return "";
	}

	public void appendTextSetting(Setting setting, int maxSize, int itemUIConstraints){
		TextField t = new TextField(setting.getDisplayName(), setting.getValue(), maxSize, itemUIConstraints);
		uiItems.put(setting.getName(), t);
	}
	
	public void appendChoiceSetting(Setting setting, String[] choices, int choiceGroupType){
		ChoiceGroup ch = new ChoiceGroup(setting.getDisplayName(), choiceGroupType, choices, null);
		uiItems.put(setting.getName(), ch);
	}
	
	/*public void appendSeparator(){
		StringItem sp = new StringItem("", "................................");
	}*/
	
	public void openSettingsEditor(Display display, Displayable nextDisplayable){
		this.nextDisplayable = nextDisplayable;
		
		deleteAll();
		for (int i = 0; i < settingList.size(); i++) {
			Object item = uiItems.get(settingList.elementAt(i).getName());
			if(item != null){
				append((Item) item);
			}
		}
		display.setCurrent(this);
	}
}
