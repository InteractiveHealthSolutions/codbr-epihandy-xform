package org.ihs.codbr.handler;

import java.io.DataInputStream;

import net.sf.jazzlib.GZIPInputStream;

import org.fcitmuk.communication.TransportLayerListener;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.openmrs.ResponseHeader;
import org.ihs.codbr.constant.ErrorMessages;

public class ResponseHandler implements TransportLayerListener
{
	private static boolean isConnectionAvailable = true;

	private boolean async;
	private ResponseNotification notifierHandler;
	
	public ResponseHandler(ResponseNotification notifierHandler, boolean async) {
		this.notifierHandler = notifierHandler;
		this.async = async;
	}
	
	public void uploaded(DataInputStream responseStream, Persistent dataOut, boolean isZippedStreamExpected) {
		try{
			ResponseHeader response = new ResponseHeader();
			if (isZippedStreamExpected) {
				GZIPInputStream gz = new GZIPInputStream(responseStream);
				responseStream = new DataInputStream(gz);
	
				response.read(responseStream);
				
				if(response.getStatus() == ResponseHeader.STATUS_SUCCESS){
					dataOut.read(responseStream);
					notifierHandler.response("Success", dataOut);
				}
				else if(response.getStatus() == ResponseHeader.STATUS_ACCESS_DENIED){
					notifierHandler.response(ErrorMessages.ACCESS_DENIED_ON_SERVER , dataOut);
				}
				else {
					notifierHandler.response(ErrorMessages.ERROR_OCCURRED_ON_SERVER , dataOut);
				}
			}
			else {
				dataOut.read(responseStream);
				notifierHandler.response("Success", dataOut);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloaded(DataInputStream downloadedStream, Persistent dataOut, boolean isZippedStreamExpected) {
		try{
//			GZIPInputStream gis = new GZIPInputStream(downloadedStream);
//    byte[] buf = new byte[25];
//    int len;
//						// Open the GZIPInputStream the regular way
//       long t0,t1;                  // time stamps
//       t0 = System.currentTimeMillis();
//
//				 //Read the gzip archive.
//       while ((len = gis.read(buf)) >= 0)
//				 {
//						 // Print the content if you want
//						  System.out.println(""+ new String(buf));
//       }
			ResponseHeader response = new ResponseHeader();
			if (isZippedStreamExpected) {
				GZIPInputStream gz = new GZIPInputStream(downloadedStream);
				//ZInputStream gz = new ZInputStream(downloadedStream);
				downloadedStream = new DataInputStream(gz);

				response.read(downloadedStream);
				
				if(response.getStatus() == ResponseHeader.STATUS_SUCCESS){
					dataOut.read(downloadedStream);
					notifierHandler.response(null, dataOut);
				}
				else if(response.getStatus() == ResponseHeader.STATUS_ACCESS_DENIED){
					notifierHandler.response(ErrorMessages.ACCESS_DENIED_ON_SERVER , dataOut);
				}
				else {
					notifierHandler.response(ErrorMessages.ERROR_OCCURRED_ON_SERVER , dataOut);
				}
			}
			else {
				dataOut.read(downloadedStream);
				notifierHandler.response(null, dataOut);
			}

/*	     //  
		       StringBuffer sb = new StringBuffer();
		       int c;
		       while ((c = di2.read()) != -1) {
		       sb.append((char) c);
		       }
		       String sss = sb.toString();
*/
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
/*		if(!async){
			mainMidlet.setDisplay(currentDisplayable);
		}*/
		isConnectionAvailable = true;
	}

	public void receivedResponse(byte[] receivedbytes) {
		notifierHandler.response(receivedbytes, null);
	}
	
	public void errorOccured(String errorMessage, Exception e) {
		notifierHandler.error(errorMessage + "; "+e.getMessage());
	}

	public void cancelled() {
		// TODO Auto-generated method stub
		
	}

	public void updateCommunicationParams() {
		// TODO Auto-generated method stub
		
	}



}
