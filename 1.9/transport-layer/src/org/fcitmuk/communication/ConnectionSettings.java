package org.fcitmuk.communication;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import org.fcitmuk.midp.mvc.AbstractView;
import org.fcitmuk.util.DefaultCommands;
import org.fcitmuk.util.MenuText;
import org.fcitmuk.util.Properties;
import org.fcitmuk.util.SimpleOrderedHashtable;

/**
 * This class shows existing connection parameters and lets the user modify them,
 * before passing them over.
 * 
 * @author Daniel
 *
 */
public class ConnectionSettings extends AbstractView {

	private static final int CA_NONE = 0;
	private static final int CA_CON_TYPES = 1;
	private static final int CA_CON_PARAMS = 2;
	
	private static final String SERVLET_MAPPING = "mpsubmit";
	
	String userName = "";
	String password = "";
	
	/** The connection type to use. */
	private byte conType = TransportLayer.CON_TYPE_NULL;
	
	/** The connection parameters. */
	protected Hashtable conParams = new Hashtable();
	
	/** The connection parameters. */
	protected Vector connectionParameters = new Vector();
	
	private SimpleOrderedHashtable conTypes;
	
	/** Reference to the parent. */
	TransportLayer defTransLayer;
	
	private int currentAction = CA_NONE;
	
	private ChoiceGroup hTTPSelectionBox;
	private ChoiceGroup advancedOptionsCheckbox;
	private StringItem defaultUrlHeading;
	private TextField defaultUrlTextField;
	private TextField advancedUrlTextField;
	private String serverURL;
	
	private ConnectionSettingsListener eventHandler=null;
	
	public ConnectionSettings(){
		
	}
	
	/** 
	 * Displays a screen for the user to select a connection type.
	 * 
	 * @param display - reference to the current display.
	 * @param prevScreen - the screen to display after dismissing our screens.
	 */
	public void getUserSettings(Display display, Displayable prevScreen, byte conType, SimpleOrderedHashtable conTypes,Hashtable conParams, TransportLayer defTransLayer, String name, String password, Vector connectionParameters, ConnectionSettingsListener listener){
		AbstractView.display = display;
		this.prevScreen = prevScreen;
		this.conTypes = conTypes;
		this.conParams = conParams;
		this.connectionParameters = connectionParameters;
		this.defTransLayer = defTransLayer;
		this.eventHandler =listener;
		
		serverURL = (String) conParams.get(TransportLayer.KEY_HTTP_URL);
		if (serverURL == null || serverURL.trim().equals("")) {
			serverURL = getDefaults().getProperty("httpUrl");
		}

		hTTPSelectionBox = new ChoiceGroup("", ChoiceGroup.POPUP);
		hTTPSelectionBox.append("HTTP", null);
		hTTPSelectionBox.append("HTTPS", null);
		hTTPSelectionBox.setSelectedIndex(0, true);
		defaultUrlHeading = new StringItem("Server Address", null);
		defaultUrlTextField = new TextField(null, getServerName(serverURL), 500, TextField.ANY);
		advancedUrlTextField = new TextField("Server URL", serverURL, 500, TextField.ANY);
		advancedOptionsCheckbox = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		advancedOptionsCheckbox.append("Advanced view", null);

		// If there is only one option, ask for params directly
		if (conTypes.size() == 1) {
			this.conType = fromIndexToConType(0);
			showConParams();
			return;
		}
		
		currentAction = CA_CON_TYPES;
		
		List list = new List(MenuText.CONNECTION_TYPE(), Choice.IMPLICIT);
		list.setFitPolicy(List.TEXT_WRAP_ON);
		screen = list;
		
		//TODO This hashtable does not maintain the order on devices like sony erickson.
		Enumeration keys = conTypes.keys();
		Object key;
		while(keys.hasMoreElements()){
			key = keys.nextElement();
			((List)screen).append(conTypes.get(key).toString(), null);
		}
			
		screen.addCommand(DefaultCommands.cmdOk);
		screen.addCommand(DefaultCommands.cmdCancel);
		byte index = fromConTypeToIndex(conType);
		if(index >= 0 && index < ((List)screen).size())
			((List)screen).setSelectedIndex(index,true);
		screen.setCommandListener(this);
				
		AbstractView.display.setCurrent(screen);
	}
	
