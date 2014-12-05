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

import org.fcitmuk.db.util.Persistent;



/**
 * Stores user settings.
 * 
 * @author Daniel
 * @author Maimoona
 *
 */
public class Setting implements Persistent{
	
	private String name;
	private String value;
	private String displayName;
	private String description;
	private String validator;
	private String validationErrorMessage;
	private String isEditable;
	private String isVisible;
		
	/** This should be called only by the deserializer. */
	public Setting(){
		
	}
	
	public Setting(String name, String value, String displayName,
			String description, String validator,
			String validationErrorMessage, String isEditable, String isVisible) {
		super();
		this.name = name;
		this.value = value;
		this.displayName = displayName;
		this.description = description;
		this.validator = validator;
		this.validationErrorMessage = validationErrorMessage;
		this.isEditable = isEditable;
		this.isVisible = isVisible;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}

	public String getValidationErrorMessage() {
		return validationErrorMessage;
	}

	public void setValidationErrorMessage(String validationErrorMessage) {
		this.validationErrorMessage = validationErrorMessage;
	}

	public String isEditable() {
		return isEditable;
	}

	public void setEditable(String isEditable) {
		this.isEditable = isEditable;
	}

	public String isVisible() {
		return isVisible;
	}

	public void setVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public void write(DataOutputStream dos) throws IOException{
		dos.writeUTF(this.name);
		dos.writeUTF(this.value);
		dos.writeUTF(this.displayName);
		dos.writeUTF(this.description);
		dos.writeUTF(this.validator);
		dos.writeUTF(this.validationErrorMessage);
		dos.writeUTF(this.isEditable);           
		dos.writeUTF(this.isVisible);
    }
    
	public void read(DataInputStream dis) throws IOException,InstantiationException,IllegalAccessException{
		this.name = dis.readUTF();
		this.value = dis.readUTF();
		this.displayName = dis.readUTF();
		this.description = dis.readUTF();
		this.validator = dis.readUTF();
		this.validationErrorMessage = dis.readUTF();
		this.isEditable = dis.readUTF();           
		this.isVisible = dis.readUTF();
    }

}
