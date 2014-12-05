package org.fcitmuk.epihandy.midp.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.midp.model.Model;

/**
 *  Handles download of Form Data
 */
public class FormDownload implements Persistent {

	private Model model;
	private int studyId;
	private int formDefId;
	private FormData formData;

	public FormDownload(Model model, int studyId, int formDefId) {
		this.model = model;
		this.studyId = studyId;
		this.formDefId = formDefId;
	}

	public void read(DataInputStream dis) throws IOException, 
		IllegalAccessException, InstantiationException {
		// read FormData from the stream
		FormDef formDef = model.getFormDef(studyId, formDefId);
		formData = new FormData();
		formData.read(dis);
		formData.setDef(formDef);
		// update form data references in the model
		model.saveFormData(model.getSelectedStudyDef().getId(), formData);
	}

	public void write(DataOutputStream dos) throws IOException {
		// not implemented
	}
	
	public FormData getDownloadedFormData() {
		return formData;
	}
}
