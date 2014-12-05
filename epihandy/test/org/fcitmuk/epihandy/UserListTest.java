/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.epihandy;


import java.util.Vector;

public class UserListTest {

	public static UserList getTestUserList(){
		Vector users = new Vector();
		
		/*String salt = UserManager.getRandomToken();
		String password  = "daniel123";
		String hashedPassword  = UserManager.encodeString(password + salt);
		users.addElement(new User(1,"Guyzb",hashedPassword,salt));
		
		salt = UserManager.getRandomToken();
		password  = "gibro123";
		hashedPassword  = UserManager.encodeString(password + salt);
		users.addElement(new User(2,"Gibro",hashedPassword,salt));
		
		salt = UserManager.getRandomToken();
		password  = "test";
		hashedPassword  = UserManager.encodeString(password + salt);
		users.addElement(new User(2,"test",hashedPassword,salt));*/
		
		return new UserList(users);
	}
}
