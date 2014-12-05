package org.ihs.xform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fcitmuk.db.util.Persistent;

public class Xform implements Persistent{

	private int formId;
	private String formName;
	
	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getFormId());
		dos.writeUTF(getFormName());
	}

	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		formId = dis.readInt();
		formName = dis.readUTF();
	}

}
