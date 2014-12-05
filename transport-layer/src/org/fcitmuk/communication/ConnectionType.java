package org.fcitmuk.communication;

/**
 * 
 * @author Maimoona
 *
 */
public class ConnectionType {
	public static final String KEY_BLUETOOTH_SERVER_ID = "BLUETOOTH_SERVER_ID";
	public static final String KEY_BLUETOOTH_DEVICE_NAME = "BLUETOOTH_DEVICE_NAME";
	public static final String KEY_HTTP_URL = "HTTP_URL";
	public static final String KEY_SMS_DESTINATION_ADDRESS = "SMS_DESTINATION_ADDRESS";
	public static final String KEY_SMS_SOURCE_ADDRESS = "SMS_SOURCE_ADDRESS";
	
	public static final ConnectionType HTTP = new ConnectionType("HTTP", "&", "?", "javax.microedition.io.HttpConnection");
	public static final ConnectionType BLUETOOTH = new ConnectionType("BLUETOOTH", ";", ";", "javax.bluetooth.DiscoveryListener");
	public static final ConnectionType SMS = new ConnectionType("SMS", "", "", "javax.wireless.messaging.MessageConnection");
	// TODO FOR file standard was to check property. 
	//if(System.getProperty("microedition.io.file.FileConnection.version") != null)
	public static final ConnectionType FILE = new ConnectionType("FILE", "", "", "javax.microedition.io.file.FileConnection");
	// TODO FOR commport standard was to check property. 
	//if(System.getProperty("microedition.commports") != null)
	public static final ConnectionType USB = new ConnectionType("USB", "", "", "javax.microedition.io.CommConnection");

	private final String NAME;
	private final String PARAM_SEPARATOR;
	private final String URL_PARAM_SEPARATOR;
	private final boolean IS_AVAILABLE;
	
	public static ConnectionType getType(String connectionType){
		if(connectionType.toLowerCase().compareTo(HTTP.NAME.toLowerCase()) == 0){
			return HTTP;
		}
		else if(connectionType.toLowerCase().compareTo(BLUETOOTH.NAME.toLowerCase()) == 0){
			return BLUETOOTH;
		}
		else if(connectionType.toLowerCase().compareTo(SMS.NAME.toLowerCase()) == 0){
			return SMS;
		}
		else if(connectionType.toLowerCase().compareTo(FILE.NAME.toLowerCase()) == 0){
			return FILE;
		}
		else if(connectionType.toLowerCase().compareTo(USB.NAME.toLowerCase()) == 0){
			return USB;
		}
		return null;
	}
	
	public String NAME(){
		return NAME;
	}
	
	/**
	 * key-val pair separator of parameters to used while sending request to server.
	 * ex: & in http request OR ; in bluetooth request
	 * @return
	 */
	public String PARAM_SEPARATOR(){
		return PARAM_SEPARATOR;
	}
	
	public String URL_PARAM_SEPARATOR(){
		return URL_PARAM_SEPARATOR;
	}
	
	/**
	 * Whether connection type is available (can be established) on the system or not.
	 * @return
	 */
	public boolean IS_AVAILABLE(){
		return IS_AVAILABLE;
	}
	
	private ConnectionType(String name, String paramSeparator, String urlParamSeparator, String handlerClassName) {
		this.NAME = name;
		this.PARAM_SEPARATOR = paramSeparator;
		this.URL_PARAM_SEPARATOR = urlParamSeparator;
		this.IS_AVAILABLE = isConnectionAvailable(handlerClassName);
	}

	private boolean isConnectionAvailable(String handlerClassName) {
		try {
			Class.forName(handlerClassName);
			return true;
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		return false;
	}
}
