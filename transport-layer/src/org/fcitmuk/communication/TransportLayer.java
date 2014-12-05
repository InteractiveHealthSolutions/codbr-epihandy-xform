/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */

package org.fcitmuk.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Display;

import org.fcitmuk.communication.bluetooth.BluetoothClient;
import org.fcitmuk.communication.bluetooth.BluetoothClientListener;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.util.MenuText;

/**
 * Abstracts the communication details. This class is threaded such that the
 * user does not have to deal with threading issues which are a must for midlets
 * handling blocking calls. When requests are processed the user is notified by
 * callbacks through the BluetoothClientEventListener interface .
 * 
 * @author Daniel Kayiwa
 * 
 */
public class TransportLayer implements Runnable, BluetoothClientListener/* , MessageListener */{
	
	private ConnectionRequest connectionRequest;
	
	private static final int ACTION_DOWNLOAD = 99;
	private static final int ACTION_UPLOAD = 95;
	private static final int ACTION_SIMPLE = 90;

	// TODO Some users wanted the sending of forms to be done one by one instead
	// of the entire batch such that failure
	// occurs on a form by form basis instead of the entire batch.

	//public static final String BLUETOOTH_SERVER_ID = "F0E0D0C0B0A000908070605040302010";

	/** Reference to the listener for communication events. */
	protected TransportLayerListener eventListener;

	private ConnectionSettings conSettings;

	private Connection con;
	BluetoothClient btClient;
	private boolean cancelled = false;
	//private boolean cancelPrompt = false;

	private byte connectionRetries;
	private byte streamRetries;

	private int actionType;

	private static final byte MAX_CONNECTION_RETRIES = 3;

	/** Cconstructs a transport layer object. 
	 * @param mainMidlet */
	public TransportLayer(Display display) {
		conSettings = new ConnectionSettings(display);
	}

	protected TransportLayerListener getEventListener() {
		return this.eventListener;
	}

	public ConnectionSettings getConnectionSettings(){
		return conSettings;
	}

	public void download(TransportLayerListener eventListener, ConnectionRequest connectionRequest) {
		saveParameters(eventListener, ACTION_DOWNLOAD, null, null, connectionRequest);
		new Thread(this).start();
		//handleRequest();
	}
	
	public void upload(TransportLayerListener eventListener, ConnectionRequest connectionRequest) {
		saveParameters(eventListener, ACTION_UPLOAD, null, null, connectionRequest);
		new Thread(this).start();
		//handleRequest();
	}
	
	public void sendRequest(TransportLayerListener eventListener, ConnectionRequest connectionRequest) {
		saveParameters(eventListener, ACTION_SIMPLE, null, null, connectionRequest);
		new Thread(this).start();
		//handleRequest();
	}

	/**
	 * Uploads data over the transport layer.
	 * 
	 * @param dataInParams
	 *            - Data input connection parameters. eg which request type.
	 * @param dataIn
	 *            - Data to be sent.
	 * @param dataOutParams
	 *            - Data received parameters. eg failure or success status.
	 * @param dataOut
	 *            - Data received if any.
	 * @param eventListener
	 *            - Reference to listener to communication events.
	 */
	public void upload(Persistent dataInParams, Persistent dataIn,
			Persistent dataOutParams, Persistent dataOut,
			TransportLayerListener eventListener, String userName,
			String password, ConnectionRequest connectionRequest) {
		saveParameters(/*dataInParams, dataIn, */eventListener, ACTION_UPLOAD, userName, password, connectionRequest);
		new Thread(this).start();
	}

	/**
	 * Saves parameters to class level variables.
	 * 
	 * @param dataInParams
	 *            - Data input connection parameters. eg which request type.
	 * @param dataIn
	 *            - Data to be sent if any.
	 * @param dataOutParams
	 *            - Data received parameters. eg failure or success status.
	 * @param dataOut
	 *            - Data received if any.
	 * @param eventListener
	 *            - Reference to listener to communication events.
	 */
	private void saveParameters(/*Persistent dataIn, Persistent dataOut,*/
			TransportLayerListener eventListener, int actionType,
			String userName, String password, ConnectionRequest connectionRequest) {
		//this.dataInParams = dataInParams;
		//this.dataIn = dataIn;
		//this.dataOutParams = dataOutParams;
		//this.dataOut = dataOut;
		this.eventListener = eventListener;
		this.actionType = actionType;
		this.connectionRequest = connectionRequest;
	}

