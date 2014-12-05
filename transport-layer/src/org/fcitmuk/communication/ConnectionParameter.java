package org.fcitmuk.communication;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Creates a wrapper to maintain connection parameters.
 * @author Maimoona
 *
 */
public class ConnectionParameter {
	private Hashtable params = new Hashtable();
	
	public ConnectionParameter() {	}

	public ConnectionParameter(String name, String value) {
		addParam(name, value);
	}

	public void addParam(String name, String value) {
		params.put(name, value);
	}

	/**
	 * Makes a string out of all connection parameters.
	 * ex: name1=value1&name2=value2&name3=value3 (& is pair separator)
	 * @param pairSeparator
	 * @return
	 */
	public String getAllParams(String pairSeparator) {
		String paramstr = "";
		
		Enumeration el = params.keys(); 
		while (el.hasMoreElements()) {
			String key = (String) el.nextElement();
			//if paramstr is NOT empty (this is NOT first key-val pair) then append key-val separator before pair
			paramstr += (paramstr==""?"":pairSeparator)+(key+"="+(String) params.get(key));
		}
		return paramstr;
	}
}
