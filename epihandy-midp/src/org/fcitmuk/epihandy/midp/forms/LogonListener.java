/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */


package org.fcitmuk.epihandy.midp.forms;


/**
 * 
 * @author Daniel
 *
 */
public interface LogonListener {
	public boolean onLoggedOn();
	public void onLogonCancel();
}
