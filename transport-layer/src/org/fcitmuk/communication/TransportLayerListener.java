/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.communication;

import java.io.DataInputStream;

import org.fcitmuk.db.util.Persistent;

/**
 * Interface through which the transport layer communicates to the user.
 * 
 * @author Daniel Kayiwa
 *
 */
public interface TransportLayerListener {
	
	/**
	 * Called after data has been successfully uploaded.
	 * 
	 */
	public void uploaded(DataInputStream responseStream, Persistent dataOut, boolean isZippedStreamExpected);
	
	/**
	 * Called after data has been successfully downloaded.
	 * 
	 */
	public void downloaded(DataInputStream downloadedStream, Persistent dataOut, boolean isZippedStreamExpected);
	
	public void receivedResponse(byte[] receivedbytes);
	
	/**
	 * Called when an error occurs during a data upload or download.
	 * 
	 * @param errorMessage - the error message.
	 * @param e - the exception, if any, that did lead to this error.
	 */
	public void errorOccured(String errorMessage, Exception e);
	
	public void cancelled();
	
	public void updateCommunicationParams();
}
