package org.fcitmuk.epihandy.midp.forms;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.midp.db.util.StorageListener;
import org.fcitmuk.midp.mvc.SettingsManager;
import org.fcitmuk.util.MenuText;
import org.fcitmuk.util.Setting;
import org.fcitmuk.util.SettingList;
import org.fcitmuk.util.SettingValidator;
import org.fcitmuk.util.SettingView;
import org.fcitmuk.util.Utilities;
import org.ihs.logger.Log;

public class FormSettings extends SettingsManager{
	public static final String STORAGE_NAME_SETTINGS = "fcitmuk.GeneralSettings";

	public static final String KEY_SINGLE_QUESTION_EDIT = "SINGLE_QUESTION_EDIT";
	public static final String KEY_QUESTION_NUMBERING = "QUESTION_NUMBERING";
	public static final String KEY_OK_ON_RIGHT = "OK_ON_RIGHT";
	public static final String KEY_DELETE_DATA_AFTER_UPLOAD = "DELETE_DATA_AFTER_UPLOAD";
	
	public static final String KEY_DATE_FORMAT = "DATE_FORMAT ";
	
	public static final String KEY_LAST_SELECTED_ITEM = "KEY_LAST_SELECTED_ITEM_LANG";
	public static final String KEY_LOCALE = "LOCALE ";
	
	public static final String KEY_PICTURE_FORMAT = "PICTURE_FORMAT ";
	public static final String KEY_PICTURE_WIDTH = "PICTURE_WIDTH";
	public static final String KEY_PICTURE_HEIGHT = "PICTURE_HEIGHT";
	public static final String KEY_VIDEO_FORMAT = "KEY_VIDEO_FORMAT";
	public static final String KEY_AUDIO_FORMAT = "KEY_AUDIO_FORMAT";
	public static final String KEY_ENCODING = "KEY_ENCODING";

	private final SettingView SETTING_EDITOR;

	public SettingList getSettingList(){
		return SETTING_EDITOR.getSettingList();
	}
	
	FormSettings(final Display display) {
		Log.LOGGER().debug("Loading connection settings... configuring editor..");

		SETTING_EDITOR = new SettingView("Form Settings", null, null,
				new SettingValidator() {
					public boolean validateSettings() {
						return validateFormSettings(display);
					}
				}, 
				new StorageListener() {
					public void errorOccured(String errorMessage, Exception e) {
						Log.LOGGER().error(errorMessage, e);
					}
				}, STORAGE_NAME_SETTINGS, display, null);

		initDefaultSettings();
	}
	
