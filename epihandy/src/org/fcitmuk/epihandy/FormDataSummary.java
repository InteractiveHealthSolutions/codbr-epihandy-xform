package org.fcitmuk.epihandy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.AbstractRecord;

/**
 * Contains a summary of submitted form data
 * (form data description + identifier) 
 * 
 * @author dagmar@cell-life.org
 */
public class FormDataSummary  extends AbstractRecord {
	
	short studyIndex;
	short formIndex;
	private String reference;
	private String dateSubmitted;
	private String dataDescription;

	/** Constructs a form data summary object. */
	public FormDataSummary(){
		super();
	}

	public void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		
		studyIndex = dis.readShort();
		formIndex = dis.readShort();
		
		reference = dis.readUTF().intern();
		dateSubmitted = dis.readUTF().intern();	
		dataDescription = dis.readUTF().intern();
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeShort(studyIndex);
		dos.writeShort(formIndex);
		dos.writeUTF(reference);
		dos.writeUTF(dateSubmitted);
		dos.writeUTF(dataDescription);
	}

	public short getStudyIndex() {
		return studyIndex;
	}

	public short getFormIndex() {
		return formIndex;
	}
	
	public String getReference() {
		return reference;
	}

	public String getDateSubmitted() {
		return dateSubmitted;
	}
	
	public String getDataDescription(){
		return this.dataDescription;
	}
	
	public void setDataDescription(String description) {
		this.dataDescription = description;
	}
}
