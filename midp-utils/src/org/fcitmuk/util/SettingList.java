/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.db.util.PersistentHelper;
import org.fcitmuk.db.util.Storage;
import org.fcitmuk.midp.db.util.StorageFactory;
import org.fcitmuk.midp.db.util.StorageListener;

/**
 * Stores user settings.
 * 
 * @author Daniel
 *
 */
public class SettingList implements Persistent{
	
	private String storageName = null;
	private Vector settings = new Vector();
	private StorageListener storagelistner;
	
	/**
	 * Constructs a settings storage with a custom storageName.
	 * 
	 * @param storageName the storageName of storage.
	 */
	public SettingList(String storageName){
		this.storageName = storageName;
	}
	
	/**
	 * Constructs a new settings object.
	 * 
	 * @param loadSettings - set to true if you want to load existing settings.
	 */
	public SettingList(String storageName, boolean loadSettings, StorageListener storagelistner){
		this.storageName = storageName;
		this.storagelistner = storagelistner;
		loadSettings();//TODO
	}
	
	private String getStorageName() {
		return storageName;
	}

	public int size(){
		return settings.size();
	}
	
	public Setting elementAt(int i){
		return (Setting)settings.elementAt(i);
	}
	
	public Vector getSettings(){
		return settings;
	}
	
	public Setting getSetting(String key){
		for (int i = 0; i < size(); i++) {
			if(elementAt(i).getName().toLowerCase().compareTo(key.toLowerCase()) == 0){
				return elementAt(i);
			}
		}
		return null;
	}
	
	public String getSettingValue(String key, String defaultValue){
		Setting set = getSetting(key);
		if(set == null)
			return defaultValue;
		return set.getValue();
	}
	
	public void addSetting(Setting setting){
		settings.addElement(setting);
	}
	
	public void deleteSetting(String key){
		Setting set = getSetting(key);
		if(set != null)
			settings.removeElement(set);
	}
	
	public void deleteAllSettings(){
		settings.removeAllElements();
	}
	
	public void loadSettings(){
		Storage store = StorageFactory.getStorage(getStorageName(),storagelistner);
		settings = store.read(new Setting().getClass());
		
		if(settings == null)
			settings = new Vector();
	}
	
	public void saveSettings(){
		Storage store = StorageFactory.getStorage(getStorageName(),storagelistner);
		store.delete();
		store.addNew(settings);
		loadSettings();
	}
	
	public void write(DataOutputStream dos) throws IOException{
    	PersistentHelper.write(settings, dos);
    }
    
	public void read(DataInputStream dis) throws IOException,InstantiationException,IllegalAccessException{
    	settings = PersistentHelper.read(dis, new Setting().getClass());		
    }

}
