package org.fcitmuk.openmrs.forms;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.epihandy.midp.forms.GeneralSettings;
import org.fcitmuk.util.SettingList;
import org.fcitmuk.util.Utilities;


/**
 * 
 * @author daniel
 *
 */
public class PatientSearchForm extends Form {

	private static final String KEY_INCLUDE_SERVER_SEARCH = "INCLUDE_SERVER_SEARCH";
	
	private TextField txtName;
	private TextField txtId;
	private ChoiceGroup cgSearchType;
	private boolean flagServerSearch;
	
	public PatientSearchForm(String title, boolean searchOnServer) {
		super(title);
		this.flagServerSearch = searchOnServer;
		txtId = new TextField("Patient Identifier:","",100,TextField.ANY);
		txtName = new TextField("Name:","",100,TextField.ANY);
		append(txtId);
		append(txtName);
		
		if(flagServerSearch)
		{
			cgSearchType = new ChoiceGroup("Search including",Choice.MULTIPLE);
			cgSearchType.append("Server", null);
			
			SettingList settings = new SettingList(GeneralSettings.STORAGE_NAME_SETTINGS,true);
			cgSearchType.setSelectedIndex(0, Utilities.stringToBoolean(settings.getSetting(KEY_INCLUDE_SERVER_SEARCH)));			
			append(cgSearchType);
		}
		
		
	}
	
	public String getId(){
		return this.txtId.getString();
	}
	
	public void setId(String id){
		this.txtId.setString(id);
	}
	
	public String getName(){
		return this.txtName.getString();
	}
	
	public void setName(String name){
		this.txtId.setString(name);
	}
	
	public boolean includeServerSearch(){
		if(this.flagServerSearch)
		{
			SettingList settings = new SettingList(GeneralSettings.STORAGE_NAME_SETTINGS,true);		
			settings.setSetting(KEY_INCLUDE_SERVER_SEARCH,Utilities.booleanToString((cgSearchType.isSelected(0))));
			settings.saveSettings();

			return cgSearchType.isSelected(0);
		}
		return false;
	}
}