	private byte fromConTypeToIndex(byte conType){
		return (byte)conTypes.getIndex(new Byte(conType));
	}
	
	private byte fromIndexToConType(int index){
		return ((Byte)conTypes.keyAt(index)).byteValue();
	}
		
	/**
	 * Processes the command events.
	 * 
	 * @param c - the issued command.
	 * @param d - the screen object the command was issued for.
	 */
	public void commandAction(Command c, Displayable d) {
		try{
			if(c == DefaultCommands.cmdOk || c == List.SELECT_COMMAND)
				handleOkCommand(d);
			else if(c == DefaultCommands.cmdCancel)
				handleCancelCommand(d);
		}
		catch(Exception e){
			// TODO: Handle this gracefully
		}
	}
	
	/**
	 * Processes the cancel command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleCancelCommand(Displayable d){
		if(currentAction == CA_CON_PARAMS && conTypes.size() > 1){
			currentAction = CA_CON_TYPES;
			display.setCurrent(screen);
		}
		else
			defTransLayer.onConnectionSettingsClosed(false);
	}
	
	/**
	 * Processes the OK command event.
	 * 
	 * @param d - the screen object the command was issued for.
	 */
	private void handleOkCommand(Displayable d){
		if(currentAction == CA_CON_PARAMS){
			if(d != null){
				if(conType == TransportLayer.CON_TYPE_HTTP){
					conParams.put(TransportLayer.KEY_HTTP_URL, serverURL);

					for(int i=0; i<connectionParameters.size(); i++)
					{
						ConnectionParameter conParam = (ConnectionParameter)connectionParameters.elementAt(i);
						if(conParam.getConnectionType() == TransportLayer.CON_TYPE_HTTP)
							conParam.setValue(((TextField)((Form)d).get(i+3)).getString());
					}
				}
			}
			defTransLayer.onConnectionSettingsClosed(true);
			if(this.eventHandler!=null)
				this.eventHandler.connectionSettingsClosed();			
		}
		else{
			conType = fromIndexToConType(((List)d).getSelectedIndex());
			showConParams();
		}
	}

	/**
	 * Adds selected transport-protocol to the URL, if it is not present in the URL-string.
	 * 
	 * @param url
	 * @param protocol
	 * @return
	 */
	protected String addHTTPtoUrl(String url, String protocol) {
		if (!isHttpServerProtocol(url) && !isHttpsServerProtocol(url)) {
			return protocol + "://" + url;
		}
		return url;
	}

	/**
	 * Adds the SERVLET_MAPPING if it is not present in the URL
	 * 
	 * @param url
	 * @return
	 */
	protected String addServletMappingToUrl(String url) {
		boolean containsMapping = url.toLowerCase().indexOf(SERVLET_MAPPING) != -1;
		if (!containsMapping) {
			if (url.length()>0 && url.charAt(url.length() - 1) != '/')
				url += "/";
			url += SERVLET_MAPPING;
		}
		return url;
	}
	
	private void showConParams(){
		currentAction = CA_CON_PARAMS;
		
		if(conType == TransportLayer.CON_TYPE_HTTP)
			showHttpConParams();
		else
			handleOkCommand(null);
	}
	
