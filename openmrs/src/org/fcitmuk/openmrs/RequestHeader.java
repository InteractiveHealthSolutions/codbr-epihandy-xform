/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */


package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.db.util.PersistentHelper;


/**
 * Containts the connection request header details of user name, password,
 * and what action to execute.
 * 
 * @author Daniel Kayiwa
 *
 */
public class RequestHeader implements Persistent{

	/** Value representing a not yet set status. */
	public static final byte STATUS_NULL = -1;

	/** Value representing success of an action. */
	public static final byte STATUS_SUCCESS = 1;

	/** Value representing failure of an action. */
	public static final byte STATUS_FAILURE = 0;

	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_FORMS = 3;

	/** Action to save a list of form data. */
	public static final byte ACTION_UPLOAD_FORMS = 5;

	/** Action to download a list of patients from the server. */
	public static final byte ACTION_DOWNLOAD_PATIENTS = 6;

	/** Action to download a list of users from the server. */
	public static final byte ACTION_DOWNLOAD_USERS = 7;

	/** Action to download a list of users and forms from the server. */
	public static final byte ACTION_DOWNLOAD_COHORTS = 8;
	
	/** Action to download a list of users and forms from the server. */
	private static final byte ACTION_DOWNLOAD_SAVED_SEARCHES = 9;
	
	/** Action to download a list of patients from the server. */
	private static final byte ACTION_DOWNLOAD_SS_PATIENTS = 10;

	/** Action to download a list of users and forms from the server. */
	private static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;
	
	/** Action to download a list of patients filtered by name and identifier. */
	public static final byte ACTION_DOWNLOAD_FILTERED_PATIENTS = 15;

	private static final byte ACTION_NONE = -1;
	
	public static final String XFORMS_XFORM_SERIALIZER = "xforms.xformSerializer";
	public static final String XFORMS_PATIENT_SERIALIZER = "xforms.patientSerializer";
	public static final String XFORMS_COHORT_SERIALIZER = "xforms.cohortSerializer";
	public static final String XFORMS_USER_SERIALIZER = "xforms.userSerializer";
	
	public static RequestHeader getRequestHeader(int actionType, String username, String password, String locale, Object[] otherParamsInAscOrder){
		RequestHeader header = null;
		if(actionType == ACTION_DOWNLOAD_USERS){
			header = new RequestHeader(XFORMS_USER_SERIALIZER, ACTION_DOWNLOAD_USERS);
		}
		else if(actionType == ACTION_DOWNLOAD_COHORTS){
			header = new RequestHeader(XFORMS_COHORT_SERIALIZER, ACTION_DOWNLOAD_COHORTS);
		}
		else if(actionType == ACTION_DOWNLOAD_PATIENTS){
			header = new RequestHeader(XFORMS_PATIENT_SERIALIZER, ACTION_DOWNLOAD_PATIENTS);
		}
		else if(actionType == ACTION_DOWNLOAD_FORMS){
			header = new RequestHeader(XFORMS_XFORM_SERIALIZER, ACTION_DOWNLOAD_FORMS);
		}
		else if(actionType == ACTION_UPLOAD_FORMS){
			header = new RequestHeader(XFORMS_XFORM_SERIALIZER, ACTION_UPLOAD_FORMS);
		}
		else {
			header = new RequestHeader("", (byte) actionType);
		}
		
		header.setLocale(locale);
		header.setUserName(username);
		header.setPassword(password);
		header.setOtherParams(otherParamsInAscOrder);
		return header;
		
	}
	/** The current status. This could be a request or return code status. */
	private byte action = ACTION_NONE ;
	private String serializer = "";
	private String locale = "en";
	private String userName = "";
	private String password = "";
	private Object[] otherParams;

	/** Constructs a new communication parameter. */
	private RequestHeader(String serializer, byte action){
		super();
		this.action = action;
		this.serializer = serializer;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private byte getAction() {
		return action;
	}

	private void setAction(byte action) {
		this.action = action;
	}
	private String getSerializer() {
		return serializer;
	}
	
	private void setSerializer(String serializer) {
		this.serializer = serializer;
	}
	
	public Object[] getOtherParams() {
		return otherParams;
	}

	public void setOtherParams(Object[] otherParams) {
		this.otherParams = otherParams;
	}

	/**
	 * @see org.fcitmuk.db.util.Persistent#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream dos) throws IOException{
		dos.writeUTF(getUserName());
		dos.writeUTF(getPassword());
		dos.writeUTF(getSerializer());
		dos.writeUTF(getLocale());
		dos.writeByte(getAction());
		
		if(otherParams != null)
		for (int i = 0; i < otherParams.length; i++) {
			if(otherParams[i] instanceof Byte){
				dos.writeByte(((Byte)otherParams[i]).byteValue());
			}
			else if(otherParams[i] instanceof Short){
				dos.writeShort(((Short)otherParams[i]).shortValue());
			}
			else if(otherParams[i] instanceof Integer){
				dos.writeInt(((Integer)otherParams[i]).intValue());
			}
			else if(otherParams[i] instanceof Long){
				dos.writeLong(((Long)otherParams[i]).longValue());
			}
			else if(otherParams[i] instanceof String){
				dos.writeUTF((String)otherParams[i]);
			}
			else if(otherParams[i] instanceof Persistent){
				((Persistent)otherParams[i]).write(dos);
			}
		}
	}

	/**
	 * @see org.fcitmuk.db.util.Persistent#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream dis) throws IOException,InstantiationException,IllegalAccessException{
		/*setUserName(dis.readUTF());
		setPassword(dis.readUTF());
		setSerializer(dis.readUTF());
		setLocale(dis.readUTF());
		setAction(dis.readByte());
		
		if(otherParams != null)
			for (int i = 0; i < otherParams.length; i++) {
				if(otherParams[i] instanceof Byte){
					dis.readByte(((Byte)otherParams[i]).byteValue());
				}
				else if(otherParams[i] instanceof Short){
					dis.writeShort(((Short)otherParams[i]).shortValue());
				}
				else if(otherParams[i] instanceof Integer){
					dis.writeInt(((Integer)otherParams[i]).intValue());
				}
				else if(otherParams[i] instanceof Long){
					dis.writeLong(((Long)otherParams[i]).longValue());
				}
				else if(otherParams[i] instanceof String){
					dis.writeUTF((String)otherParams[i]);
				}
			}*/
		
		throw new UnsupportedOperationException();
	}

}


/** The folder where to put form xml collected data in NON database mode. 
 * The files are named starting with form definition variable name and
 * ending with the form data id. 
 * For each submitted set of form data, a new folder, 
 * under the folder with the name of the study, under folder with name of user,
 * is created having a name
 * which is composed of the date and time of submission.
 * This will most of the times ensure uniqueness of the files, but if a 
 * file of the same name is found in the same folder,
 * this form data is considerered not saved and the user will be required
 * to resolve this, as it can be dangerous to automatically overwrite such data.
 * */
//public static final String FORMS_DATA_FOLDER = "FormsDataFolder";