	/**
	 * Called when the thread starts to run the user request.
	 * 
	 */
	protected void handleRequest(ConnectionRequest connectionRequest) {

		cancelled = false;
		btClient = null;

		streamRetries = 0;

		try {
			ConnectionType connectionType = connectionRequest.getConnectionType();
			if(connectionRequest.isUseConnectionSettings()){
				String cType = getConnectionSettings().getSettingList().getSettingValue(ConnectionSettings.SETTING_CONNECTION_TYPE, "HTTP");
				connectionType = ConnectionType.getType(cType);
			}
			
			if (connectionType == ConnectionType.HTTP) {
				connectHttp();
			}
			else if(connectionType == ConnectionType.BLUETOOTH){
				connectBluetooth();
			}
			/*else if(connectionRequest.getConnectionType().NAME().compareTo(ConnectionType.SMS.NAME()) == 0){
				connectSMS();
			}
			else if(connectionRequest.getConnectionType().NAME().compareTo(ConnectionType.USB.NAME()) == 0){
				connectSerial();
			}
			else if(connectionRequest.getConnectionType().NAME().compareTo(ConnectionType.FILE.NAME()) == 0){
				connectFile();
			}*/
		} catch (Exception e) {
			this.eventListener.errorOccured(MenuText.PROBLEM_HANDLING_REQUEST(), e);
			e.printStackTrace();
		}
	}

	/**
	 * Writes and writes data to and from the streams.
	 * 
	 * @param dos
	 *            - Stream to write data to.
	 * @param dis
	 *            - Stream to read data from.
	 * @throws IOException
	 *             - Thrown when there is a problem for a read or write
	 *             operation.
	 */
//	protected void handleStreams(DataOutputStream dos, DataInputStream dis)
//			throws IOException {
//		try {
//			showConnectionProgress(MenuText.TRANSFERING_DATA());
//
////			if (dataInParams != null) // for now http will not send this value.
////				dataInParams.write(dos);
//
//			if (dataIn != null)
//				dataIn.write(dos);
//
//			dos.flush(); // if you dont do this, the client will block on a
//							// read.
//			dos.close();
//			dos = null; // Not setting to null results in KErrCouldNotConnect on
//						// the client.
//
//			readResponseData(dis);
//
//		} catch (Exception e) {
//			this.eventListener.errorOccured(
//					MenuText.PROBLEM_HANDLING_STREAMS(), e);
//			// e.printStackTrace(); //TODO May need to report this to user.
//		} finally {
//			try {
//				dis.close();
//				dis = null;
//			} catch (Exception ex) {
//				// ex.printStackTrace();
//			}
//		}
//	}

	/**
	 * Handles File communications. eg memory card transfer.
	 * 
	 * @throws IOException
	 *             - Thrown when there is a problem reading from or writting to
	 *             the connection stream.
	 */
	/*
	 * protected void connectFile() throws IOException{
	 * 
	 * //connectSocketServer(); connectSocketClint(); }
	 */

	/*
	 * private void connectSocketServer() throws IOException{
	 * 
	 * alertMsg.showProgress(title,System.getProperty("microedition.hostname")+
	 * "=Waiting socket connection...");
	 * 
	 * ServerSocketConnection server =
	 * (ServerSocketConnection)Connector.open("socket://:");
	 * 
	 * 
	 * alertMsg.showProgress(title,"Listening at address="+server.getLocalAddress
	 * ()+" & Port="+server.getLocalPort());
	 * 
	 * // Wait for a connection. SocketConnection con =
	 * (SocketConnection)server.acceptAndOpen();
	 * //si.setText("Connection accepted");
	 * 
	 * alertMsg.showProgress(title,"Connection accepted. Processing...");
	 * 
	 * InputStream is = con.openInputStream(); OutputStream os =
	 * con.openOutputStream(); }
	 */

