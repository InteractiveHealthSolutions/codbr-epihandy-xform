/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.util;


/** 
 * This interface is the means of communication betwen the AlerMessage class and api users. 
 * 
 * @author Daniel
 *
 */
public interface AlertMessageListener {
	
	public static byte MSG_OK = 1;
	public static byte MSG_CANCEL = 2;
	
	public void onAlertMessage(byte msg);
}
