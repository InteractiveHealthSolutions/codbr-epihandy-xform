package org.fcitmuk.openmrs.util;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.fcitmuk.openmrs.User;

public class SecurityUtils {
	public static boolean authenticate(User user, String password){
		String hashedPassword = encodeString(password + user.getSalt());
		return (hashedPassword != null && hashedPassword.equals(user.getPassword()));
	}
	
	 /**
     * @param string to encode
     * @return the SHA-1 encryption of a given string
     */
	private static String encodeString(String strToEncode) {    	
    	SHA512Digest digEng = new SHA512Digest();
    	
  		byte[] input = strToEncode.getBytes(); //TODO: pick a specific character encoding, don't rely on the platform default
  		digEng.update(input, 0, input.length);
  		
  		byte[] digest = new byte[digEng.getDigestSize()];
  		digEng.doFinal(digest, 0);
  		
		return hexString(digest);
    }
	
/*	 public static String encodeString(String strToEncode) {
		 byte[] input = strToEncode.getBytes();
		 MD5 md5 = new MD5(input);
		 md5.update(input);
		 byte[] digest = md5.doFinal();
		return hexString(md5.toHex(digest).getBytes());
	 }*/
    
	 /**
     * @param Byte array to convert to HexString
     * @return Hexidecimal based string
     */
    
	private static String hexString(byte[] b) {
		StringBuffer buf = new StringBuffer();
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		int len = b.length;
		int high = 0;
		int low = 0;
		for (int i = 0; i < len; i++) {
			high = ((b[i] & 0xf0) >> 4);
			low = (b[i] & 0x0f);
			buf.append(hexChars[high]);
			buf.append(hexChars[low]);
		}
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		User u = new User();
		u.setName("admin");
		u.setPassword("03ec79b1f46f861ffb915f647dbc12c113aff5fbfc9e808401d9031aa58fb98c1ec0c5e3da8d442b40dbb66f15f6729ea85a8db87a613e568000100f0cd1389e");
		u.setSalt("082a1aa6c2fa7bcf76b83714a8f36a6f123008b6cbb39ef2450904ad6f258f3d137949baa944736f24a319db2a71b9c269ce072469563081d50e2b8c49866515");
		System.out.println(authenticate(u, "Admin123"));
	}
}
