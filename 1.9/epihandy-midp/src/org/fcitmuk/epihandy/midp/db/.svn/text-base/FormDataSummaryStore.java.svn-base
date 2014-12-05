package org.fcitmuk.epihandy.midp.db;

import java.util.Vector;

import org.fcitmuk.epihandy.FormDataSummary;
import org.fcitmuk.midp.db.util.Storage;
import org.fcitmuk.midp.db.util.StorageFactory;

public class FormDataSummaryStore {
	
	private final String SESSION_REFERENCE_STORE_NAME = "sessionReferenceStore";
	
	public boolean hasStoreData() {
		String[] names = StorageFactory.getNames();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (name.startsWith(SESSION_REFERENCE_STORE_NAME)) {
				Storage store = StorageFactory.getStorage(name, null);
				if (store.getNumRecords() > 0)
					return true;
			}
		}
		return false;
	}
	
	public Vector getSessionReferences() {
		
		Vector sessionReferences = new Vector();
		sessionReferences = getStore().read(FormDataSummary.class);
		
		return sessionReferences;
	}
	
	public boolean saveSessionReferences(FormDataSummary sessionReference) {
		
		return getStore().save(sessionReference);
	}
	
	public Storage getStore(){
		Storage store = StorageFactory.getStorage(SESSION_REFERENCE_STORE_NAME, null);
		return store;
	}

	public void clearSessionReferences() {
		if(hasStoreData()){
			getStore().delete();
		}
	}
}