	private Properties getDefaults() {
		Properties p = new Properties();
		try {
			p.load(ConnectionSettings.class
					.getResourceAsStream("/defaults.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	private void showHttpConParams(){
		if (isAdvancedViewSelected()) {
			display.setCurrent(createAdvancedForm());
		} else {
			display.setCurrent(createDefaultForm());
		}
	}

	private boolean isAdvancedViewSelected() {
		return advancedOptionsCheckbox.isSelected(0);
	}

	private Form createDefaultForm() {
		Form form = null;
		if (display.getCurrent() != null && display.getCurrent() instanceof Form) {
			form = (Form)display.getCurrent();
			form.deleteAll();
		} else {
			form = new Form(this.title);
		}

		// get http/https from the current URL
		if (isHttpsServerProtocol(serverURL)) {
			hTTPSelectionBox.setSelectedIndex(1, true);
		} else {
			hTTPSelectionBox.setSelectedIndex(0, true);
		}
		form.append(defaultUrlHeading);
		form.append(hTTPSelectionBox);
		// get the host part from the current URL
		defaultUrlTextField.setString(getServerName(serverURL));
		form.append(defaultUrlTextField);
		// add checkbox for simple/advanced view
		form.append(advancedOptionsCheckbox);
		
		form.setItemStateListener(createChoiceGroupListener());
		setupCommands(form);

		return form;
	}

	private Displayable createAdvancedForm() {
		Form form = (Form)display.getCurrent();
		form.deleteAll();
		advancedUrlTextField.setString(serverURL);
		form.append(advancedUrlTextField);
		form.append(advancedOptionsCheckbox);
		form.setItemStateListener(createChoiceGroupListener());
		setupCommands(form);
		return form;
	}

	private ItemStateListener createChoiceGroupListener() {
		ItemStateListener listener = new ItemStateListener() {
			public void itemStateChanged(Item item) {
				if (item == advancedOptionsCheckbox) {
					showHttpConParams();
				} else {
					if (isAdvancedViewSelected()) {
						serverURL = advancedUrlTextField.getString();
					} else {
						String protocol = hTTPSelectionBox.getString(hTTPSelectionBox.getSelectedIndex()).toLowerCase();
						String server = ((TextField) defaultUrlTextField).getString();
						serverURL = addHTTPtoUrl(addServletMappingToUrl(server), protocol);
					}
				}
			}
		};
		return listener;
	}

	private void setupCommands(Form frm) {
		frm.addCommand(DefaultCommands.cmdCancel);
		frm.addCommand(DefaultCommands.cmdOk);
		frm.setCommandListener(this);
	}
	
	public byte getConType(){
		return conType;
	}
	
	public Hashtable getConParams(){
		return conParams;
	}
	
	public Vector getConnectionParameters(){
		return connectionParameters;
	}

	/**
	 * Extracts the server-name (or IP) from a URL
	 * 
	 * @param url
	 * @return The server
	 */
	protected String getServerName(String url) {
		int start = 0;
		if (isHttpServerProtocol(url)) {
			start = 7; // http://
		} else if (isHttpsServerProtocol(url)) {
			start = 8; // https://
		}
		return url.substring(start, findServerAddressEnd(start, url));
	}

	private int findServerAddressEnd(int start, String url) {
		int indexOfServletMapping = url.indexOf(SERVLET_MAPPING); 
		if (indexOfServletMapping != -1) {
			// there is a servlet mapping to extract
			for (int i=indexOfServletMapping; i>start; i--) {
				if (url.charAt(i) == '/') {
					return i;
				}
			}
			return indexOfServletMapping;
		}
		// no servlet mapping, so assume rest of url is part of server address
		return url.length();
	}

	protected boolean isHttpServerProtocol(String url) {
		if (url != null && !url.trim().equals("")) {
			if (url.toLowerCase().indexOf("http") != -1 && url.toLowerCase().indexOf("https") == -1)
				return true;
		}
		return false;
	}
	
	protected boolean isHttpsServerProtocol(String url) {
		if (url != null && !url.trim().equals("")) {
			if (url.toLowerCase().indexOf("https") != -1)
				return true;
		}
		return false;
	}
}