	protected void initDefaultSettings() {
		SettingList settingList = SETTING_EDITOR.getSettingList();
		if(settingList.size() == 0){
			settingList.addSetting(new Setting(KEY_DATE_FORMAT, "0", MenuText.DATE_FORMAT(), "The setting to specify date format in forms", "", "", String.valueOf(true), String.valueOf(true)));
	
			settingList.addSetting(new Setting(KEY_SINGLE_QUESTION_EDIT, new Boolean(true).toString(), MenuText.SINGLE_QUESTION_EDIT(), "The setting to specify if only one question should be editable at a time or complete form could be filled", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_QUESTION_NUMBERING, new Boolean(true).toString(), MenuText.NUMBERING(), "The setting to specify if question numbers should be displayed", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_OK_ON_RIGHT, new Boolean(true).toString(), MenuText.OK_ON_RIGHT(), "The setting to specify if ok button should be on right", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_DELETE_DATA_AFTER_UPLOAD, new Boolean(true).toString(), MenuText.DELETE_AFTER_UPLOAD(), "The setting to specify if data should be deleted after upload on server", "", "", String.valueOf(true), String.valueOf(true)));
			
			settingList.addSetting(new Setting(KEY_PICTURE_FORMAT, "jpeg", MenuText.PICTURE_FORMAT(), "The setting to specify picture format in forms", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_PICTURE_WIDTH, "200", MenuText.PICTURE_WIDTH(), "The setting to specify picture width in forms", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_PICTURE_HEIGHT, "200", MenuText.PICTURE_HEIGHT(), "The setting to specify picture height in forms", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_VIDEO_FORMAT, "mpeg", MenuText.VIDEO_FORMAT(), "The setting to specify video format in forms", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_AUDIO_FORMAT, "x-wav", MenuText.AUDIO_FORMAT(), "The setting to specify audio format in forms", "", "", String.valueOf(true), String.valueOf(true)));
			settingList.addSetting(new Setting(KEY_ENCODING, System.getProperty("video.snapshot.encodings"), MenuText.ENCODINGS(), "The setting to specify audio format in forms", "", "", String.valueOf(true), String.valueOf(true)));
		
			settingList.saveSettings();
		}
		
		SETTING_EDITOR.appendChoiceSetting(settingList.getSetting(KEY_DATE_FORMAT), 
				new String[]{MenuText.DAY_FIRST(), MenuText.MONTH_FIRST(), MenuText.YEAR_FIRST()}, 
				ChoiceGroup.POPUP);
		
		SETTING_EDITOR.appendChoiceSetting(settingList.getSetting(KEY_SINGLE_QUESTION_EDIT), 
				new String[]{new Boolean(true).toString(), new Boolean(false).toString()}, 
				ChoiceGroup.POPUP);
		
		SETTING_EDITOR.appendChoiceSetting(settingList.getSetting(KEY_QUESTION_NUMBERING), 
				new String[]{new Boolean(true).toString(), new Boolean(false).toString()}, 
				ChoiceGroup.POPUP);
		
		SETTING_EDITOR.appendChoiceSetting(settingList.getSetting(KEY_OK_ON_RIGHT), 
				new String[]{new Boolean(true).toString(), new Boolean(false).toString()}, 
				ChoiceGroup.POPUP);
		
		SETTING_EDITOR.appendChoiceSetting(settingList.getSetting(KEY_DELETE_DATA_AFTER_UPLOAD), 
				new String[]{new Boolean(true).toString(), new Boolean(false).toString()}, 
				ChoiceGroup.POPUP);
		
		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_PICTURE_FORMAT), 10, TextField.ANY);

		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_PICTURE_WIDTH), 3, TextField.NUMERIC);
		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_PICTURE_HEIGHT), 3, TextField.NUMERIC);
		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_VIDEO_FORMAT), 10, TextField.ANY);
	
		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_AUDIO_FORMAT), 10, TextField.ANY);
		SETTING_EDITOR.appendTextSetting(settingList.getSetting(KEY_ENCODING), 255, TextField.ANY);
	
	}
	
	protected boolean validateFormSettings(Display display) {
		return true;
	}
	
	public void openSettingsEditor(Display display, Displayable nextDisplayable){
		SETTING_EDITOR.openSettingsEditor(display, nextDisplayable);
	}
	
	public byte getDateFormat(){
		return Byte.parseByte(SETTING_EDITOR.getSettingList().getSettingValue(KEY_DATE_FORMAT,"0"));
	}
	
	public boolean isSingleQtnEdit(){
		return Utilities.stringToBoolean(SETTING_EDITOR.getSettingList().getSettingValue(KEY_SINGLE_QUESTION_EDIT, new Boolean(true).toString()));
	}
	
	public boolean isOkOnRight(){
		return Utilities.stringToBoolean(SETTING_EDITOR.getSettingList().getSettingValue(KEY_OK_ON_RIGHT, new Boolean(true).toString()));
	}
	
	public boolean isQuestionNumbering(){
		return Utilities.stringToBoolean(SETTING_EDITOR.getSettingList().getSettingValue(KEY_QUESTION_NUMBERING, new Boolean(true).toString()));
	}
	
	public boolean deleteDataAfterUpload(){
		return Utilities.stringToBoolean(SETTING_EDITOR.getSettingList().getSettingValue(KEY_DELETE_DATA_AFTER_UPLOAD, new Boolean(true).toString()));
	}
	
	public String getAudioFormat(){
		return SETTING_EDITOR.getSettingList().getSettingValue(KEY_AUDIO_FORMAT,"x-wav");
	}

	public String getVideoFormat(){
		return SETTING_EDITOR.getSettingList().getSettingValue(KEY_VIDEO_FORMAT,"mpeg");
	}
	
	public String getPictureParameters(){
		String format = null;

		String s = SETTING_EDITOR.getSettingList().getSettingValue(KEY_PICTURE_FORMAT, "png");
		if(s == null || s.trim().length() == 0)
			return null;
		format = "encoding="+s;

		s = SETTING_EDITOR.getSettingList().getSettingValue(KEY_PICTURE_WIDTH, "200");
		if(s == null || s.trim().length() == 0)
			return null;
		format += "&width="+s;

		s = SETTING_EDITOR.getSettingList().getSettingValue(KEY_PICTURE_HEIGHT, "200");
		if(s == null || s.trim().length() == 0)
			return null;
		format += "&height="+s;

		return format;
	}
	
/*	public void setDeleteDataAfterUpload(boolean delete){
		SettingList settings = new SettingList(STORAGE_NAME_SETTINGS,true);
		settings.setSetting(KEY_DELETE_DATA_AFTER_UPLOAD,Utilities.booleanToString(delete));
	}*/
}
