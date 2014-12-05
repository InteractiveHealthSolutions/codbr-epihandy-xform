/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.communication.bluetooth;

/**
 * Interface through which the bluetooth client communicates to the user.
 * 
 * @author Daniel Kayiwa
 *
 */
public interface BluetoothClientListener {
	/**
	 * Called when an error occurs during any bluetooth operation.
	 * 
	 * @param errorMessage - the error message.
	 * @param e - the exception, if any, that did lead to this error.
	 */
	public void errorOccured(String errorMessage, Exception e);
}
