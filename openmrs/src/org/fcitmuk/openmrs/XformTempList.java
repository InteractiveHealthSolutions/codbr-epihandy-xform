package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.FormDef;
import org.ihs.xform.EpihandyXform;
import org.ihs.xform.StringReader;
import org.ihs.xform.Xform;
import org.ihs.xform.XformXml;

public class XformTempList implements Persistent{
	private Vector forms = new Vector();
	
	public Vector getForms() {
		return forms;
	}

	public void setForms(Vector forms) {
		this.forms = forms;
	}

	public void write(DataOutputStream dos) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void read(DataInputStream dis) throws IOException, InstantiationException, IllegalAccessException {
		byte len = dis.readByte();
		for (int i = 0; i < len; i++) {
			String xml = dis.readUTF();
			Xform xform = new Xform();
			FormDef fd = EpihandyXform.fromXform2FormDef(new StringReader(xml));
			xform.setFormId(fd.getId());
			xform.setFormName(fd.getName());
			XformXml xfxml = new XformXml();
			xfxml.setFormId(fd.getId());
			xfxml.setFormXml(xml);
			
			Hashtable fht = new Hashtable();
			fht.put(xform, xfxml);
			forms.addElement(fht);
		}
	}
}
