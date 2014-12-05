package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;

/**
 * An Xform user that would be downloaded on Device. This is to ensure that only
 * authorized openmrs users can do data entry on the device.
 * 
 * @author Daniel
 * @author Maimoona
 */

public class User implements Persistent{
	private int userId;
	private String name;
	private String password;
	/**
	 * DONT WRITE IT. JUST TO MAINTAIN SESSION IN MOBILE LOGIN
	 * @return
	 */
	private String clearTextPassword;
	private String salt;
	
	public User(){
		
	}
	
	public User(int userId,String name, String password, String salt, String clearTextpassword){
		this.userId = userId;
		this.name = name;
		this.password = password;
		this.salt = salt;
		this.clearTextPassword = clearTextpassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		setUserId(dis.readInt());
		setName(dis.readUTF());
		setPassword(dis.readUTF());
		setSalt(dis.readUTF());
	}
	
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getUserId());
		dos.writeUTF(getName());
		dos.writeUTF(getPassword());
		dos.writeUTF(getSalt());
	}

	/**
	 * DONT WRITE IT. JUST TO MAINTAIN SESSION IN MOBILE LOGIN
	 * @return
	 */
	public String getClearTextPassword() {
		return clearTextPassword;
	}

	/**
	 * DONT WRITE IT. JUST TO MAINTAIN SESSION IN MOBILE LOGIN
	 * @return
	 */
	public void setClearTextPassword(String clearTextpassword) {
		this.clearTextPassword = clearTextpassword;
	}

}
