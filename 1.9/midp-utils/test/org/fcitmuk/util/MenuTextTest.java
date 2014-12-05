package org.fcitmuk.util;

import junit.framework.TestCase;

public class MenuTextTest extends TestCase {
	
	public void testGetTextDoesNotReturnNull(){
		
		assertEquals("Last", MenuText.LAST());
	}
	
	public void testGetTextReturnsCorrectValueGivenKey(){
		
		assertEquals("Exit", MenuText.EXIT());
		assertEquals("Login", MenuText.LOGIN());
	}
}
