package com.secureops.fieldextraction;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class TestLoading {

	@Test
	public void testLoadMain() throws ConfigurationException {
		FieldExtractorConfigLoader.loadConf("fieldextraction.properties");
	}
	
	@Test (expected=ConfigurationException.class)
	public void testBadFile() throws ConfigurationException {
		FieldExtractorConfigLoader.loadConf("bob.properties");
	}
	
	@Test (expected=ConfigurationException.class)
	public void testBadInclude() throws ConfigurationException {
		FieldExtractorConfigLoader.loadConf("broken.properties");
	}

}