	/*
	 * private void connectSocketClint() throws IOException{
	 * 
	 * alertMsg.showProgress(title,"Socket connecting...");
	 * 
	 * SocketConnection con =
	 * (SocketConnection)Connector.open("socket://10.10.3.76:2105");
	 * 
	 * alertMsg.showProgress(title,"Connection accepted. Processing...");
	 * 
	 * InputStream is = con.openInputStream(); OutputStream os =
	 * con.openOutputStream();
	 * 
	 * os.write(1); os.flush();
	 * 
	 * alertMsg.showProgress(title,"Processing..."); //InputStream is =
	 * con.openInputStream(); //OutputStream os = con.openOutputStream(); }
	 */

	/**
	 * Handles HTTP communications. eg GPRS.
	 * 
	 * @throws IOException
	 *             - Thrown when there is a problem reading from or writting to
	 *             the connection stream.
	 */
	protected void connectHttp() throws IOException {

		try {
			String HTTP_URL = "";
			if(connectionRequest.isUseConnectionSettings()){
				HTTP_URL = connectionRequest.getConnectionURL(getConnectionSettings().getSettingList().getSettingValue(ConnectionSettings.SETTING_HTTP_SERVER_URL, ""),
						ConnectionType.HTTP);
			}
			else {
				HTTP_URL = connectionRequest.getConnectionURL();
			}
			System.out.println(HTTP_URL);
			con = (HttpConnection) Connector.open(HTTP_URL, Connector.READ_WRITE);
			HttpConnection httpCon = (HttpConnection) con;

			//((HttpConnection) con).setRequestProperty("Content-Length", ""+dataToBeSend.length());
	        
			httpCon.setRequestMethod(HttpConnection.POST);
			//((HttpConnection) con).setRequestProperty("Content-Type",	"application/octet-stream");
			//((HttpConnection) con).setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.0");
			//((HttpConnection) con).setRequestProperty("Content-Language", "en-US");

			DataOutputStream dos = httpCon.openDataOutputStream();
			if ( connectionRequest.getDataInput() != null) {
				 connectionRequest.getDataInput().write(dos);
				dos.flush();
			}

			int status = httpCon.getResponseCode();
			if (status != HttpConnection.HTTP_OK)
				this.eventListener.errorOccured(MenuText.RESPONSE_CODE_FAIL()+status, null);
			else {// TODO May need some more specific failure codes
				// For HTTP, we get only data back. Status is via HTTP status
				// code. So no need of readin the data in param.
				// if(dataOut != null) //There are cases where we are not
				// getting any data back apart fomr the HTTP status code.
				// readHttpData(((HttpConnection)con).openDataInputStream());

				DataInputStream dis = httpCon.openDataInputStream();
				if (actionType == ACTION_DOWNLOAD) //if download action is performed					
				{
					
					this.eventListener.downloaded(dis, connectionRequest.getDataOutput(), connectionRequest.isZippedStreamExpected());				
				}					
				else if(actionType == ACTION_UPLOAD){
					this.eventListener.uploaded(dis, connectionRequest.getDataOutput(), connectionRequest.isZippedStreamExpected());		
				}
				else if(actionType == ACTION_SIMPLE){
					byte[] b = new byte[(int) httpCon.getLength()];
					dis.read(b);
					this.eventListener.receivedResponse(b);
				}
			}
		}
		catch (SecurityException e) {
			this.eventListener.errorOccured(MenuText.DEVICE_PERMISSION_DENIED(), e);
			e.printStackTrace();
		}
		catch (Exception e) {
			this.eventListener.errorOccured(MenuText.PROBLEM_HANDLING_REQUEST(), e);
			e.printStackTrace();
		} 
		finally {
			if (con != null)
				con.close();
		}
	}

	/** Resets the concatenated SMS counters. */
	/*
	 * private void resetSmsDataParams(){ /*smsdata = null; smsCurrentLen = 0;
	 * smsTotalLen = 0;
	 */

	/*
	 * TransportLayerStorage.getInstance().deleteSmsAssemblerData(); }
	 */

