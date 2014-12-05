package org.fcitmuk.openmrs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.fcitmuk.db.util.Persistent;
public class UploadResponseList implements Persistent{
	Hashtable responseList = new Hashtable();

	public UploadResponseList(){
		
	}
	
	public Hashtable getResponseList() {
		return responseList;
	}

	public void setResponseList(Hashtable responseList) {
		this.responseList = responseList;
	}
	
	public void read(DataInputStream dis) throws IOException {
		byte len = dis.readByte();
		
		for (int i = 0; i < len; i++) {
			responseList.put(dis.readUTF(), dis.readUTF());
		}
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeByte(responseList.size());
		
		Enumeration keys = responseList.keys();
		while (keys.hasMoreElements()) {
			Object object = (Object) keys.nextElement();
			
			dos.writeUTF((String) object);
			dos.writeUTF((String) responseList.get(object));
		}
	}
}
