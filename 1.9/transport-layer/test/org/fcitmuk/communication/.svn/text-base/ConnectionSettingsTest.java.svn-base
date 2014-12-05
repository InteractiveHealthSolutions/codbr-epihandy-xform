package org.fcitmuk.communication;


import junit.framework.TestCase;

import org.junit.Assert;

public class ConnectionSettingsTest  extends TestCase {
	ConnectionSettings conSettings = null;

	public void setUp() {
		conSettings = new ConnectionSettings();
	}

	public void testAddServletMappingToUrl() {
		String urlWithoutMPSUBMIT = "http://127.0.0.1:8889/";
		Assert.assertEquals(urlWithoutMPSUBMIT+"mpsubmit", conSettings.addServletMappingToUrl(urlWithoutMPSUBMIT));
	}
	
	public void testAddServletMappingToUrlNoBackslash() {
		String urlWithoutMPSUBMIT = "http://127.0.0.1:8889";
		Assert.assertEquals(urlWithoutMPSUBMIT+"/mpsubmit", conSettings.addServletMappingToUrl(urlWithoutMPSUBMIT));
	}

	public void testAddServletMappingToUrlLeftUnchanged() {
		String url = "http://127.0.0.1:8889/mpsubmit";
		Assert.assertEquals(url, conSettings.addServletMappingToUrl(url));
	}
	
	public void testAddServletMappingToUrlLeftUnchangedExtraBackslash() {
		String url = "http://127.0.0.1:8889/mpsubmit/";
		Assert.assertEquals(url, conSettings.addServletMappingToUrl(url));
	}
	
	public void testAddServletMappingToUrlWithNamedWebapp() {
		String url = "http://127.0.0.1:8889/openxdata";
		Assert.assertEquals(url+"/mpsubmit", conSettings.addServletMappingToUrl(url));
	}
	
	public void testAddServletMappingToUrlWithNamedWebappSlash() {
		String url = "http://127.0.0.1:8889/openxdata/";
		Assert.assertEquals(url+"mpsubmit", conSettings.addServletMappingToUrl(url));
	}
	
	public void testAddHTTPToUrlDoesNothing() {
		String url = "http://127.0.0.1:8889/mpsubmit";
		Assert.assertEquals(url, conSettings.addHTTPtoUrl(url, "http"));
	}
	
	public void testAddHTTPToUrlDoesNothingHttps() {
		String url = "https://127.0.0.1:8889/mpsubmit";
		Assert.assertEquals(url, conSettings.addHTTPtoUrl(url, "https"));
	}
	
	public void testAddHTTPToUrlHttp() {
		String url = "127.0.0.1:8889/mpsubmit";
		Assert.assertEquals("http://"+url, conSettings.addHTTPtoUrl(url, "http"));
	}
	
	public void testAddHTTPToUrlHttps() {
		String url = "127.0.0.1:8889/mpsubmit";
		Assert.assertEquals("https://"+url, conSettings.addHTTPtoUrl(url, "https"));
	}

	public void testGetServerNameIPShouldStripHTTPAndOther() {
		String url = "http://127.0.0.1:8889/mpsubmit";
		Assert.assertEquals("127.0.0.1:8889", conSettings.getServerName(url));
	}
	
	public void testGetServerNameLocalhostShouldStripHTTPAndOther() {
		String url = "http://localhost:8080/mpsubmit";
		Assert.assertEquals("localhost:8080", conSettings.getServerName(url));
	}
	
	public void testGetServerNameLocalhostOpenXdataShouldStripHTTPAndOther() {
		String url = "http://localhost:8080/openxdata/mpsubmit";
		Assert.assertEquals("localhost:8080/openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameLocalhostOpenXdataShouldStripHTTPSAndOther() {
		String url = "https://localhost:8080/openxdata/mpsubmit";
		Assert.assertEquals("localhost:8080/openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameFinalSlash() {
		String url = "https://localhost:8080/openxdata/mpsubmit/";
		Assert.assertEquals("localhost:8080/openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameNoMapping() {
		String url = "https://localhost:8080/openxdata";
		Assert.assertEquals("localhost:8080/openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameNoMappingFinalSlash() {
		String url = "https://localhost:8080/openxdata/";
		Assert.assertEquals("localhost:8080/openxdata/", conSettings.getServerName(url));
	}
	
	public void testGetServerNameFTP() {
		String url = "ftp://localhost:8080/openxdata/mpsubmit";
		Assert.assertEquals("ftp://localhost:8080/openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameFaultyURL() {
		String url = "mpsubmit";
		Assert.assertEquals("", conSettings.getServerName(url));
	}
	
	public void testGetServerNameFaultyURL2() {
		String url = "openxdata/mpsubmit";
		Assert.assertEquals("openxdata", conSettings.getServerName(url));
	}
	
	public void testGetServerNameFaultyURL3() {
		String url = "http:openxdata/mpsubmit";
		Assert.assertEquals("enxdata", conSettings.getServerName(url));
	}

	public void testGetServerIPShouldHaveOnlyServerIP() {
		String server = "127.0.0.1:8889";
		String urlWithoutHTTP = "127.0.0.1:8889/mpsubmit";
		Assert.assertEquals(server, conSettings.getServerName(urlWithoutHTTP));
	}
	
	public void testGetServerAddressShouldContainWebappName() {
		String server = "dev.openxdata.org/test";
		String urlWithoutHTTP = "dev.openxdata.org/test/mpsubmit";
		Assert.assertEquals(server, conSettings.getServerName(urlWithoutHTTP));
	}
	
	public void testIsHttpServerProtocolHttp() {
		String url = "http://127.0.0.1:8889/mpsubmit";
		Assert.assertTrue("http", conSettings.isHttpServerProtocol(url));
	}
	
	public void testIsHttpServerProtocolHttps() {
		String url = "https://127.0.0.1:8889/mpsubmit";
		Assert.assertFalse("https", conSettings.isHttpServerProtocol(url));
	}
	
	public void testIsHttpsServerProtocolHttps() {
		String url = "https://127.0.0.1:8889/mpsubmit";
		Assert.assertTrue("https", conSettings.isHttpsServerProtocol(url));
	}
	
	public void testIsHttpsServerProtocolHttp() {
		String url = "http://127.0.0.1:8889/mpsubmit";
		Assert.assertFalse("http", conSettings.isHttpsServerProtocol(url));
	}
}