	/**
	 * Handles SMS communications..
	 * 
	 * @throws IOException
	 *             - Thrown when there is a problem reading from or writting to
	 *             the connection stream.
	 */
	/*
	 * protected void connectSMS() throws IOException,Exception{
	 * 
	 * closeConnection();
	 * 
	 * //Try checking just incase user closed before receiving the complete set
	 * of //concatenated sms es. String[] connections =
	 * PushRegistry.listConnections(true); if(connections != null &&
	 * connections.length > 0) handleIncomingSmsData(connections[0]); else{
	 * //TransportLayerStorage.getInstance().deleteSmsAssemblerData();
	 * 
	 * String destAddress =
	 * (String)conParams.get(TransportLayer.KEY_SMS_DESTINATION_ADDRESS);
	 * //"sms://+256772963035:1234"; //"sms://+256772963035:1234";; String
	 * srcAddress =
	 * (String)conParams.get(TransportLayer.KEY_SMS_SOURCE_ADDRESS);
	 * //"sms://:3333"; con = (MessageConnection)Connector.open(srcAddress);
	 * ((MessageConnection)con).setMessageListener(this);
	 * 
	 * showConnectionProgress("SMS Connected to server ["+destAddress+
	 * "]. Tranfering data...");
	 * 
	 * //PushRegistry.listConnections(true);
	 * 
	 * /*BinaryMessage msg =
	 * (BinaryMessage)((MessageConnection)con).newMessage(MessageConnection
	 * .BINARY_MESSAGE); msg.setPayloadData(getPayLoadData());
	 * msg.setAddress(destAddress); ((MessageConnection)con).send(msg);
	 */

	/*
	 * sendMessage(destAddress,getPayLoadData());
	 * 
	 * showConnectionProgress("Sent Message to ["+destAddress+
	 * "]. Waiting for reply..."); } }
	 */

	/*
	 * public void handleIncomingSmsData(String connectionAddress){ try{
	 * showConnectionProgress("Processing incoming SMS data..."); con =
	 * (MessageConnection)Connector.open(connectionAddress);
	 * ((MessageConnection)con).setMessageListener(this); } catch(Exception e){
	 * eventListener.errorOccured("Problem handling incoming sms data",e); } }
	 */

	/*
	 * private byte[] getPayLoadData(){ try{ ByteArrayOutputStream baos = new
	 * ByteArrayOutputStream(); ZOutputStream zip = new
	 * ZOutputStream(baos,JZlib.Z_BEST_COMPRESSION); DataOutputStream dos = new
	 * DataOutputStream(baos);
	 * 
	 * if(dataInParams != null) dataInParams.write(dos); if(dataIn != null)
	 * dataIn.write(dos);
	 * 
	 * dos.flush(); zip.finish();
	 * 
	 * return baos.toByteArray(); } catch(Exception e){
	 * this.eventListener.errorOccured("Problem getting payload data",e);
	 * //e.printStackTrace(); }
	 * 
	 * return null; }
	 */

	/*
	 * private void sendMessage(String address, byte[] srcBytes) throws
	 * Exception{ /** The size of a message part.
	 */
	/*
	 * final int MSG_PART_SIZE = 1000; final int LEN_BYTE_SIZE = 4;
	 * 
	 * int len = srcBytes.length; byte[] dstBytes; int count =
	 * (len/MSG_PART_SIZE)+1;
	 * 
	 * int srcStartIndex = 0, destStartIndex=0,copyLen =
	 * MSG_PART_SIZE,bytesCopied=0; for(int i=1; i<=count; i++){ srcStartIndex =
	 * (i-1)*MSG_PART_SIZE; if(bytesCopied + copyLen > len) copyLen =
	 * len-bytesCopied;
	 * 
	 * destStartIndex = ((i==1) ? LEN_BYTE_SIZE : 0); dstBytes = new
	 * byte[copyLen+destStartIndex];
	 * 
	 * //Use the first four bytes for the total message length. if(i == 1)
	 * setBytesLength(dstBytes,len);
	 * 
	 * //Add user data from the fifth byte onwards. System.arraycopy(srcBytes,
	 * srcStartIndex, dstBytes, destStartIndex, copyLen); bytesCopied +=
	 * copyLen;
	 * 
	 * BinaryMessage msg =
	 * (BinaryMessage)((MessageConnection)con).newMessage(MessageConnection
	 * .BINARY_MESSAGE); msg.setPayloadData(dstBytes); msg.setAddress(address);
	 * ((MessageConnection)con).send(msg);
	 * 
	 * test(dstBytes); } }
	 */

