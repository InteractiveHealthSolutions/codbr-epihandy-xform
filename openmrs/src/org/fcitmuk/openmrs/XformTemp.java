package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;

public class XformTemp implements Persistent{

	private String formXml;
	
	public String getFormXml() {
		return formXml;
	}

	public void setFormXml(String formXml) {
		this.formXml = formXml;
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeUTF(getFormXml());
	}

	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		StringBuffer sb = new StringBuffer();
		int c;
		while ((c = dis.read()) != -1) {
			sb.append((char) c);
		}
		formXml = sb.toString();
	}

}
