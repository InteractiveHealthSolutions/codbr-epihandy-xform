package org.ihs.xform;


public class StringUtils {

	public static boolean equalsIgnoreCase(String string1, String string2){
		if(string1.toLowerCase().compareTo(string2.toLowerCase()) == 0){
			return true;
		}
		return false;
	}
	
	public static int lastIndexOf(String stringIn, String stringIndex) {
		int lind = -1;
		if((lind=stringIn.indexOf(stringIndex)) == -1){
			return -1;
		}
		
		int ind = lind;
		while (ind != -1) {
			ind = stringIn.indexOf(stringIndex, lind+1);
			if(ind != -1){
				lind = ind;
			}
		}
		return lind;
	}
	
	public static void main(String[] args) {
		System.out.println(rightPad("123", 12, '0'));
	}
	
	public static String replaceAll (String oldStr, String newStr, String inString)
	{
		while (inString.indexOf (oldStr) != -1)
			inString = replace (oldStr, newStr, inString);
		return inString;
	}

	public static String replace (String oldStr, String newStr, String inString)
	{
		int start = inString.indexOf (oldStr);
		if (start == -1)
		{
			return inString;
		}
		StringBuffer sb = new StringBuffer ();
		sb.append (inString.substring (0, start));
		sb.append (newStr);
		sb.append (inString.substring (start + oldStr.length ()));
		return sb.toString ();
	}
	
	/* 
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 *  contributor license agreements.  See the NOTICE file distributed with
	 *  this work for additional information regarding copyright ownership.
	 *  The ASF licenses this file to You under the Apache License, Version 2.0
	 *  (the "License"); you may not use this file except in compliance with
	 *  the License.  You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 *  Unless required by applicable law or agreed to in writing, software
	 *  distributed under the License is distributed on an "AS IS" BASIS,
	 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 *  See the License for the specific language governing permissions and
	 *  limitations under the License.
	 *
	 *
	 */
	  /**
	   * <p>The maximum size to which the padding constant(s) can expand.</p>
	   */
	  private static final int PAD_LIMIT = 8192;

	  /**
	   * <p>Left pad a String with a specified character.</p>
	   *
	   * <p>Pad to a size of <code>size</code>.</p>
	   *
	   * <pre>
	   * StringUtils.leftPad(null, *, *)     = null
	   * StringUtils.leftPad("", 3, 'z')     = "zzz"
	   * StringUtils.leftPad("bat", 3, 'z')  = "bat"
	   * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
	   * StringUtils.leftPad("bat", 1, 'z')  = "bat"
	   * StringUtils.leftPad("bat", -1, 'z') = "bat"
	   * </pre>
	   *
	   * @param str  the String to pad out, may be null
	   * @param size  the size to pad to
	   * @param padChar  the character to pad with
	   * @return left padded String or original String if no padding is necessary,
	   *  <code>null</code> if null String input
	   * @since 2.0
	   */
	  public static String leftPad(String str, int size, char padChar) {
	      if (str == null) {
	          return null;
	      }
	      int pads = size - str.length();
	      if (pads <= 0) {
	          return str; // returns original String when possible
	      }

	      return padding(pads, padChar).concat(str);
	  }

	  /**
	   * <p>Left pad a String with a specified String.</p>
	   *
	   * <p>Pad to a size of <code>size</code>.</p>
	   *
	   * <pre>
	   * StringUtils.leftPad(null, *, *)      = null
	   * StringUtils.leftPad("", 3, "z")      = "zzz"
	   * StringUtils.leftPad("bat", 3, "yz")  = "bat"
	   * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
	   * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
	   * StringUtils.leftPad("bat", 1, "yz")  = "bat"
	   * StringUtils.leftPad("bat", -1, "yz") = "bat"
	   * StringUtils.leftPad("bat", 5, null)  = "  bat"
	   * StringUtils.leftPad("bat", 5, "")    = "  bat"
	   * </pre>
	   *
	   * @param str  the String to pad out, may be null
	   * @param size  the size to pad to
	   * @param padStr  the String to pad with, null or empty treated as single space
	   * @return left padded String or original String if no padding is necessary,
	   *  <code>null</code> if null String input
	   */
	  public static String leftPad(String str, int size, String padStr) {
	      if (str == null) {
	          return null;
	      }
	      if (isEmpty(padStr)) {
	          padStr = " ";
	      }
	      int padLen = padStr.length();
	      int strLen = str.length();
	      int pads = size - strLen;
	      if (pads <= 0) {
	          return str; // returns original String when possible
	      }
	      if (padLen == 1 && pads <= PAD_LIMIT) {
	          return leftPad(str, size, padStr.charAt(0));
	      }

	      if (pads == padLen) {
	          return padStr.concat(str);
	      } else if (pads < padLen) {
	          return padStr.substring(0, pads).concat(str);
	      } else {
	          char[] padding = new char[pads];
	          char[] padChars = padStr.toCharArray();
	          for (int i = 0; i < pads; i++) {
	              padding[i] = padChars[i % padLen];
	          }
	          return new String(padding).concat(str);
	      }
	  }
	  /**
	   * <p>Returns padding using the specified delimiter repeated
	   * to a given length.</p>
	   *
	   * <pre>
	   * StringUtils.padding(0, 'e')  = ""
	   * StringUtils.padding(3, 'e')  = "eee"
	   * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
	   * </pre>
	   *
	   * <p>Note: this method doesn't not support padding with
	   * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
	   * as they require a pair of <code>char</code>s to be represented.
	   * If you are needing to support full I18N of your applications
	   * consider using {@link #repeat(String, int)} instead. 
	   * </p>
	   *
	   * @param repeat  number of times to repeat delim
	   * @param padChar  character to repeat
	   * @return String with repeated character
	   * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
	   * @see #repeat(String, int)
	   */
	  private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
	      if (repeat < 0) {
	          throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
	      }
	      final char[] buf = new char[repeat];
	      for (int i = 0; i < buf.length; i++) {
	          buf[i] = padChar;
	      }
	      return new String(buf);
	  }
	  
	  public static String rightPad(String str, int size, char padChar) {
	      if (str == null) {
	          return null;
	      }
	      int pads = size - str.length();
	      if (pads <= 0) {
	          return str; // returns original String when possible
	      }

	      return str.concat(padding(pads, padChar));
	  }
	  
	  // Empty checks
	  //-----------------------------------------------------------------------
	  /**
	   * <p>Checks if a String is empty ("") or null.</p>
	   *
	   * <pre>
	   * StringUtils.isEmpty(null)      = true
	   * StringUtils.isEmpty("")        = true
	   * StringUtils.isEmpty(" ")       = false
	   * StringUtils.isEmpty("bob")     = false
	   * StringUtils.isEmpty("  bob  ") = false
	   * </pre>
	   *
	   * <p>NOTE: This method changed in Lang version 2.0.
	   * It no longer trims the String.
	   * That functionality is available in isBlank().</p>
	   *
	   * @param str  the String to check, may be null
	   * @return <code>true</code> if the String is empty or null
	   */
	  public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	  }
}