	/*
	 * private void setBytesLength(byte[] bytes, int len){ byte[] sizeBytes =
	 * getBytes(len); bytes[3] = sizeBytes[3]; bytes[2] = sizeBytes[2]; bytes[1]
	 * = sizeBytes[1]; bytes[0] = sizeBytes[0]; }
	 */

	/*
	 * private byte[] getBytes(int num){ ByteArrayOutputStream baos = new
	 * ByteArrayOutputStream(); DataOutputStream dos = new
	 * DataOutputStream(baos); try{ dos.writeInt(num); }catch(Exception
	 * e){return null;}
	 * 
	 * return baos.toByteArray(); }
	 */

	/**
	 * 
	 * @param serviceUrl 
	 * @throws IOException
	 *             - Thrown when there is a problem reading from or writting to
	 *             the connection stream.
	 * @throws Exception
	 *             - Thrown when serial communication is not implemented.
	 */
	/*
	 * protected void connectSerial() throws IOException,Exception {
	 * 
	 * CommConnection con = null;
	 * 
	 * try{ //"comm:IR0"; //String CON_STR = (String)conParams.get("CON_STR");
	 * // + ";baudrate=115200;" nokiacomm String url = "comm:" + getPort() +
	 * ";baudrate=9600";
	 * 
	 * showConnectionProgress("Connecting to server with url " + url + " ...");
	 * 
	 * con = (CommConnection)Connector.open(url,Connector.READ_WRITE,true);
	 * 
	 * showConnectionProgress("Connected to server with url " + url + " ...");
	 * 
	 * //TODO May need to check success status
	 * handleStreams(con.openDataOutputStream(),con.openDataInputStream());
	 * }finally{ if(con != null) con.close(); } }
	 * 
	 * private String getPort() throws Exception{ String port1; String ports =
	 * System.getProperty("microedition.commports"); if(ports == null) throw new
	 * Exception("Serial Communication not implemented.");
	 * 
	 * int comma = ports.indexOf(','); if (comma > 0) { // Parse the first port
	 * from the available ports list. port1 = ports.substring(0, comma); } else
	 * { // Only one serial port available. port1 =ports; } return port1; }
	 */

