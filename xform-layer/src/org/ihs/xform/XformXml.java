package org.ihs.xform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;

public class XformXml implements Persistent{

	private int formId;
	private String formXml;
	
	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getFormXml() {
		return formXml;
	}

	public void setFormXml(String formXml) {
		this.formXml = formXml;
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getFormId());
		dos.writeUTF(getFormXml());
	}

	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		formId = dis.readInt();
		formXml = dis.readUTF();
	}

}
