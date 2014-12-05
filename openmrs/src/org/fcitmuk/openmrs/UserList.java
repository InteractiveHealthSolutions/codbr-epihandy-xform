package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.db.util.PersistentHelper;
import org.fcitmuk.db.util.Record;

/**
 * @author Maimoona
 */

public class UserList implements Persistent, Record{

	private Vector users = new Vector();

	public UserList(){

	}

	public Vector getUsers() {
		return users;
	}

	public void addUser(User user){
		users.addElement((User) user);
	}

	public void addUsers(Vector userList){
		if(userList != null){
			for(int i=0; i<userList.size(); i++ )
				addUser((User) userList.elementAt(i));
		}
	}

	public int size(){
		return users.size();
	}

	public User getUser(int index){
		return (User)users.elementAt(index);
	}

	/** 
	 * Reads the users from supplied stream.
	 * 
	 * @param dis - the stream to read from.
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		this.users = PersistentHelper.readBig(dis,new User().getClass());
	}

	/** 
	 * Writes the users to supplied stream.
	 * 
	 * @param dos - the stream to write to.
	 * @throws IOException
	 */
	public void write(DataOutputStream dos) throws IOException {
		PersistentHelper.writeBig(getUsers(), dos);
	}

	public int getRecordId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRecordId(int id) {
		// TODO Auto-generated method stub
		
	}
}
