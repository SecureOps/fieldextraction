package com.secureops.fieldextraction;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TestConfigUtils {
	private static final int HTTP_PORT = 8099;
	private static final String TEST_BODY = "Hello World";
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(HTTP_PORT);
	
	@Test
	public void testCreateFileFromInputStream() {
		//NO OP
	}


	@Test
	public void testGetTextFileAsFile() throws IOException, URISyntaxException {
		URL testR = this.getClass().getClassLoader().getResource("testresource.txt");
		String testS = ConfigUtils.getTextFileContent(testR.getPath());
		assertNotNull(testS);
	}

	@Test
	public void testGetTextFileAsResource() throws IOException, URISyntaxException {		
		assertNotNull(ConfigUtils.getTextFileContent("testresource.txt"));
	}
	
	@Test
	public void testGetTextFileAsURL() throws IOException, URISyntaxException {
		stubFor(get(urlEqualTo("/some/thing"))
	            .willReturn(aResponse()
	                .withHeader("Content-Type", "text/plain")
	                .withBody(TEST_BODY)));
		String testResp = ConfigUtils.getTextFileContent("http://localhost:" + HTTP_PORT + "/some/thing");
		assertTrue(TEST_BODY.equalsIgnoreCase(testResp));
	}
	
	@Test (expected=Exception.class)
	public void testGetTextFileNullTest() throws IOException, URISyntaxException {		
		assertNotNull(ConfigUtils.getTextFileContent(null));
	}
	
	@Test (expected=Exception.class)
	public void testGetTextFileNotFoundTest() throws IOException, URISyntaxException {		
		assertNotNull(ConfigUtils.getTextFileContent("bob.txt"));
	}
	
	
	
	@Test
	public void testTopParent() {
		// NO OP
	}
	
	@Test
	public void testJoinStrings() {
		// NO OP
	}

}
