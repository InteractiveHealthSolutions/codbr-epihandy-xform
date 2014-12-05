package org.fcitmuk.communication;

import org.fcitmuk.db.util.Persistent;

public class ConnectionRequest 
{
	private ConnectionParameter connectionParameter; 
	private ConnectionType connectionType;
	private String serverUrl;
	private String subURL;
	private boolean useConnectionSettings;
	private Persistent dataInput;
	private Persistent dataOutput;
	private boolean isZippedStreamExpected;
	
	public ConnectionRequest(boolean useConnectionSettings, String subUrl, ConnectionParameter connectionParameter, Persistent dataInput, Persistent dataOutput, boolean isZippedStreamExpected) {
		this.setUseConnectionSettings(useConnectionSettings);
		this.subURL = subUrl;
		this.dataInput = dataInput;
		this.dataOutput = dataOutput;
		this.connectionParameter = connectionParameter;
		this.setZippedStreamExpected(isZippedStreamExpected);
	}
	
	public ConnectionRequest(ConnectionType connectionType, String serverUrl, Persistent dataInput, Persistent dataOutput, boolean isZippedStreamExpected) {
		this.connectionType = connectionType;
		this.serverUrl = serverUrl;
		this.dataInput = dataInput;
		this.dataOutput = dataOutput;
		this.setZippedStreamExpected(isZippedStreamExpected);
	}
	
	public ConnectionRequest(ConnectionType connectionType, String serverUrl, ConnectionParameter connectionParameter, Persistent dataInput, Persistent dataOutput, boolean isZippedStreamExpected) {
		this.connectionType = connectionType;
		this.serverUrl = serverUrl;
		this.dataInput = dataInput;
		this.dataOutput = dataOutput;
		this.connectionParameter = connectionParameter;
		this.isZippedStreamExpected = isZippedStreamExpected;
	}
	
	public void setConnectionParameter(ConnectionParameter connectionParameter){
		this.connectionParameter = connectionParameter;
	}
	
	public ConnectionParameter getConnectionParameter(){
		return connectionParameter;
	}
	
	public ConnectionType getConnectionType(){
		return connectionType;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	public String getConnectionURL(){
		StringBuffer requrl = new StringBuffer(makeRequestURL());
		
		if(!requrl.toString().endsWith(connectionType.URL_PARAM_SEPARATOR())){
			requrl.append(connectionType.URL_PARAM_SEPARATOR());
		}
		
		requrl.append(connectionParameter.getAllParams(connectionType.PARAM_SEPARATOR()));
		return requrl.toString();
	}
	
	public String getConnectionURL(String serverURL, ConnectionType conType){
		this.serverUrl = serverURL;
		StringBuffer requrl = new StringBuffer(makeRequestURL());
		
		if(!requrl.toString().endsWith(conType.URL_PARAM_SEPARATOR())){
			requrl.append(conType.URL_PARAM_SEPARATOR());
		}
		
		requrl.append(connectionParameter.getAllParams(conType.PARAM_SEPARATOR()));
		return requrl.toString();
	}

	private String makeRequestURL(){
		if(!serverUrl.endsWith("/")){
			serverUrl += "/";
		}
		if(serverUrl.endsWith("?")){
			serverUrl = serverUrl.substring(0,serverUrl.length()-1);
		}
		subURL = subURL == null?"":subURL.trim();
		if(subURL.startsWith("/")){
			subURL = subURL.substring(1,subURL.length());
		}

		StringBuffer requrl = new StringBuffer(serverUrl+subURL);
		
		return requrl.toString();
	}
	
	public Persistent getDataInput() {
		return dataInput;
	}

	public Persistent getDataOutput() {
		return dataOutput;
	}

	public boolean isZippedStreamExpected() {
		return isZippedStreamExpected;
	}

	public void setZippedStreamExpected(boolean isZippedStreamExpected) {
		this.isZippedStreamExpected = isZippedStreamExpected;
	}

	public boolean isUseConnectionSettings() {
		return useConnectionSettings;
	}

	private void setUseConnectionSettings(boolean useConnectionSettings) {
		this.useConnectionSettings = useConnectionSettings;
	}
}