	private boolean openBluetoothConnection(String serviceUrl) {
		try {
			connectionRetries++;
			con = (StreamConnection) Connector.open(serviceUrl,
					Connector.READ_WRITE, true);
			return true;
		} catch (Exception e) {
			// Thread.currentThread().notifyAll();
			if (con != null) {
				try {
					con.close();
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
				con = null; // Not setting this to null results into
							// KErrAlreadyExists failures on the client
			}
			if (connectionRetries > MAX_CONNECTION_RETRIES)
				return false;
			else
				openBluetoothConnection(serviceUrl);
		}
		return true;
	}

	/**
	 * Handles bluetooth communications.
	 * 
	 */
	protected void connectBluetooth() throws IOException {
		/*try {
			streamRetries++;

			if (serviceUrl == null) {
				showConnectionProgress(MenuText.GETTING_BLUETOOTH_URL());
				String bluetooth_server_id = (String) conParams
						.get(TransportLayer.KEY_BLUETOOTH_SERVER_ID);
				String deviceName = (String) conParams
						.get(TransportLayer.KEY_BLUETOOTH_DEVICE_NAME);
				btClient = new BluetoothClient(bluetooth_server_id, this);
				serviceUrl = btClient.getServiceUrl(deviceName);
			}

			if (serviceUrl == null)
				return; // An error message was already reported by whatever
						// made this null.

			showConnectionProgress(MenuText.OPENING_BLUETOOTH_CONNECTION());

			// TODO May need to check success status
			connectionRetries = 0;
			if (openBluetoothConnection(serviceUrl)) {
				showConnectionProgress(MenuText.GETTINGS_STREAM());
				handleStreams(((StreamConnection) con).openDataOutputStream(),
						((StreamConnection) con).openDataInputStream());
			} else
				eventListener.errorOccured(MenuText.OPEN_CONNECTION_FAIL(),
						null);
		} catch (Exception ex) {
			// if(streamRetries > 2){
			this.eventListener.errorOccured(MenuText.PROBLEM_OPENING_STREAMS(),
					ex);
			// ex.printStackTrace();
			
			 * } else{ //serviceUrl = null;showConnectionProgress(
			 * "Refreshing bluetooth service connection........."); //serviceUrl
			 * = btClient.getServiceUrlMini(); connectBluetooth(); }
			 
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
				con = null; // Not setting this to null results into
							// KErrAlreadyExists failures on the client
			}
		}*/
	}

	/**
	 * Called when the thread starts executing.
	 */
	public void run() {
		handleRequest(connectionRequest);
	}

	public void errorOccured(String errorMessage, Exception e) {
		if (!cancelled) // If user canclled, we dont need to bubble the
						// connection interrupt error message.
			this.eventListener.errorOccured(errorMessage, e);
	}
	/*
	 * private int getSize(byte[] b){ DataInputStream dis = new
	 * DataInputStream(new ByteArrayInputStream(b)); try{ return dis.readInt();
	 * }catch(IOException e){return -1;} }
	 */

	// public void notifyIncomingMessage(MessageConnection msgCon){
	/*
	 * try{ Message msg = msgCon.receive();
	 * 
	 * if(msg instanceof BinaryMessage){ BinaryMessage binMsg =
	 * (BinaryMessage)msg; byte[] payload = binMsg.getPayloadData();
	 * 
	 * SmsAssemblerData assemblerData =
	 * TransportLayerStorage.getInstance().getSmsAssemblerData();
	 * if(assemblerData == null) assemblerData = new SmsAssemblerData();
	 * 
	 * /*if(smsdata == null){ //first send, this will be null smsTotalLen =
	 * getSize(payload); //read off first four bytes for total length. smsdata =
	 * new byte[smsTotalLen]; //create storage space. smsCurrentLen =
	 * payload.length - 4; //actual data is minus the four length bytes.
	 * System.arraycopy(payload,4,smsdata,0,smsCurrentLen); } else{ //this
	 * should be a send after the first one
	 * System.arraycopy(payload,0,smsdata,smsCurrentLen,payload.length);
	 * smsCurrentLen = (smsCurrentLen + payload.length); }
	 */

	// if(smsCurrentLen == smsTotalLen){ //if all bytes have been read.
	/*
	 * if(SmsAssembler.assembleMessage(assemblerData,payload)){ DataInputStream
	 * dis = new DataInputStream(new
	 * ByteArrayInputStream(assemblerData.getSmsData())); dis =
	 * this.getDecompressedStream(dis);
	 * 
	 * if(dataOut != null) dataOut.read(dis); if(this.isDownload)
	 * this.eventListener.downloaded(dataOutParams,dataOut); else
	 * this.eventListener.uploaded(dataOutParams,dataOut);
	 * 
	 * //resetSmsDataParams();
	 * TransportLayerStorage.getInstance().deleteSmsAssemblerData(); } else
	 * showConnectionProgress("Transferred " + assemblerData.getSmsCurrentLen()
	 * + " of " + assemblerData.getSmsTotalLen() + " bytes. " +
	 * (assemblerData.getSmsTotalLen()-assemblerData.getSmsCurrentLen()) +
	 * " left."); } elsethis.eventListener.errorOccured(
	 * "notifyIncomingMessage: Some non non binary text message.",null); }
	 * catch(Exception e){
	 * this.eventListener.errorOccured("Problem handling notifyIncomingMessage"
	 * ,e); //e.printStackTrace(); }
	 */
	// }

	/*
	 * private void closeConnection(){ try{ if(con != null){ con.close(); con =
	 * null; } }catch(Exception e){} }
	 */
}
