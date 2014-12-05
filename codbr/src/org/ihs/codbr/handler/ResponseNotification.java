package org.ihs.codbr.handler;

import org.fcitmuk.db.util.Persistent;

public interface ResponseNotification {

	public void response(Object otherInformation, Persistent persistentData);
	
	public void error(Object errorInformation);
}
